package com.hyphenate.easeui.game.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hawk on 2017/1/3.
 */

public class LayoutItem implements Parcelable {
    private int id;
    private String name;
    private String data;

    public LayoutItem(int id,String name,String data){
        this.id=id;
        this.name=name;
        this.data=data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.data);
    }

    protected LayoutItem(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.data = in.readString();
    }

    public static final Parcelable.Creator<LayoutItem> CREATOR = new Parcelable.Creator<LayoutItem>() {
        @Override
        public LayoutItem createFromParcel(Parcel source) {
            return new LayoutItem(source);
        }

        @Override
        public LayoutItem[] newArray(int size) {
            return new LayoutItem[size];
        }
    };
}
