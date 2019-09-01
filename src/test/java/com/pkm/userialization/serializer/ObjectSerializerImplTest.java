package com.pkm.userialization.serializer;

import com.pkm.userialization.testclasses.Gender;
import com.pkm.userialization.testclasses.Human;
import com.pkm.userialization.testclasses.Student;
import com.pkm.userialization.testclasses.University;
import lombok.SneakyThrows;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class ObjectSerializerImplTest {

    private static ObjectSerializer objectSerializer;

    @BeforeClass
    public static void setUp() {
        objectSerializer = new ObjectSerializerImpl();
    }

    @Test
    @SneakyThrows
    public void name() {
        List<University> universities = new ArrayList<>();
        universities.add(new University("DNU"));
        universities.add(new University("GORKA"));
        System.out.println(universities);

        byte[] bytes = objectSerializer.serializeObj(universities);
        System.out.println(new String(bytes));

        Object obj = objectSerializer.deserializeObj(bytes);
        System.out.println(obj);
        System.out.println(universities.equals(obj));
    }

    @Test
    @SneakyThrows
    public void name1() {
        Student student = new Student("FPM", new University("DNU"));
        System.out.println(student);

        byte[] bytes = objectSerializer.serializeObj(student);
        System.out.println(new String(bytes));

        Object obj = objectSerializer.deserializeObj(bytes);
        System.out.println(obj);
        System.out.println(student.equals(obj));
    }

    @Test
    @SneakyThrows
    public void name2() {
        HashMap<Integer, Student> studentIdMap = new HashMap<>();
        studentIdMap.put(1, new Student("FPM", new University("DNU")));
        studentIdMap.put(2, new Student("FDS", new University("DGU")));

        byte[] bytes = objectSerializer.serializeObj(studentIdMap);
        System.out.println(new String(bytes));

        Object obj = objectSerializer.deserializeObj(bytes);
        System.out.println(obj);
        System.out.println(studentIdMap.equals(obj));
    }

    @Test
    @SneakyThrows
    public void testClassWithEnum() {
        Human human = new Human("John", "Ivanov", 27, Gender.Male);
        System.out.println(human);

        byte[] bytes = objectSerializer.serializeObj(human);
        System.out.println(new String(bytes));
        Human deserializeHuman = (Human) objectSerializer.deserializeObj(bytes);
        System.out.println(deserializeHuman);

        assertEquals(human, deserializeHuman);
    }
}