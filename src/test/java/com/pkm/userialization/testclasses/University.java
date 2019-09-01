package com.pkm.userialization.testclasses;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class University {

    @Getter
    @Setter
    private String name;
}
