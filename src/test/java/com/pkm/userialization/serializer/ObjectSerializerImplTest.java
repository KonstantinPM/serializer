package com.pkm.userialization.serializer;

import com.pkm.userialization.testclasses.*;
import lombok.SneakyThrows;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class ObjectSerializerImplTest {

    private static ObjectSerializer objectSerializer;

    @BeforeClass
    public static void init() {
        objectSerializer = new ObjectSerializerImpl();
    }

    @Test
    @SneakyThrows
    public void serializeNull() {
        byte[] bytes = objectSerializer.serializeObj(null);
        Object o = objectSerializer.deserializeObj(bytes);

        assertNull(o);
    }

    @Test
    @SneakyThrows
    public void serializeNullString() {
        String str = "null";
        byte[] bytes = objectSerializer.serializeObj(str);
        String deserializeStr = String.valueOf(objectSerializer.deserializeObj(bytes));

        assertNotNull(deserializeStr);
        assertEquals(str, deserializeStr);
    }

    @Test
    @SneakyThrows
    public void serializeString() {
        String str = "This is string";
        System.out.println("Before: " + str);

        byte[] bytes = objectSerializer.serializeObj(str);
        System.out.println("Size in bytes: " + bytes.length);

        String deserializeStr = String.valueOf(objectSerializer.deserializeObj(bytes));
        System.out.println("After: " + deserializeStr);

        assertEquals(str, deserializeStr);
    }

    @Test
    @SneakyThrows
    public void serializePrimitives() {
        byte b = 12;
        short s = 55;
        int i = 540333;
        long l = 42412553421341232L;
        char c = 'Ы';
        float f = 3.21f;
        double d = 4.3221;
        boolean bool = true;

        byte[] bytesB = objectSerializer.serializeObj(b);
        byte[] bytesS = objectSerializer.serializeObj(s);
        byte[] bytesI = objectSerializer.serializeObj(i);
        byte[] bytesL = objectSerializer.serializeObj(l);
        byte[] bytesC = objectSerializer.serializeObj(c);
        byte[] bytesF = objectSerializer.serializeObj(f);
        byte[] bytesD = objectSerializer.serializeObj(d);
        byte[] bytesBool = objectSerializer.serializeObj(bool);

        byte b1 = (byte) objectSerializer.deserializeObj(bytesB);
        short s1 = (short) objectSerializer.deserializeObj(bytesS);
        int i1 = (int) objectSerializer.deserializeObj(bytesI);
        long l1 = (long) objectSerializer.deserializeObj(bytesL);
        char c1 = (char) objectSerializer.deserializeObj(bytesC);
        float f1 = (float) objectSerializer.deserializeObj(bytesF);
        double d1 = (double) objectSerializer.deserializeObj(bytesD);
        boolean bool1 = (boolean) objectSerializer.deserializeObj(bytesBool);

        assertEquals(b, b1);
        assertEquals(s, s1);
        assertEquals(i, i1);
        assertEquals(l, l1);
        assertEquals(c, c1);
        assertEquals(f, f1, 0.0001);
        assertEquals(d, d1, 0.0001);
        assertEquals(bool, bool1);
    }

    @Test
    @SneakyThrows
    public void serializePrimitiveWrappers() {
        Byte b = 12;
        Short s = 55;
        Integer i = 540333;
        Long l = 42412553421341232L;
        Character c = 'Ы';
        Float f = 3.21f;
        Double d = 4.3221;
        Boolean bool = true;

        byte[] bytesB = objectSerializer.serializeObj(b);
        byte[] bytesS = objectSerializer.serializeObj(s);
        byte[] bytesI = objectSerializer.serializeObj(i);
        byte[] bytesL = objectSerializer.serializeObj(l);
        byte[] bytesC = objectSerializer.serializeObj(c);
        byte[] bytesF = objectSerializer.serializeObj(f);
        byte[] bytesD = objectSerializer.serializeObj(d);
        byte[] bytesBool = objectSerializer.serializeObj(bool);

        Byte b1 = (Byte) objectSerializer.deserializeObj(bytesB);
        Short s1 = (Short) objectSerializer.deserializeObj(bytesS);
        Integer i1 = (Integer) objectSerializer.deserializeObj(bytesI);
        Long l1 = (Long) objectSerializer.deserializeObj(bytesL);
        Character c1 = (Character) objectSerializer.deserializeObj(bytesC);
        Float f1 = (Float) objectSerializer.deserializeObj(bytesF);
        Double d1 = (Double) objectSerializer.deserializeObj(bytesD);
        Boolean bool1 = (Boolean) objectSerializer.deserializeObj(bytesBool);

        assertEquals(b, b1);
        assertEquals(s, s1);
        assertEquals(i, i1);
        assertEquals(l, l1);
        assertEquals(c, c1);
        assertEquals(f, f1, 0.0001);
        assertEquals(d, d1, 0.0001);
        assertEquals(bool, bool1);
    }

    @Test
    @SneakyThrows
    public void serializeArrayOfPrimitive() {
        int[] arr = {12,3,4,35,10,43,2};
        System.out.println("Before: " + Arrays.toString(arr));

        byte[] bytes = objectSerializer.serializeObj(arr);
        System.out.println("Size in bytes: " + bytes.length);

        int[] deserializeArr = (int[]) objectSerializer.deserializeObj(bytes);
        System.out.println("After: " + Arrays.toString(deserializeArr));

        assertArrayEquals(arr, deserializeArr);
    }

    @Test
    @SneakyThrows
    public void serializeSimpleObject() {
        University university = new University("ДНУ", 5);
        System.out.println("Before: " + university);

        byte[] bytes = objectSerializer.serializeObj(university);
        System.out.println("Size in bytes: " + bytes.length);

        University deserializeUniversity = (University) objectSerializer.deserializeObj(bytes);
        System.out.println("After: " + deserializeUniversity);

        assertEquals(university, deserializeUniversity);
    }

    @Test
    @SneakyThrows
    public void serializeComplexObjectWithSuperclass() {
        Student student = new Student("ФПМ", new University("ДНУ",4));
        student.setName("Енисей");
        student.setSurname("Швайнштайгер");
        student.setAge(21);
        student.setGender(Gender.MALE);
        System.out.println("Before: " + student);

        byte[] bytes = objectSerializer.serializeObj(student);
        System.out.println("Size in bytes: " + bytes.length);

        Student deserializeStudent = (Student) objectSerializer.deserializeObj(bytes);
        System.out.println("After: " + deserializeStudent);

        assertEquals(student, deserializeStudent);
        assertEquals(student.getName(), deserializeStudent.getName());
        assertEquals(student.getSurname(), deserializeStudent.getSurname());
        assertEquals(student.getAge(), deserializeStudent.getAge());
        assertEquals(student.getGender(), deserializeStudent.getGender());
    }

    @Test
    @SneakyThrows
    public void serializeArrayList() {
        List<University> universities = new ArrayList<>();
        universities.add(new University("DNU",2));
        universities.add(new University("DPI",1));
        System.out.println("Before: " + universities);

        byte[] bytes = objectSerializer.serializeObj(universities);
        System.out.println("Size in bytes: " + bytes.length);

        List<University> deserializeUniversities = (List<University>) objectSerializer.deserializeObj(bytes);
        System.out.println("After: " + deserializeUniversities);

        assertEquals(universities, deserializeUniversities);
    }

    @Test
    @SneakyThrows
    public void serializeHashMap() {
        HashMap<Integer, Student> studentIdMap = new HashMap<>();
        studentIdMap.put(1, new Student("FPM", new University("DNU",4)));
        studentIdMap.put(2, new Student("FDS", new University("DGU",5)));
        studentIdMap.put(3, new Student("KNT", new University("KNU",5)));
        System.out.println("Before: " + studentIdMap);

        byte[] bytes = objectSerializer.serializeObj(studentIdMap);
        System.out.println("Size in bytes: " + bytes.length);

        HashMap<Integer, String> deserializeMap = (HashMap<Integer, String>) objectSerializer.deserializeObj(bytes);
        System.out.println("After: " + deserializeMap);

        assertEquals(studentIdMap, deserializeMap);
    }

    @Test
    @SneakyThrows
    public void testClassWithEnum() {
        Human human = new Human("John", "Ivanov", 27, Gender.MALE);
        System.out.println("Before: " + human);

        byte[] bytes = objectSerializer.serializeObj(human);
        System.out.println(new String(bytes));
        Human deserializeHuman = (Human) objectSerializer.deserializeObj(bytes);
        System.out.println("After: " + deserializeHuman);

        assertEquals(human, deserializeHuman);
    }

    @Test
    @SneakyThrows
    public void testComplexEnum() {
        TestEnum testEnum = TestEnum.valueOf("ONE");
        testEnum.setTitle("десять");
        testEnum.setValue(10);
        System.out.println("Before: " + testEnum);

        byte[] bytes = objectSerializer.serializeObj(testEnum);
        System.out.println(new String(bytes));
        TestEnum deserializeObj = (TestEnum) objectSerializer.deserializeObj(bytes);
        System.out.println("After: " + deserializeObj);

        assertEquals(testEnum, deserializeObj);
    }
}