package com.tom.meeter.context.profile.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Optional;

public class UpdateProfileRequest {

    private Optional<String> name;
    private Optional<String> surname;
    private Optional<String> info;
    @JsonProperty(value = "old_password")
    private Optional<String> oldPassword;
    @JsonProperty(value = "new_password")
    private Optional<String> newPassword;
    private Optional<LocalDate> birthday;
    @JsonProperty(value = "photo_path")
    private Optional<String> photoPath;

    public boolean isEmpty() {
        return (name == null && surname == null && info == null && oldPassword == null
              && newPassword == null && birthday == null && photoPath == null);
    }

    public Optional<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Optional.ofNullable(name);
    }

    public Optional<String> getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = Optional.ofNullable(surname);
    }

    public Optional<String> getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = Optional.ofNullable(info);
    }

    public Optional<String> getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = Optional.ofNullable(oldPassword);
    }

    public Optional<String> getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = Optional.ofNullable(newPassword);
    }

    public Optional<LocalDate> getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = Optional.ofNullable(birthday);
    }

    public Optional<String> getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = Optional.ofNullable(photoPath);
    }
}
