package io.github.gaming32.pyjabr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.PY_VERSION_HEX;
import static io.github.gaming32.pyjabr.lowlevel.cpython.Python_h.Py_Version;

public class PythonVersion {
    private static final Logger LOGGER = LoggerFactory.getLogger(PythonVersion.class);
    private static final String[] LEVEL_NAMES = new String[0xF];
    private static final int TARGET_VERSION = 0x030c00f0;

    private static boolean versionChecked = false;

    static {
        LEVEL_NAMES[0xA] = "a";
        LEVEL_NAMES[0xB] = "b";
        LEVEL_NAMES[0xC] = "rc";
    }

    public static synchronized void checkAndLog() {
        if (versionChecked) return;
        versionChecked = true;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Current Python version: {}", formatVersion(getCurrentVersion()));
            LOGGER.debug("Targeted Python version: {}", formatVersion(getTargetVersion()));
            LOGGER.debug("Python version compiled against: {}", formatVersion(getCompiledAgainstVersion()));
        }
        if (getCurrentVersion() < getTargetVersion()) {
            LOGGER.warn(
                "Loaded Python version ({}) is less than the version pyjabr is targeting ({}). Issues may arise.",
                formatVersion(getCurrentVersion()), formatVersion(getTargetVersion())
            );
        }
    }

    public static int getCurrentVersion() {
        return Py_Version();
    }

    public static int getTargetVersion() {
        return TARGET_VERSION;
    }

    public static int getCompiledAgainstVersion() {
        return PY_VERSION_HEX();
    }

    public static String formatVersion(int version) {
        final int major = version >>> 24;
        final int minor = version >> 16 & 0xff;
        final int micro = version >> 8 & 0xff;
        final int level = version >> 4 & 0xf;
        final int serial = version & 0xf;

        final StringBuilder result = new StringBuilder();
        result.append(major)
            .append('.').append(minor)
            .append('.').append(micro);
        if (level != 0xF) {
            result.append(LEVEL_NAMES[level]).append(serial);
        }
        return result.toString();
    }
}
