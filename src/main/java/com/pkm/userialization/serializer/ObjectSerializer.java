package com.pkm.userialization.serializer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ObjectSerializer {

    /**
     * Сериализация объекта в массив байт
     */
    byte[] serializeObj(Object obj) throws IOException, ReflectiveOperationException;

    /**
     * Десериализация массива байт в объект
     */
    Object deserializeObj(byte [] bytes) throws ReflectiveOperationException, IOException;
}
