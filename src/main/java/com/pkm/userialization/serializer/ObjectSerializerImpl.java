package com.pkm.userialization.serializer;

import com.google.common.primitives.Primitives;
import com.pkm.userialization.utils.ReflectionUtils;

import java.io.*;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
        } else if (Map.class.isAssignableFrom(clazz)) {
            obj = readMap(clazz);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            obj = readCollection(clazz);
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

    private Object readMap(Class<?> clazz) throws IllegalAccessException, InstantiationException, IOException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        Map<Object,Object> map = (Map<Object, Object>) clazz.newInstance();
        int size = dataInputStream.readInt();
        for (int i = 0; i < size; i++) {
            Object key = readObject();
            Object value = readObject();
            map.put(key, value);
        }
        return map;
    }

    private Object readCollection(Class<?> clazz) throws IllegalAccessException, InstantiationException, IOException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        Collection<Object> collection = (Collection<Object>) clazz.newInstance();
        int size = dataInputStream.readInt();
        for (int i = 0; i < size; i++) {
            collection.add(readObject());
        }
        return collection;
    }

    private Object readEnum(Class<Enum> clazz) throws IOException, IllegalAccessException, InvocationTargetException,
            InstantiationException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException {
        String value = dataInputStream.readUTF();
        Enum anEnum = Enum.valueOf(clazz, value);

        List<Field> fields = ReflectionUtils.getNonStaticDeclaredFields(clazz);
        for (Field field : fields) {
            readField(anEnum, field);
        }
        return anEnum;
    }

    private Object readArray() throws IOException, ClassNotFoundException, NoSuchFieldException,
            InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        int arrLength = dataInputStream.readInt();
        String componentType = dataInputStream.readUTF();
        Object array = Array.newInstance(ReflectionUtils.loadClass(componentType), arrLength);
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

//        String fieldName = dataInputStream.readUTF();
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
        } else if (obj instanceof Collection<?>) {
            writeCollection((Collection<?>) obj);
        } else if (obj instanceof Map<?,?>) {
            writeMap((Map<?,?>) obj);
        } else {
            List<Field> allNonStaticFields = ReflectionUtils.getAllNonStaticFields(clazz);
            for (Field field : allNonStaticFields) {
                writeField(field, obj);
            }
        }
    }

    private void writeMap(Map<?, ?> map) throws IllegalAccessException, InvocationTargetException, IOException,
            InstantiationException, NoSuchMethodException, NoSuchFieldException {
        dataOutputStream.writeInt(map.size());
        for (Object key : map.keySet()) {
            writeObject(key);
            writeObject(map.get(key));
        }
    }

    private void writeCollection(Collection<?> collection) throws IllegalAccessException, InvocationTargetException,
            IOException, InstantiationException, NoSuchMethodException, NoSuchFieldException {
        dataOutputStream.writeInt(collection.size());
        for (Object elem : collection) {
            writeObject(elem);
        }
    }

    private void writeEnum(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException,
            NoSuchMethodException, InvocationTargetException, InstantiationException {
        Class<?> clazz = obj.getClass();

        Field nameField = Enum.class.getDeclaredField("name");
        nameField.setAccessible(true);
        String name = String.valueOf(nameField.get(obj));
        dataOutputStream.writeUTF(name);

        List<Field> fields = ReflectionUtils.getNonStaticDeclaredFields(clazz);
        for (Field field : fields) {
            writeField(field, obj);
        }
    }

    private void writeField(Field field, Object obj) throws IOException, IllegalAccessException,
            InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
//        dataOutputStream.writeUTF(field.getName());
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
            throw new IllegalArgumentException();
        }
    }
}
