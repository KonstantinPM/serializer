package com.pkm.userialization.utils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionUtils {

    /**
     * Возвращает список всех полей для указанного класса, включая поля его предков
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        Class<?> superclass = clazz.getSuperclass();
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        if (superclass != Object.class) {
            fields.addAll(getAllFields(superclass));
        }

        return fields;
    }

    /**
     * Возвращает список всех нестатических полей для указанного класса, включая поля его предков
     */
    public static List<Field> getAllNonStaticFields(Class<?> clazz) {
        return getAllFields(clazz).stream()
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список всех нестатических полей для указанного класса
     */
    public static List<Field> getNonStaticDeclaredFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает true, если указанный класс имеет конструктор без аргументов, иначе false
     */
    public static boolean hasNoArgsConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                    .anyMatch(c -> c.getParameterCount() == 0);
    }

    /**
     * Возвращает новый экземпляр класса, созданный без использования конструктора без аргументов
     */
    public static Object newInstanceWithoutNoArgsConstructor(Class<?> clazz) throws ReflectiveOperationException {
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

    /**
     * Возвращает новый экземпляр класса (включая примитивы), созданный по указанному имени.
     * @param className имя класса
     */
    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        Class<?> clazz;
        switch (className) {
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
                clazz = Class.forName(className);
        }

        return clazz;
    }
}
