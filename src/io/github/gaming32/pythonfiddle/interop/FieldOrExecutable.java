package io.github.gaming32.pythonfiddle.interop;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public sealed interface FieldOrExecutable {
    String CONSTRUCTOR_NAME = "<init>";

    Class<?> owner();

    String name();

    List<? extends AccessibleObject> accessibleObjects();

    @Nullable
    static FieldOrExecutable lookup(Class<?> owner, String name, boolean isStatic) {
        if (name.equals(CONSTRUCTOR_NAME)) {
            if (!isStatic) {
                return null;
            }
            try {
                return ExecutablesWrapper.findConstructors(owner);
            } catch (NoSuchMethodException _) {
                return null;
            }
        }
        try {
            return ExecutablesWrapper.findMethods(owner, name, isStatic);
        } catch (NoSuchMethodException _) {
        }
        try {
            return FieldWrapper.findField(owner, name, isStatic);
        } catch (NoSuchFieldException _) {
        }
        return null;
    }

    record FieldWrapper(Field field) implements FieldOrExecutable {
        public static FieldWrapper findField(Class<?> owner, String name, boolean isStatic) throws NoSuchFieldException {
            final Field field = owner.getField(name);
            if (Modifier.isStatic(field.getModifiers()) != isStatic) {
                throw new NoSuchFieldException(owner.getName() + '.' + name);
            }
            return new FieldWrapper(field);
        }

        @Override
        public Class<?> owner() {
            return field.getDeclaringClass();
        }

        @Override
        public String name() {
            return field.getName();
        }

        @Override
        public List<? extends AccessibleObject> accessibleObjects() {
            return List.of(field);
        }
    }

    record ExecutablesWrapper(Class<?> owner, String name, List<? extends Executable> executables) implements FieldOrExecutable {
        public static ExecutablesWrapper findMethods(Class<?> owner, String name, boolean isStatic) throws NoSuchMethodException {
            final List<Method> methods = Arrays.stream(owner.getMethods())
                .filter(m -> m.getName().equals(name))
                .filter(m -> Modifier.isStatic(m.getModifiers()) == isStatic)
                .toList();
            if (methods.isEmpty()) {
                throw new NoSuchMethodException(owner.getName() + '.' + name);
            }
            return new ExecutablesWrapper(owner, name, methods);
        }

        public static ExecutablesWrapper findConstructors(Class<?> owner) throws NoSuchMethodException {
            final Constructor<?>[] constructors = owner.getConstructors();
            if (constructors.length == 0) {
                throw new NoSuchMethodException(owner.getName() + '.' + CONSTRUCTOR_NAME);
            }
            return new ExecutablesWrapper(owner, CONSTRUCTOR_NAME, List.of(constructors));
        }

        @Override
        public List<? extends AccessibleObject> accessibleObjects() {
            return executables;
        }
    }
}
