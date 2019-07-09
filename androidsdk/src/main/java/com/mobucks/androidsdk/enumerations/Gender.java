package com.mobucks.androidsdk.enumerations;

public enum Gender {
    FEMALE("f"),
    MALE("m"),
    OTHER("o");

    private final String name;

    Gender(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
