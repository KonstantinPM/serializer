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

    public static List<Field> getNonStaticDeclaredFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
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

    public static Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> clazz;
        switch (name) {
            case "byte":
                clazz = byte.class;
                break;
            case "short":
                clazz = short.class;
                break;
            case "int":
                clazz = int.class;
                break;
            case "long":
                clazz = long.class;
                break;
            case "float":
                clazz = float.class;
                break;
            case "double":
                clazz = double.class;
                break;
            case "char":
                clazz = char.class;
                break;
            case "boolean":
                clazz = boolean.class;
                break;
            default:
                clazz = Class.forName(name);
        }

        return clazz;
    }
}
