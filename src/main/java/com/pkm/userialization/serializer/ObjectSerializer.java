package com.pkm.userialization.serializer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ObjectSerializer {

    /**
     * Сериализация объекта в массив байт
     */
    byte[] serializeObj(Object obj) throws IOException, IllegalAccessException, InstantiationException,
            NoSuchFieldException, NoSuchMethodException, InvocationTargetException;

    /**
     * Десериализация массива байт в объект
     */
    Object deserializeObj(byte [] bytes) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, IOException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException;
}
