package com.longx.intelligent.android.ichat2.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Created by LONG on 2024/6/3 at 5:34 PM.
 */
public class ChannelTag implements Parcelable {
    private String id;
    private String ichatId;
    private String name;
    private int order;
    private List<String> channelIchatIdList;

    public ChannelTag() {
    }

    public ChannelTag(String id, String ichatId, String name, int order, List<String> channelIchatIdList) {
        this.id = id;
        this.ichatId = ichatId;
        this.name = name;
        this.order = order;
        this.channelIchatIdList = channelIchatIdList;
    }

    public static final Creator<ChannelTag> CREATOR = new Creator<ChannelTag>() {
        @Override
        public ChannelTag createFromParcel(Parcel in) {
            return new ChannelTag(in);
        }

        @Override
        public ChannelTag[] newArray(int size) {
            return new ChannelTag[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getIchatId() {
        return ichatId;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<String> getChannelIchatIdList() {
        return channelIchatIdList;
    }

    public void setChannelIchatIdList(List<String> channelIchatIdList) {
        this.channelIchatIdList = channelIchatIdList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ChannelTag(Parcel in) {
        id = in.readString();
        ichatId = in.readString();
        name = in.readString();
        order = in.readInt();
        channelIchatIdList = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ichatId);
        dest.writeString(name);
        dest.writeInt(order);
        dest.writeStringList(channelIchatIdList);
    }
}
