package com.longx.intelligent.android.ichat2.behavior;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.privatefile.PrivateFilesAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.SelfInfo;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.ichat2.util.FileUtil;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/4/1 at 4:41 AM.
 */
public class ContentUpdater {
    private static final List<String> updatingIds = new ArrayList<>();

    public interface OnServerContentUpdateYier {
        String ID_CURRENT_USER_INFO = "current_user_info";
        String ID_CHANNEL_ADDITION_ACTIVITIES = "channel_addition_activities";

        void onStartUpdate(String id);

        void onUpdateComplete(String id);
    }

    private static class ContentUpdateApiYier<T> extends RetrofitApiCaller.BaseYier<T>{
        private final String updateId;
        private final Context context;

        public ContentUpdateApiYier(String updateId, Context context) {
            this.updateId = updateId;
            this.context = context;
        }

        @Override
        public void start(Call<T> call) {
            synchronized (ContentUpdater.class) {
                updatingIds.add(updateId);
                super.start(call);
                onStartUpdate(updateId);
            }
        }

        @Override
        public void notOk(int code, String message, Response<T> row, Call<T> call) {
            super.notOk(code, message, row, call);
            MessageDisplayer.autoShow(context, "数据更新 HTTP 状态码异常 (" + updateId + ")  >  " + code, MessageDisplayer.Duration.LONG);
        }

        @Override
        public void failure(Throwable t, Call<T> call) {
            super.failure(t, call);
            MessageDisplayer.autoShow(context, "数据更新出错 (" + updateId + ")  >  " + t.getClass().getName(), MessageDisplayer.Duration.LONG);
        }

        @Override
        public void complete(Call<T> call) {
            synchronized (ContentUpdater.class) {
                updatingIds.remove(updateId);
                super.complete(call);
                onUpdateComplete(updateId);
            }
        }
    };

    public static boolean isUpdating(){
        return updatingIds.size() != 0;
    }

    public static List<String> getUpdatingIds() {
        return updatingIds;
    }

    private static void onStartUpdate(String updateId) {
        GlobalYiersHolder.getYiers(OnServerContentUpdateYier.class)
                .ifPresent(onServerContentUpdateYiers -> onServerContentUpdateYiers.forEach(onServerContentUpdateYier -> {
                    onServerContentUpdateYier.onStartUpdate(updateId);
                }));
    }

    private static void onUpdateComplete(String updateId) {
        GlobalYiersHolder.getYiers(OnServerContentUpdateYier.class)
                .ifPresent(onServerContentUpdateYiers -> onServerContentUpdateYiers.forEach(onServerContentUpdateYier -> {
                    onServerContentUpdateYier.onUpdateComplete(updateId);
                }));
    }

    public static void updateCurrentUserInfo(Context context){
        updateCurrentUserInfo(context, null);
    }

    public static void updateCurrentUserInfo(Context context, SelfInfo selfInfo) {
        if(selfInfo != null){
            SharedPreferencesAccessor.UserInfoPref.saveCurrentUserInfo(context, selfInfo);
        }else {
            UserApiCaller.whoAmI(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CURRENT_USER_INFO, context) {
                @Override
                public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                    super.ok(data, row, call);
                    data.commonHandleSuccessResult(() -> {
                        SelfInfo selfInfo = data.getData(SelfInfo.class);
                        SharedPreferencesAccessor.UserInfoPref.saveCurrentUserInfo(context, selfInfo);
                    });
                }
            });
        }
    }

    public static void updateChannelAdditionNotViewCount(Context context){
        ChannelApiCaller.fetchChannelAdditionNotViewCount(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CHANNEL_ADDITION_ACTIVITIES, context){
            @Override
            public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                super.ok(data, row, call);
                Integer notViewCount = data.getData(Integer.class);
                SharedPreferencesAccessor.NewContentCount.saveChannelAdditionActivities(context, notViewCount);
            }
        });
    }

}
