package com.pkm.userialization.serializer;

import com.google.common.primitives.Primitives;
import com.pkm.userialization.utils.ReflectionUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

public class ObjectSerializerImpl implements ObjectSerializer {

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public byte[] serializeObj(Object obj) throws IOException, IllegalAccessException, InstantiationException,
            NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(baos);
        writeObject(obj);

        return baos.toByteArray();
    }

    public Object deserializeObj(byte[] bytes) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, IOException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        dataInputStream = new DataInputStream(bais);

        return readObject();
    }

    private Object readObject() throws IOException, ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        String className = dataInputStream.readUTF();
        if ("null".equals(className)) {
            return null;
        }
        Class<?> clazz = Class.forName(className);
        Object obj;

        if (clazz.isPrimitive()){
            obj = readPrimitive(clazz);
        } else if (Primitives.isWrapperType(clazz)) {
            obj = readPrimitive(Primitives.unwrap(clazz));
        } else if (clazz == String.class) {
            obj = dataInputStream.readUTF();
        } else if (clazz.isArray()) {
            obj = readArray();
        } else if (clazz.isEnum()) {
            obj = readEnum((Class<Enum>) clazz);
        } else {
            if (ReflectionUtils.hasDefaultConstructor(clazz)) {
                obj = clazz.newInstance();
            } else {
                obj = ReflectionUtils.newInstanceWithoutDefaultConstructor(clazz);
            }
            List<Field> allNonStaticField = ReflectionUtils.getAllNonStaticFields(clazz);
            for (Field field : allNonStaticField) {
                readField(obj, field);
            }
        }

        return obj;
    }

    private Object readEnum(Class<Enum> clazz) throws IOException {
        String name = dataInputStream.readUTF();
        return Enum.valueOf(clazz, name);
    }

    private Object readArray() throws IOException, ClassNotFoundException, NoSuchFieldException,
            InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        int arrLength = dataInputStream.readInt();
        String componentType = dataInputStream.readUTF();
        Object array = Array.newInstance(Class.forName(componentType), arrLength);
        for (int i = 0; i < arrLength; i++) {
            Array.set(array, i, readObject());
        }
        return array;
    }

    private void readField(Object obj, Field field) throws IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        if (Modifier.isFinal(field.getModifiers())) {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }

        String fieldName = dataInputStream.readUTF();
        field.set(obj, readObject());
    }

    private Object readPrimitive(Class<?> clazz) throws IOException {
        if (clazz == int.class) {
            return dataInputStream.readInt();
        } else if (clazz == short.class) {
            return dataInputStream.readShort();
        } else if (clazz == long.class) {
            return dataInputStream.readLong();
        } else if (clazz == char.class) {
            return dataInputStream.readChar();
        } else if (clazz == boolean.class) {
            return dataInputStream.readBoolean();
        } else if (clazz == double.class) {
            return dataInputStream.readDouble();
        } else if (clazz == float.class) {
            return dataInputStream.readFloat();
        } else if (clazz == byte.class) {
            return dataInputStream.readByte();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void writeObject(Object obj) throws IOException, IllegalAccessException, InstantiationException,
            NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        if (obj == null) {
            dataOutputStream.writeUTF("null");
            return;
        }
        Class<?> clazz = obj.getClass();
        dataOutputStream.writeUTF(clazz.getName());

        if (clazz.isPrimitive()) {
            writePrimitive(clazz, obj);
        } else if (Primitives.isWrapperType(clazz)) {
            writePrimitive(Primitives.unwrap(clazz), obj);
        } else if (clazz == String.class) {
            dataOutputStream.writeUTF((String) obj);
        } else if (clazz.isArray()) {
            writeArray(obj, clazz.getComponentType());
        } else if (clazz.isEnum()) {
            writeEnum(obj);
        } else {
            List<Field> allNonStaticFields = ReflectionUtils.getAllNonStaticFields(clazz);
            for (Field field : allNonStaticFields) {
                writeField(field, obj);
            }
        }
    }

    private void writeEnum(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException {
        Field field = Enum.class.getDeclaredField("name");
        field.setAccessible(true);
        String name = String.valueOf(field.get(obj));
        dataOutputStream.writeUTF(name);
    }

    private void writeField(Field field, Object obj) throws IOException, IllegalAccessException,
            InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        dataOutputStream.writeUTF(field.getName());
        writeObject(field.get(obj));
    }

    private void writeArray(Object obj, Class<?> componentType) throws IllegalAccessException, IOException,
            InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        int arrLength = Array.getLength(obj);
        dataOutputStream.writeInt(arrLength);
        dataOutputStream.writeUTF(componentType.getName());
        for (int i = 0; i < arrLength; i++) {
            writeObject(Array.get(obj, i));
        }
    }

    private void writePrimitive(Class<?> clazz, Object value) throws IOException {
        if (clazz == int.class) {
            dataOutputStream.writeInt((int) value);
        } else if (clazz == short.class) {
            dataOutputStream.writeShort((short) value);
        } else if (clazz == long.class) {
            dataOutputStream.writeLong((long) value);
        } else if (clazz == char.class) {
            dataOutputStream.writeChar((char) value);
        } else if (clazz == boolean.class) {
            dataOutputStream.writeBoolean((boolean) value);
        } else if (clazz == double.class) {
            dataOutputStream.writeDouble((double) value);
        } else if (clazz == float.class) {
            dataOutputStream.writeFloat((float) value);
        } else if (clazz == byte.class) {
            dataOutputStream.writeByte((byte) value);
        } else {
            System.out.println("Это не примитив! : " + clazz.getName());
        }
    }
}
