package com.pkm.userialization.utils;

import com.pkm.userialization.testclasses.House;
import com.pkm.userialization.testclasses.Student;
import lombok.SneakyThrows;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ReflectionUtilsTest {

    @Test
    public void getAllFields() {
        List<Field> fields = ReflectionUtils.getAllFields(Student.class);
        List<String> fieldsNames = fields.stream()
                                        .map(Field::getName)
                                        .collect(Collectors.toList());

        System.out.println("All fields of Student class: " + fieldsNames);

        assertEquals(fields.size(), 8);
        assertTrue(fieldsNames.contains("name"));
        assertTrue(fieldsNames.contains("surname"));
        assertTrue(fieldsNames.contains("age"));
        assertTrue(fieldsNames.contains("gender"));
        assertTrue(fieldsNames.contains("facultyName"));
        assertTrue(fieldsNames.contains("university"));
        assertTrue(fieldsNames.contains("HUMAN_CLASS"));
        assertTrue(fieldsNames.contains("YEAR"));
    }

    @Test
    public void getAllNonStaticFields() {
        List<Field> fields = ReflectionUtils.getAllNonStaticFields(Student.class);
        List<String> fieldsNames = fields.stream()
                .map(Field::getName)
                .collect(Collectors.toList());

        System.out.println("All non-static fields of Student class: " + fieldsNames);

        assertEquals(fields.size(), 6);
        assertTrue(fieldsNames.contains("name"));
        assertTrue(fieldsNames.contains("surname"));
        assertTrue(fieldsNames.contains("age"));
        assertTrue(fieldsNames.contains("gender"));
        assertTrue(fieldsNames.contains("facultyName"));
        assertTrue(fieldsNames.contains("university"));
    }

    @Test
    public void getNonStaticDeclaredFields() {
        List<Field> fields = ReflectionUtils.getNonStaticDeclaredFields(Student.class);
        List<String> fieldsNames = fields.stream()
                .map(Field::getName)
                .collect(Collectors.toList());

        System.out.println("Non-static fields of Student class: " + fieldsNames);

        assertEquals(fields.size(), 2);
        assertTrue(fieldsNames.contains("facultyName"));
        assertTrue(fieldsNames.contains("university"));
    }

    @Test
    public void hasNoArgsConstructor() {
        assertFalse(ReflectionUtils.hasNoArgsConstructor(House.class));
        assertTrue(ReflectionUtils.hasNoArgsConstructor(Student.class));
    }

    @Test
    @SneakyThrows
    public void newInstanceWithoutNoArgsConstructor() {
        House house = (House) ReflectionUtils.newInstanceWithoutNoArgsConstructor(House.class);
        System.out.println(house);

        assertNotNull(house);
        assertNull(house.getAddress());
        assertEquals(house.getNumberOfFloors(), 0);
        assertFalse(house.isStone());
    }

    @Test
    public void loadClass() throws ClassNotFoundException {
        Class<?> iClass = ReflectionUtils.loadClass("int");
        Class<?> dClass = ReflectionUtils.loadClass("double");
        Class<?> bClass = ReflectionUtils.loadClass("boolean");
        Class<?> cClass = ReflectionUtils.loadClass("char");

        assertTrue(iClass == int.class);
        assertTrue(dClass == double.class);
        assertTrue(bClass == boolean.class);
        assertTrue(cClass == char.class);
    }
}