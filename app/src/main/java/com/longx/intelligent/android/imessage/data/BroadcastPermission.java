package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LONG on 2024/10/17 at 4:21 PM.
 */
public class BroadcastPermission implements Parcelable {
    @JsonIgnore
    private String stability;

    public String getStability() {
        return stability;
    }

    public static final int PUBLIC = 0;
    public static final int PRIVATE = 1;
    public static final int CONNECTED_CHANNEL_CIRCLE = 2;

    private String broadcastId;
    private int permission;
    private Set<String> excludeConnectedChannels;

    public BroadcastPermission() {
    }

    public BroadcastPermission(String broadcastId, int permission, Set<String> excludeConnectedChannels) {
        this.broadcastId = broadcastId;
        this.permission = permission;
        this.excludeConnectedChannels = excludeConnectedChannels;
    }

    public static final Creator<BroadcastPermission> CREATOR = new Creator<BroadcastPermission>() {
        @Override
        public BroadcastPermission createFromParcel(Parcel in) {
            return new BroadcastPermission(in);
        }

        @Override
        public BroadcastPermission[] newArray(int size) {
            return new BroadcastPermission[size];
        }
    };

    public String getBroadcastId() {
        return broadcastId;
    }

    public int getPermission() {
        return permission;
    }

    public Set<String> getExcludeConnectedChannels() {
        return excludeConnectedChannels;
    }

    public void setBroadcastId(String broadcastId) {
        this.broadcastId = broadcastId;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public void setExcludeConnectedChannels(Set<String> excludeConnectedChannels) {
        this.excludeConnectedChannels = excludeConnectedChannels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(broadcastId);
        dest.writeInt(permission);
        List<String> excludeChannelsList = new ArrayList<>(excludeConnectedChannels);
        dest.writeStringList(excludeChannelsList);
    }

    protected BroadcastPermission(Parcel in) {
        broadcastId = in.readString();
        permission = in.readInt();
        List<String> excludeChannelsList = in.createStringArrayList();
        excludeConnectedChannels = new HashSet<>(excludeChannelsList);
    }
}
