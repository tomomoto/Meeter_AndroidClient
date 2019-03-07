package com.example.tom.meeter.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.tom.meeter.context.user.UserDTO;

/**
 * Created by Tom on 13.01.2017.
 */

public class RightLoginEvent implements Parcelable {

    public static final Creator<RightLoginEvent> CREATOR = new Creator<RightLoginEvent>() {
        @Override
        public RightLoginEvent createFromParcel(Parcel in) {
            return new RightLoginEvent(in);
        }

        @Override
        public RightLoginEvent[] newArray(int size) {
            return new RightLoginEvent[size];
        }
    };

    private UserDTO user;

    //public BitSet photo;

    public RightLoginEvent(int userId, String userName, String userSurname, String userGender,
                           String userInfo, String userBirthday){
        user = new UserDTO(userId, userName, userGender, userSurname, userInfo, userBirthday);
        //this.photo = photo;
    }

    private RightLoginEvent(Parcel in) {
        user = new UserDTO(
                in.readInt(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString());
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
}
