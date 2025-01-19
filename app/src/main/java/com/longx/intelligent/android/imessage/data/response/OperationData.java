package com.longx.intelligent.android.imessage.data.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.longx.intelligent.android.imessage.util.JsonUtil;

public class OperationData extends OperationStatus{

    private Object data;

    public OperationData(){
        super();
    }

    public OperationData(int code, String message) {
        super(code, message);
    }

    public OperationData(int code, String message, Object data) {
        super(code, message);
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public <T> T getData(Class<T> clazz) {
        return JsonUtil.convertValue(data, clazz);
    }

    public <T> T getData(TypeReference<T> typeReference){
        return JsonUtil.convertValue(data, typeReference);
    }

    public String getJson(){
        return JsonUtil.toJson(this);
    }
}
