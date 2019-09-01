package com.pkm.userialization.utils;

import com.google.common.primitives.Primitives;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionUtils {

    public static List<Field> getAllFields(Class<?> clazz) {
        Class<?> superclass = clazz.getSuperclass();
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        if (superclass != Object.class) {
            fields.addAll(getAllFields(superclass));
        }

        return fields;
    }

    public static List<Field> getAllNonStaticFields(Class<?> clazz) {
        return getAllFields(clazz).stream()
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .collect(Collectors.toList());
    }

    public static boolean hasDefaultConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                    .anyMatch(c -> c.getParameterCount() == 0);
    }

    public static Object newInstanceWithoutDefaultConstructor(Class<?> clazz) throws NoSuchMethodException,
            IllegalAccessException, InstantiationException, InvocationTargetException {
        Optional<Constructor<?>> constructorOpt = Arrays.stream(clazz.getDeclaredConstructors())
                                                        .filter(c -> c.getParameterCount() != 0)
                                                        .min(Comparator.comparingInt(Constructor::getParameterCount));

        Constructor<?> constructor = constructorOpt.orElseThrow(NoSuchMethodException::new);
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        Parameter[] parameters = constructor.getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramClass = parameters[i].getType();
            if (paramClass.isPrimitive()){
                params[i] = 0;
            } else {
                params[i] = null;
            }
        }
        return constructor.newInstance(params);
    }

    public static Field getField(Class<?> basicClass, Class<?> fieldClass, String fieldName) {
        List<Field> fields = getAllFields(basicClass);
        Optional<Field> field = fields.stream()
                                    .filter(f -> f.getName().equals(fieldName) && f.getType() == fieldClass)
                                    .findFirst();
        return field.orElse(null);
    }

    public static List<Field> getAllNonPrimitiveFields(Class<?> clazz) {
        List<Field> fields = getAllFields(clazz);
        return fields.stream()
                    .filter(field -> !field.getType().isPrimitive() &&
                                     !Primitives.isWrapperType(field.getType()))
                    .collect(Collectors.toList());
    }
}
