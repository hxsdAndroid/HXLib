package com.netease.nim.uikit.business.chatroom.module;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muwenlei on 2018/2/5.
 */

public class NimUserProps implements Parcelable{

    private int tag;
    private String tagName;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public NimUserProps(int tag, String tagName) {
        this.tag = tag;
        this.tagName = tagName;
    }

    public NimUserProps() {
        super();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected NimUserProps(Parcel in) {
        tag = in.readInt();
        tagName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(tag);
        dest.writeString(tagName);
    }

    public static final Creator<NimUserProps> CREATOR = new Creator<NimUserProps>() {
        @Override
        public NimUserProps createFromParcel(Parcel in) {
            return new NimUserProps(in);
        }

        @Override
        public NimUserProps[] newArray(int size) {
            return new NimUserProps[size];
        }
    };
}
