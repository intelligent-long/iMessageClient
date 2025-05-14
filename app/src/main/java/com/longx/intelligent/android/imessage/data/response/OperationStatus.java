package com.longx.intelligent.android.imessage.data.response;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OperationStatus {
    private int code;
    private String message;
    private final Map<String, List<String>> details = new LinkedHashMap<>();

    public OperationStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public OperationStatus() {

    }

    public static OperationStatus success(){
        return new OperationStatus(0, "成功");
    }

    public static OperationStatus failure(){
        return new OperationStatus(-200, "失败");
    }

    public void putDetail(String key, String value){
        if(details.containsKey(key)){
            details.get(key).add(value);
        }else {
            List<String> messages = new ArrayList<>();
            messages.add(value);
            details.put(key, messages);
        }
    }

    public void putDetails(Map<String, List<String>> details){
        this.details.putAll(details);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, List<String>> getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "OperationStatus{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", details=" + details +
                '}';
    }

    public String buildMessageOfValidationError(){
        StringBuilder builder = new StringBuilder();
        this.getDetails().values().forEach(
                stringList -> stringList.forEach(s -> builder.append(s).append("\n")));
        try {
            builder.delete(builder.length() - 1, builder.length());
        }catch (StringIndexOutOfBoundsException ignored){}
        return builder.toString();
    }

    public String buildMessageOfOperationStatusMessage(){
        return this.getMessage();
    }

    public String buildMessageOfOperationStatus(){
        StringBuilder builder = new StringBuilder();
        builder.append("Code: ")
                .append(this.getCode())
                .append("\n")
                .append("Message: ")
                .append(this.getMessage());
        if (this.getDetails().size() != 0) {
            builder.append("\n").append("Details: ->");
            this.getDetails().entrySet().forEach(stringStringEntry -> {
                builder.append("\n" + stringStringEntry.getKey() + ": ");
                stringStringEntry.getValue().forEach(value -> builder.append(value + " | "));
            });
        }
        return builder.toString();
    }

    public String buildFailureMessage(int[] messageCodes, int[] excludeCodes){
        for (int excludeCode : excludeCodes) {
            if(code == excludeCode){
                return null;
            }
        }
        if(code == 0) return null;
        if(code == -200) return message;
        if(code == -300) return buildMessageOfValidationError();
        for (int messageCode : messageCodes) {
            if(messageCode == code){
                return buildMessageOfOperationStatusMessage();
            }
        }
        return buildMessageOfOperationStatus();
    }

    public interface ResultHandler {
        void handle();
    }

    public static class HandleResult {
        private final int code;
        private final ResultHandler resultHandler;

        public HandleResult(int code, ResultHandler resultHandler) {
            this.code = code;
            this.resultHandler = resultHandler;
        }
    }

    public <T extends OperationStatus> void commonHandleResult(Activity activity, int[] messageCodes, ResultHandler successHandler, HandleResult... handleResult){
        if(getCode() == 0){
            if(successHandler != null) successHandler.handle();
        } else {
            //失败时的统一弹窗提示
            int[] excludeCodes = new int[handleResult.length];
            for (int i = 0; i < handleResult.length; i++) {
                excludeCodes[i] = handleResult[i].code;
            }
            String failureMessage = buildFailureMessage(messageCodes, excludeCodes);
            if(failureMessage != null) {
                new CustomViewMessageDialog((AppCompatActivity) activity, failureMessage).create().show();
            }
        }
        //处理其他结果
        for (HandleResult result : handleResult) {
            if(result.code == code && code != 0){
                result.resultHandler.handle();
                break;
            }
        }
    }

    public <T extends OperationStatus> void commonHandleResult(Activity activity, int[] messageCodes, ResultHandler successHandler){
        commonHandleResult(activity, messageCodes, successHandler, new HandleResult[0]);
    }

    public <T extends OperationStatus> void commonHandleSuccessResult(ResultHandler successHandler){
        if(getCode() == 0){
            if(successHandler != null) successHandler.handle();
        }
    }
}
