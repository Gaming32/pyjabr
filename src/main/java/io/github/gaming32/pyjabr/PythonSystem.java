package io.github.gaming32.pyjabr;

import io.github.gaming32.pyjabr.lowlevel.interop.InteropModule;
import io.github.gaming32.pyjabr.lowlevel.module.CustomPythonModule;
import io.github.gaming32.pyjabr.run.PythonRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.*;

public class PythonSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger(PythonSystem.class);

    private static final ReentrantLock STATE_LOCK = new ReentrantLock();
    private static final Condition STATE_COND = STATE_LOCK.newCondition();

    private static final AtomicInteger INIT_STATE = new AtomicInteger();
    private static final AtomicBoolean INITIALIZED_ONCE = new AtomicBoolean();

    private static final int STATE_SHUTDOWN = 0;
    private static final int STATE_INITIALIZING = 1;
    private static final int STATE_INITIALIZED = 2;
    private static final int STATE_FINALIZING = 3;
    private static final int STATE_ERROR = -1;

    private static volatile Thread managementThread;
    private static volatile Throwable initError;

    public static void initialize() {
        if (!INIT_STATE.compareAndSet(STATE_SHUTDOWN, STATE_INITIALIZING)) {
            while (true) {
                STATE_LOCK.lock();
                try {
                    final int state = INIT_STATE.get();
                    if (state == STATE_INITIALIZED) return;
                    if (state == STATE_SHUTDOWN) {
                        if (!INIT_STATE.compareAndSet(STATE_SHUTDOWN, STATE_INITIALIZING)) {
                            throw new IllegalStateException("Failed to advance state to STATE_INITIALIZING");
                        }
                        break;
                    }
                    if (Thread.currentThread() == managementThread) return;
                    if (state == STATE_ERROR) {
                        throw rethrow(initError);
                    }
                    STATE_COND.awaitUninterruptibly();
                } finally {
                    STATE_LOCK.unlock();
                }
            }
        }
        PythonVersion.checkAndLog();
        final boolean firstInitialize = INITIALIZED_ONCE.compareAndSet(false, true);
        LOGGER.debug("Initializing Python (first time: {})", firstInitialize);

        Thread.ofPlatform()
            .name("Python Management Thread")
            .uncaughtExceptionHandler((_, t) -> {
                LOGGER.error("Unexpected error initializing Python", t);
                initError = t;
                INIT_STATE.set(STATE_ERROR);
                signalState();
            })
            .daemon()
            .start(() -> {
                managementThread = Thread.currentThread();
                MemorySegment dlHandle = null;

                try {
                    if (!System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")) {
                        final int RTLD_LAZY = 0x00001;
                        final int RTLD_GLOBAL = 0x00100;
                        dlHandle = Dlopen.dlopen(System.mapLibraryName("python3"), RTLD_LAZY | RTLD_GLOBAL);
                    }

                    if (firstInitialize) {
                        try {
                            CustomPythonModule.fromClass(InteropModule.class).registerAsBuiltin(Arena.global());
                        } catch (IllegalAccessException e) {
                            throw new IllegalStateException(e);
                        }
                    }

                    Py_InitializeEx(0);
                    try (Arena arena = Arena.ofConfined()) {
                        if (
                            PyImport_ImportModuleLevel(
                                arena.allocateFrom("threading"),
                                MemorySegment.NULL,
                                MemorySegment.NULL,
                                MemorySegment.NULL,
                                0
                            ).equals(MemorySegment.NULL)
                        ) {
                            PyErr_Clear();
                        }
                    }
                    try {
                        PythonRun.runResource("java_api.py", "java_api");
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    final MemorySegment save = PyEval_SaveThread();

                    if (!INIT_STATE.compareAndSet(STATE_INITIALIZING, STATE_INITIALIZED)) {
                        throw new IllegalStateException("Failed to advance state to STATE_INITIALIZED");
                    }
                    signalState();

                    waitUninterruptiblyForState(STATE_FINALIZING);
                    PyEval_RestoreThread(save);
                    Py_Finalize();
                } finally {
                    if (dlHandle != null) {
                        Dlopen.dlclose(dlHandle);
                    }
                    managementThread = null;
                }

                if (!INIT_STATE.compareAndSet(STATE_FINALIZING, STATE_SHUTDOWN)) {
                    throw new IllegalStateException("Failed to advance state to STATE_SHUTDOWN");
                }
                signalState();
            });

        waitUninterruptiblyForInitialize();
        if (firstInitialize) {
            Runtime.getRuntime().addShutdownHook(
                Thread.ofPlatform()
                    .name("Python Shutdown Thread")
                    .uncaughtExceptionHandler((_, t) -> LOGGER.error("Unexpected error finalizing Python", t))
                    .unstarted(PythonSystem::shutdown)
            );
        }
    }

    public static void shutdown() {
        if (!INIT_STATE.compareAndSet(STATE_INITIALIZED, STATE_FINALIZING)) {
            while (true) {
                STATE_LOCK.lock();
                try {
                    final int state = INIT_STATE.get();
                    if (state == STATE_SHUTDOWN) return;
                    if (state == STATE_INITIALIZED) {
                        if (!INIT_STATE.compareAndSet(STATE_INITIALIZED, STATE_FINALIZING)) {
                            throw new IllegalStateException("Failed to advance state to STATE_FINALIZING");
                        }
                        break;
                    }
                    if (state == STATE_ERROR) {
                        throw rethrow(initError);
                    }
                    STATE_COND.awaitUninterruptibly();
                } finally {
                    STATE_LOCK.unlock();
                }
            }
            return;
        }

        LOGGER.debug("Finalizing Python");
        signalState();
        waitUninterruptiblyForShutdown();
    }

    /**
     * Gets whether Python is initialized.
     * @return A best-effort estimate if Python is initialized at this moment.
     */
    public static boolean isInitialized() {
        return INIT_STATE.get() == STATE_INITIALIZED;
    }

    public static void waitForInitialize() throws InterruptedException {
        waitForState(STATE_INITIALIZED);
    }

    public static void waitForShutdown() throws InterruptedException {
        waitForState(STATE_SHUTDOWN);
    }

    private static void waitForState(int state) throws InterruptedException {
        while (true) {
            STATE_LOCK.lock();
            try {
                final int check = INIT_STATE.get();
                if (check == state) break;
                if (check == STATE_ERROR) {
                    throw rethrow(initError);
                }
                STATE_COND.await();
            } finally {
                STATE_LOCK.unlock();
            }
        }
    }

    public static void waitUninterruptiblyForInitialize() {
        waitUninterruptiblyForState(STATE_INITIALIZED);
    }

    public static void waitUninterruptiblyForShutdown() {
        waitUninterruptiblyForState(STATE_SHUTDOWN);
    }

    private static void waitUninterruptiblyForState(int state) {
        while (true) {
            STATE_LOCK.lock();
            try {
                final int check = INIT_STATE.get();
                if (check == state) break;
                if (check == STATE_ERROR) {
                    throw rethrow(initError);
                }
                STATE_COND.awaitUninterruptibly();
            } finally {
                STATE_LOCK.unlock();
            }
        }
    }

    private static void signalState() {
        STATE_LOCK.lock();
        try {
            STATE_COND.signalAll();
        } finally {
            STATE_LOCK.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T rethrow(Throwable t) throws T {
        throw (T)t;
    }
}
