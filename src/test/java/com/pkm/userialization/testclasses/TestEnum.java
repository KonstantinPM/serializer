package com.pkm.userialization.testclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum TestEnum {
    ONE("раз", 1, new University("DNU",1)),
    TWO("два", 2, new University("DPI",3)),
    THREE("три", 3, new University("KNU",1));

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private int value;

    @Getter
    @Setter
    private University university;
}
