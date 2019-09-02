package com.pkm.userialization.testclasses;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class University implements Comparable<University> {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int accreditationLevel;

    @Override
    public int compareTo(University u) {
        return name.compareTo(u.getName());
    }
}
