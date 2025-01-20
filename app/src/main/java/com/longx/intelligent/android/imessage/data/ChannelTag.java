package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2024/6/3 at 5:34 PM.
 */
public class ChannelTag implements Parcelable {
    private String tagId;
    private String imessageId;
    private String name;
    private int order;
    private List<String> channelImessageIdList;

    public ChannelTag() {
    }

    public ChannelTag(String tagId, String imessageId, String name, int order, List<String> channelImessageIdList) {
        this.tagId = tagId;
        this.imessageId = imessageId;
        this.name = name;
        this.order = order;
        this.channelImessageIdList = channelImessageIdList;
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

    public String getTagId() {
        return tagId;
    }

    public String getImessageId() {
        return imessageId;
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

    public List<String> getChannelImessageIdList() {
        return channelImessageIdList;
    }

    public void setChannelImessageIdList(List<String> channelImessageIdList) {
        this.channelImessageIdList = channelImessageIdList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ChannelTag(Parcel in) {
        tagId = in.readString();
        imessageId = in.readString();
        name = in.readString();
        order = in.readInt();
        channelImessageIdList = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(tagId);
        dest.writeString(imessageId);
        dest.writeString(name);
        dest.writeInt(order);
        dest.writeStringList(channelImessageIdList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelTag that = (ChannelTag) o;
        return order == that.order && Objects.equals(tagId, that.tagId) && Objects.equals(imessageId, that.imessageId) && Objects.equals(name, that.name) && Objects.equals(channelImessageIdList, that.channelImessageIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, imessageId, name, order, channelImessageIdList);
    }
}
