package io.github.gaming32.pyjabr;

import com.google.common.collect.Streams;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class ReflectUtil {
    public static Method findAccessibleMethod(Method method) {
        if (Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            return method;
        }
        final Class<?>[] paramTypes = method.getParameterTypes();
        return streamClassHierarchy(method.getDeclaringClass())
            .filter(c -> Modifier.isPublic(c.getModifiers()))
            .map(c -> {
                try {
                    return c.getMethod(method.getName(), paramTypes);
                } catch (NoSuchMethodException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    public static Stream<Class<?>> streamClassHierarchy(Class<?> clazz) {
        return streamDuplicatedHierarchy(clazz).distinct();
    }

    private static Stream<Class<?>> streamDuplicatedHierarchy(Class<?> clazz) {
        if (clazz == null) {
            return Stream.empty();
        }
        return Stream.of(clazz).flatMap(c -> Streams.concat(
            Stream.of(c),
            streamDuplicatedHierarchy(c.getSuperclass()),
            Arrays.stream(c.getInterfaces()).flatMap(ReflectUtil::streamDuplicatedHierarchy)
        ));
    }
}
