package com.pkm.userialization.testclasses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Student extends Human {

    @Getter
    @Setter
    private String facultyName;

    @Getter
    @Setter
    private University university;

}
