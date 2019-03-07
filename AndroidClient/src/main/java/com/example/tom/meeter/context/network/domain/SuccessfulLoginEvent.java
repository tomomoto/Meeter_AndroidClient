package com.example.tom.meeter.context.network.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.tom.meeter.context.user.UserDTO;

/**
 * Created by Tom on 13.01.2017.
 */

public class SuccessfulLoginEvent implements Parcelable {

    public static final Creator<SuccessfulLoginEvent> CREATOR = new Creator<SuccessfulLoginEvent>() {
        @Override
        public SuccessfulLoginEvent createFromParcel(Parcel in) {
            return new SuccessfulLoginEvent(in);
        }

        @Override
        public SuccessfulLoginEvent[] newArray(int size) {
            return new SuccessfulLoginEvent[size];
        }
    };

    private static UserDTO getUser(Parcel in) {
        return new UserDTO(in.readInt(), in.readString(), in.readString(), in.readString(),
                in.readString(), in.readString());
    }

    private UserDTO user;

    //public BitSet photo;

    public SuccessfulLoginEvent(int userId, String userName, String userSurname, String userGender,
                                String userInfo, String userBirthday){
        user = new UserDTO(userId, userName, userGender, userSurname, userInfo, userBirthday);
        //this.photo = photo;
    }

    private SuccessfulLoginEvent(Parcel in) {
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "SuccessfulLoginEvent{" +
                "user=" + user.toString() +
                '}';
    }
}
