package com.pkm.userialization.serializer;

import com.google.common.primitives.Primitives;
import com.pkm.userialization.utils.ReflectionUtils;

import java.io.*;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ObjectSerializerImpl implements ObjectSerializer {

    public byte[] serializeObj(Object obj) throws IOException, ReflectiveOperationException {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(baos)) {

            writeObject(obj, dataOutputStream);
            return baos.toByteArray();
        }
    }

    public Object deserializeObj(byte[] bytes) throws ReflectiveOperationException, IOException {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            DataInputStream dataInputStream = new DataInputStream(bais)) {

            return readObject(dataInputStream);
        }
    }

    private Object readObject(DataInputStream dataInputStream) throws IOException, ReflectiveOperationException {
        String className = dataInputStream.readUTF();
        if ("null".equals(className)) {
            return null;
        }
        Class<?> clazz = Class.forName(className);
        Object obj;

        if (clazz.isPrimitive()){
            obj = readPrimitive(clazz, dataInputStream);
        } else if (Primitives.isWrapperType(clazz)) {
            obj = readPrimitive(Primitives.unwrap(clazz), dataInputStream);
        } else if (clazz == String.class) {
            obj = dataInputStream.readUTF();
        } else if (clazz.isArray()) {
            obj = readArray(dataInputStream);
        } else if (clazz.isEnum()) {
            obj = readEnum((Class<Enum>) clazz, dataInputStream);
        } else if (Map.class.isAssignableFrom(clazz)) {
            obj = readMap(clazz, dataInputStream);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            obj = readCollection(clazz, dataInputStream);
        } else {
            if (ReflectionUtils.hasNoArgsConstructor(clazz)) {
                obj = clazz.newInstance();
            } else {
                obj = ReflectionUtils.newInstanceWithoutNoArgsConstructor(clazz);
            }
            List<Field> allNonStaticField = ReflectionUtils.getAllNonStaticFields(clazz);
            for (Field field : allNonStaticField) {
                readField(obj, field, dataInputStream);
            }
        }

        return obj;
    }

    private Object readMap(Class<?> clazz, DataInputStream dataInputStream)
            throws IOException, ReflectiveOperationException {
        Map<Object,Object> map = (Map<Object, Object>) clazz.newInstance();
        int size = dataInputStream.readInt();
        for (int i = 0; i < size; i++) {
            Object key = readObject(dataInputStream);
            Object value = readObject(dataInputStream);
            map.put(key, value);
        }
        return map;
    }

    private Object readCollection(Class<?> clazz, DataInputStream dataInputStream)
            throws IOException, ReflectiveOperationException {
        Collection<Object> collection = (Collection<Object>) clazz.newInstance();
        int size = dataInputStream.readInt();
        for (int i = 0; i < size; i++) {
            collection.add(readObject(dataInputStream));
        }
        return collection;
    }

    private Object readEnum(Class<Enum> clazz, DataInputStream dataInputStream)
            throws IOException, ReflectiveOperationException {
        String value = dataInputStream.readUTF();
        Enum anEnum = Enum.valueOf(clazz, value);

        List<Field> fields = ReflectionUtils.getNonStaticDeclaredFields(clazz);
        for (Field field : fields) {
            readField(anEnum, field, dataInputStream);
        }
        return anEnum;
    }

    private Object readArray(DataInputStream dataInputStream)
            throws IOException, ReflectiveOperationException {
        int arrLength = dataInputStream.readInt();
        String componentType = dataInputStream.readUTF();
        Object array = Array.newInstance(ReflectionUtils.loadClass(componentType), arrLength);
        for (int i = 0; i < arrLength; i++) {
            Array.set(array, i, readObject(dataInputStream));
        }
        return array;
    }

    private void readField(Object obj, Field field, DataInputStream dataInputStream)
            throws IOException, ReflectiveOperationException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        if (Modifier.isFinal(field.getModifiers())) {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }

//        String fieldName = dataInputStream.readUTF();
        field.set(obj, readObject(dataInputStream));
    }

    private Object readPrimitive(Class<?> clazz, DataInputStream dataInputStream) throws IOException {
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

    private void writeObject(Object obj, DataOutputStream dataOutputStream)
            throws IOException, ReflectiveOperationException {
        if (obj == null) {
            dataOutputStream.writeUTF("null");
            return;
        }
        Class<?> clazz = obj.getClass();
        dataOutputStream.writeUTF(clazz.getName());

        if (clazz.isPrimitive()) {
            writePrimitive(clazz, obj, dataOutputStream);
        } else if (Primitives.isWrapperType(clazz)) {
            writePrimitive(Primitives.unwrap(clazz), obj, dataOutputStream);
        } else if (clazz == String.class) {
            dataOutputStream.writeUTF((String) obj);
        } else if (clazz.isArray()) {
            writeArray(obj, clazz.getComponentType(), dataOutputStream);
        } else if (clazz.isEnum()) {
            writeEnum(obj, dataOutputStream);
        } else if (obj instanceof Collection<?>) {
            writeCollection((Collection<?>) obj, dataOutputStream);
        } else if (obj instanceof Map<?,?>) {
            writeMap((Map<?,?>) obj, dataOutputStream);
        } else {
            List<Field> allNonStaticFields = ReflectionUtils.getAllNonStaticFields(clazz);
            for (Field field : allNonStaticFields) {
                writeField(field, obj, dataOutputStream);
            }
        }
    }

    private void writeMap(Map<?, ?> map, DataOutputStream dataOutputStream)
            throws ReflectiveOperationException, IOException {
        dataOutputStream.writeInt(map.size());
        for (Object key : map.keySet()) {
            writeObject(key, dataOutputStream);
            writeObject(map.get(key), dataOutputStream);
        }
    }

    private void writeCollection(Collection<?> collection, DataOutputStream dataOutputStream)
            throws ReflectiveOperationException, IOException {
        dataOutputStream.writeInt(collection.size());
        for (Object elem : collection) {
            writeObject(elem, dataOutputStream);
        }
    }

    private void writeEnum(Object obj, DataOutputStream dataOutputStream)
            throws IOException, ReflectiveOperationException {
        Class<?> clazz = obj.getClass();

        Field nameField = Enum.class.getDeclaredField("name");
        nameField.setAccessible(true);
        String name = String.valueOf(nameField.get(obj));
        dataOutputStream.writeUTF(name);

        List<Field> fields = ReflectionUtils.getNonStaticDeclaredFields(clazz);
        for (Field field : fields) {
            writeField(field, obj, dataOutputStream);
        }
    }

    private void writeField(Field field, Object obj, DataOutputStream dataOutputStream)
            throws IOException, ReflectiveOperationException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
//        dataOutputStream.writeUTF(field.getName());
        writeObject(field.get(obj), dataOutputStream);
    }

    private void writeArray(Object obj, Class<?> componentType, DataOutputStream dataOutputStream)
            throws IOException, ReflectiveOperationException {
        int arrLength = Array.getLength(obj);
        dataOutputStream.writeInt(arrLength);
        dataOutputStream.writeUTF(componentType.getName());
        for (int i = 0; i < arrLength; i++) {
            writeObject(Array.get(obj, i), dataOutputStream);
        }
    }

    private void writePrimitive(Class<?> clazz, Object value, DataOutputStream dataOutputStream) throws IOException {
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
