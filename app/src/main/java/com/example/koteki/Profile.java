package com.example.koteki;

public class Profile {
    public long _id;
    public String name;
    public int age, photo;

    public Profile(){}

    public Profile(String name, int age, int photo) {
        this._id = _id;
        this.name = name;
        this.age = age;
        this.photo = photo;
    }

    public Profile(long _id, String name, int age, int photo) {
        this._id = _id;
        this.name = name;
        this.age = age;
        this.photo = photo;
    }
}

