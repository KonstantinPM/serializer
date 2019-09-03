package com.pkm.userialization.testclasses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Human {

    public final static String HUMAN_CLASS = "Homo sapiens";

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String surname;

    @Getter
    @Setter
    private int age;

    @Getter
    @Setter
    private Gender gender;
}
