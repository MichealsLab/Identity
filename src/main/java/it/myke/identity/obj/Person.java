package it.myke.identity.obj;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class Person {
    private String name, gender;
    private int age = -1;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasGender() {
        return gender != null;
    }

    public boolean hasAge() {
        return age != -1;
    }

}


