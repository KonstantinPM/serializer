package com.pkm.userialization.testclasses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Human {

    @Getter
    @Setter
    private String name = "Petya";

    @Getter
    @Setter
    private String surname = "Loma";

    @Getter
    @Setter
    private int age = 31;

    @Getter
    @Setter
    private Gender gender;
}
