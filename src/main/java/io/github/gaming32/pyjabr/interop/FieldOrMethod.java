package io.github.gaming32.pyjabr.interop;

import io.github.gaming32.pyjabr.util.ReflectUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public sealed interface FieldOrMethod {
    String CONSTRUCTOR_NAME = "<init>";

    Class<?> owner();

    String name();

    List<? extends AccessibleObject> accessibleObjects();

    @Nullable
    static FieldOrMethod lookup(Class<?> owner, String name, boolean isStatic) {
        if (name.equals(CONSTRUCTOR_NAME)) {
            if (!isStatic) {
                return null;
            }
            try {
                return MethodWrapper.findConstructors(owner);
            } catch (NoSuchMethodException _) {
                return null;
            }
        }
        try {
            return MethodWrapper.findMethods(owner, name, isStatic);
        } catch (NoSuchMethodException _) {
        }
        try {
            return FieldWrapper.findField(owner, name);
        } catch (NoSuchFieldException _) {
        }
        return null;
    }

    record FieldWrapper(Field field) implements FieldOrMethod {
        public static FieldWrapper findField(Class<?> owner, String name) throws NoSuchFieldException {
            return new FieldWrapper(owner.getField(name));
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

    record MethodWrapper(Class<?> owner, String name, List<? extends Executable> executables) implements FieldOrMethod {
        public static MethodWrapper findMethods(Class<?> owner, String name, boolean isStatic) throws NoSuchMethodException {
            Stream<Method> methodStream = Arrays.stream(owner.getMethods())
                .filter(m -> m.getName().equals(name))
                .filter(m -> Modifier.isStatic(m.getModifiers()) == isStatic);
            if (!isStatic) {
                methodStream = methodStream
                    .map(ReflectUtil::findAccessibleMethod)
                    .filter(Objects::nonNull);
            }
            final List<Method> methods = methodStream.toList();
            if (methods.isEmpty()) {
                throw new NoSuchMethodException(owner.getName() + '.' + name);
            }
            return new MethodWrapper(owner, name, methods);
        }

        public static MethodWrapper findConstructors(Class<?> owner) throws NoSuchMethodException {
            final Constructor<?>[] constructors = owner.getConstructors();
            if (constructors.length == 0 || Modifier.isAbstract(owner.getModifiers())) {
                throw new NoSuchMethodException(owner.getName() + '.' + CONSTRUCTOR_NAME);
            }
            return new MethodWrapper(owner, CONSTRUCTOR_NAME, List.of(constructors));
        }

        @Override
        public List<? extends AccessibleObject> accessibleObjects() {
            return executables;
        }
    }
}
