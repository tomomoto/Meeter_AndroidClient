package com.example.tom.meeter.context.network.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.tom.meeter.context.user.domain.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 13.01.2017.
 */

public class SuccessfulLogin implements Parcelable, NetworkEvent {

    public static final Creator<SuccessfulLogin> CREATOR = new Creator<SuccessfulLogin>() {
        @Override
        public SuccessfulLogin createFromParcel(Parcel in) {
            return new SuccessfulLogin(in);
        }

        @Override
        public SuccessfulLogin[] newArray(int size) {
            return new SuccessfulLogin[size];
        }
    };

    private static User getUser(Parcel in) {
        return new User(in.readInt(), in.readString(), in.readString(), in.readString(),
                in.readString(), in.readString());
    }

    private User user;

    //public BitSet photo;

    public SuccessfulLogin(int id, String name, String surname, String gender,
                           String info, String birthday){
        user = new User(id, name, surname, gender, info, birthday);
        //this.photo = photo;
    }

    private SuccessfulLogin(Parcel in) {
        user = getUser(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(user.getId());
        dest.writeString(user.getName());
        dest.writeString(user.getSurname());
        dest.writeString(user.getGender());
        dest.writeString(user.getInfo());
        dest.writeString(user.getBirthday());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "SuccessfulLogin{" +
                "user=" + user.toString() +
                '}';
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject().put("user", user);
    }
}
