package io.github.gaming32.pyjabr.lowlevel;

import io.github.gaming32.pyjabr.PythonVersion;

import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;

public class CPythonFinder {
    private static volatile String foundName;

    public static SymbolLookup findCPython(Arena arena) {
        if (foundName != null) {
            return SymbolLookup.libraryLookup(foundName, arena);
        }
        final int targetVersion = PythonVersion.getMinor(PythonVersion.getTargetVersion());
        for (int tryVersion = targetVersion; tryVersion < targetVersion + 10; tryVersion++) {
            final SymbolLookup result = tryName("python3." + tryVersion, arena);
            if (result != null) {
                return result;
            }
        }
        final SymbolLookup result = tryName("python3", arena);
        if (result != null) {
            return result;
        }
        throw new IllegalStateException("Could not Python 3 installation");
    }

    private static SymbolLookup tryName(String name, Arena arena) {
        final String mappedName = System.mapLibraryName(name);
        try {
            final SymbolLookup result = SymbolLookup.libraryLookup(mappedName, arena);
            foundName = mappedName;
            return result;
        } catch (IllegalArgumentException _) {
            return null;
        }
    }

    public static String getFoundName() {
        if (foundName == null) {
            throw new IllegalStateException("foundName not initialized yet");
        }
        return foundName;
    }
}
