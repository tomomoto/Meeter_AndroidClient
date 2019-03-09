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

    private String userId;

    public SuccessfulLogin(String id) {
        userId = id;
    }

    private SuccessfulLogin(Parcel in) {
        userId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "SuccessfulLogin{" +
                "userId='" + userId + '\'' +
                '}';
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject().put("userId", userId);
    }
}
