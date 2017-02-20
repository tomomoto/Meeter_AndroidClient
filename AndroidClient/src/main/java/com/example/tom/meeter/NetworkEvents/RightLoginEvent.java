package com.example.tom.meeter.NetworkEvents;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tom on 13.01.2017.
 */

public class RightLoginEvent implements Parcelable {
    public int UserId;
    public String Name;
    public String Surname;
    public String Sex;
    public String Info;
    public String Birthday;
    //public BitSet photo;

    public RightLoginEvent(int UserID, String Name, String Surname, String Sex, String Info, String Birthday){
        this.UserId = UserID;
        this.Name = Name;
        this.Surname = Surname;
        this.Sex = Sex;
        this.Info = Info;
        this.Birthday = Birthday;
        //this.photo = photo;
    }

    protected RightLoginEvent(Parcel in) {
        UserId = in.readInt();
        Name = in.readString();
        Surname = in.readString();
        Sex = in.readString();
        Info = in.readString();
        Birthday = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(UserId);
        dest.writeString(Name);
        dest.writeString(Surname);
        dest.writeString(Sex);
        dest.writeString(Info);
        dest.writeString(Birthday);
    }
}
