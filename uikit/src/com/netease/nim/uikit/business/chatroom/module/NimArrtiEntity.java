package com.netease.nim.uikit.business.chatroom.module;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by muwenlei on 2018/2/2.
 */

public class NimArrtiEntity implements Parcelable{
   private String name;
   private NimUserProps props;
   private String roomId;
   private String account;
   private String token;
   private List<String> address=new ArrayList<>();
   private String appKey;
   private String chatRoomId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NimUserProps getProps() {
        return props;
    }

    public void setProps(NimUserProps props) {
        this.props = props;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public NimArrtiEntity() {
        super();
    }

    public NimArrtiEntity(String name, NimUserProps props, String roomId, String account, String token, List<String> address, String appKey, String chatRoomId) {
        this.name = name;
        this.props = props;
        this.roomId = roomId;
        this.account = account;
        this.token = token;
        this.address = address;
        this.appKey = appKey;
        this.chatRoomId = chatRoomId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(props,flags);
        dest.writeString(roomId);
        dest.writeString(account);
        dest.writeString(token);
        dest.writeStringList(address);
        dest.writeString(appKey);
        dest.writeString(chatRoomId);
    }
    public NimArrtiEntity(Parcel source) {
        name = source.readString();
        props = source.readParcelable(NimUserProps.class.getClassLoader());
        roomId = source.readString();
        account = source.readString();
        token = source.readString();
        address = source.createStringArrayList();
        appKey = source.readString();
        chatRoomId = source.readString();
    }


    public static final Creator<NimArrtiEntity> CREATOR = new Creator<NimArrtiEntity>() {
        @Override
        public NimArrtiEntity createFromParcel(Parcel in) {
            return new NimArrtiEntity(in);
        }

        @Override
        public NimArrtiEntity[] newArray(int size) {
            return new NimArrtiEntity[size];
        }
    };

}
