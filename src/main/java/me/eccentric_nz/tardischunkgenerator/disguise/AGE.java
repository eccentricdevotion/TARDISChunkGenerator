package me.eccentric_nz.tardischunkgenerator.disguise;

public enum AGE {

    BABY(-24000),
    ADULT(1);

    private final int age;

    AGE(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }
}
