package com.pkm.userialization.testclasses;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class House {

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private int numberOfFloors;

    @Getter
    @Setter
    private boolean stone;

}
