package com.pkm.userialization.utils;

import com.pkm.userialization.testclasses.Student;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReflectionUtilsTest {

    @Test
    public void testGetAllDeclaredFields() {
        List<Field> allDeclaredFields = ReflectionUtils.getAllFields(Student.class);

        System.out.println(allDeclaredFields);
        for (Field field : allDeclaredFields) {
            System.out.println(field.getName());
        }
        assertEquals(allDeclaredFields.size(), 7);
    }
}