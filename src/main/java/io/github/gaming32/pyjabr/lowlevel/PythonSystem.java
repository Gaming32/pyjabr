package io.github.gaming32.pyjabr.lowlevel;

import io.github.gaming32.pyjabr.PythonRun;
import io.github.gaming32.pyjabr.PythonVersion;
import io.github.gaming32.pyjabr.interop.InteropModule;
import io.github.gaming32.pyjabr.module.CustomPythonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

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

    private static final AtomicReference<Thread> MANAGEMENT_THREAD = new AtomicReference<>();

    public static void callPython(Runnable action) {
        initialize();
        final int state = PyGILState_Ensure();
        try {
            action.run();
        } finally {
            PyGILState_Release(state);
        }
    }

    public static <T> T callPython(Supplier<T> action) {
        initialize();
        final int state = PyGILState_Ensure();
        try {
            return action.get();
        } finally {
            PyGILState_Release(state);
        }
    }

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
                    if (Thread.currentThread() == MANAGEMENT_THREAD.get()) return;
                    STATE_COND.awaitUninterruptibly();
                } finally {
                    STATE_LOCK.unlock();
                }
            }
        }
        PythonVersion.checkAndLog();
        final boolean firstInitialize = INITIALIZED_ONCE.compareAndSet(false, true);

        final Thread managementThread = Thread.ofPlatform()
            .name("Python Management Thread")
            .uncaughtExceptionHandler((_, t) -> LOGGER.error("Unexpected error initializing Python", t))
            .daemon()
            .start(() -> {
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

                MANAGEMENT_THREAD.set(null);

                if (!INIT_STATE.compareAndSet(STATE_FINALIZING, STATE_SHUTDOWN)) {
                    throw new IllegalStateException("Failed to advance state to STATE_SHUTDOWN");
                }
                signalState();
            });
        MANAGEMENT_THREAD.set(managementThread);

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
                    STATE_COND.awaitUninterruptibly();
                } finally {
                    STATE_LOCK.unlock();
                }
            }
            return;
        }

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
                if (INIT_STATE.get() == state) break;
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
                if (INIT_STATE.get() == state) break;
                STATE_COND.awaitUninterruptibly();
            } finally {
                STATE_LOCK.unlock();
            }
        }
    }

    private static void signalState() {
        STATE_LOCK.lock();
        try {
            STATE_COND.signal();
        } finally {
            STATE_LOCK.unlock();
        }
    }
}
