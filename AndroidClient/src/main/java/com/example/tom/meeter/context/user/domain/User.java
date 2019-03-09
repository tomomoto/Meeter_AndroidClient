package com.example.tom.meeter.context.user.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class User {

    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String gender;
    private String surname;
    private String info;
    private String birthday;

    public User(@NonNull String id, String name, String gender, String surname,
                String info, String birthday) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.surname = surname;
        this.info = info;
        this.birthday = birthday;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", surname='" + surname + '\'' +
                ", info='" + info + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}
