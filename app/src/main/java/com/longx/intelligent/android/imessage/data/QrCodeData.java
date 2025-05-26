package com.longx.intelligent.android.imessage.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longx.intelligent.android.imessage.util.JsonUtil;

import java.util.Objects;

public class QrCodeData<T> {
    private Type type;
    private T data;

    public enum Type { CHANNEL, GROUP_CHANNEL }

    public QrCodeData() {}

    public QrCodeData(Type type, T data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public T getData() {
        return data;
    }

    public static QrCodeData<?> toObject(String json) throws JsonProcessingException {
        JsonNode root = new ObjectMapper().readTree(json);
        String typeStr = root.get("type").asText();
        QrCodeData<?> result;
        if (Type.CHANNEL.name().equals(typeStr)) {
            result = JsonUtil.toObject(json, new TypeReference<QrCodeData<ChannelQrCode>>() {
            });
        } else if (Type.GROUP_CHANNEL.name().equals(typeStr)) {
            result = JsonUtil.toObject(json, new TypeReference<QrCodeData<GroupChannelQrCode>>() {
            });
        } else {
            throw new IllegalArgumentException("未知类型");
        }
        return result;
    }

    public <T> T getData(Class<T> clazz) {
        return (T) data;
    }
}