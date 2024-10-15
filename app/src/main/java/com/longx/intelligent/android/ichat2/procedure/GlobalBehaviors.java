package com.longx.intelligent.android.ichat2.procedure;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.Application;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.da.database.DatabaseInitiator;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.OfflineDetail;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.request.EmailLoginPostBody;
import com.longx.intelligent.android.ichat2.data.request.IchatIdUserLoginPostBody;
import com.longx.intelligent.android.ichat2.data.request.SendVerifyCodePostBody;
import com.longx.intelligent.android.ichat2.data.request.VerifyCodeLoginPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.dialog.MessageDialog;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.fragment.main.BroadcastsFragment;
import com.longx.intelligent.android.ichat2.net.CookieJar;
import com.longx.intelligent.android.ichat2.net.retrofit.RetrofitCreator;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.AuthApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.notification.Notifications;
import com.longx.intelligent.android.ichat2.service.ServerMessageService;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.OfflineDetailShowYier;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/3/30 at 3:49 PM.
 */
public class GlobalBehaviors {
    public enum LoginWay{ICHAT_ID, EMAIL, VERIFY_CODE}

    public static void doLogin(AppCompatActivity activity, String loginIchatIdUser, String loginEmail, String loginPassword, String loginVerifyCode, LoginWay loginWay) {
        switch (loginWay){
            case ICHAT_ID:{
                IchatIdUserLoginPostBody postBody = new IchatIdUserLoginPostBody(loginIchatIdUser, loginPassword);
                AuthApiCaller.ichatIdUserLogin(activity, postBody, new RetrofitApiCaller.CommonYier<OperationData>(activity){
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(activity, new int[]{-101, -102}, () -> {
                            Self self = data.getData(Self.class);
                            onLoginSuccess(activity, self);
                        });
                    }
                });
                break;
            }
            case EMAIL:{
                EmailLoginPostBody postBody = new EmailLoginPostBody(loginEmail, loginPassword);
                AuthApiCaller.emailLogin(activity, postBody, new RetrofitApiCaller.CommonYier<OperationData>(activity){
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(activity, new int[]{-101, -102}, () -> {
                            Self self = data.getData(Self.class);
                            onLoginSuccess(activity, self);
                        });
                    }
                });
                break;
            }
            case VERIFY_CODE:{
                VerifyCodeLoginPostBody postBody = new VerifyCodeLoginPostBody(loginEmail, loginVerifyCode);
                AuthApiCaller.verifyCodeLogin(activity, postBody, new RetrofitApiCaller.CommonYier<OperationData>(activity){
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(activity, new int[]{-101, -102, -103}, () -> {
                            Self self = data.getData(Self.class);
                            onLoginSuccess(activity, self);
                        });
                    }
                });
                break;
            }
        }
    }

    private static void onLoginSuccess(Context context, Self userInfo){
        SharedPreferencesAccessor.NetPref.saveLoginState(context, true);
        reloadWhenAccountSwitched(context, userInfo);
        ServerMessageService.work((Application) context.getApplicationContext());
        ActivityOperator.switchToMain(context);
    }

    public static void doLogout(Context context, String failureBaseUrl, ResultsYier resultsYier){
        tryLogout(context, null, null, results -> {
            Boolean success = (Boolean) results[0];
            Boolean failure = (Boolean) results[2];
            if (!success && failureBaseUrl != null && failure) {
                String cookie = CookieJar.getCookieString(RetrofitCreator.retrofit.baseUrl().toString());
                tryLogout(context, failureBaseUrl, cookie, resultsYier);
            } else {
                if (resultsYier != null) resultsYier.onResults(success);
            }
        });
    }

    private static void onLogoutSuccess(Context context){
        SharedPreferencesAccessor.NetPref.saveLoginState(context, false);
        try {
            ServerMessageService.stop();
        }catch (Exception ignore){}
        ActivityOperator.switchToAuth(context);
    }

    private static void tryLogout(Context context, String failureBaseUrl, String cookie, ResultsYier resultsYier){
        AppCompatActivity activity = context instanceof AppCompatActivity ? ((AppCompatActivity) context) : null;
        AuthApiCaller.logout(activity, failureBaseUrl, cookie, new RetrofitApiCaller.CommonYier<OperationStatus>(activity) {
            @Override
            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                data.commonHandleResult(activity, new int[]{}, () -> {
                    onLogoutSuccess(activity);
                    if (resultsYier != null) resultsYier.onResults(true, call, false);
                });
            }

            @Override
            public void notOk(int code, String message, Response<OperationStatus> row, Call<OperationStatus> call) {
                if(activity != null) {
                    new ConfirmDialog(activity, "状态码异常 (" + code + ")，是否强制退出登录？")
                            .setPositiveButton((dialog, which) -> {
                                onLogoutSuccess(activity);
                                if (resultsYier != null) resultsYier.onResults(true, call, false);
                            })
                            .setNegativeButton((dialog, which) -> {
                                if (resultsYier != null) resultsYier.onResults(false, call, false);
                            })
                            .forShow();
                }
            }

            @Override
            public void failure(Throwable t, Call<OperationStatus> call) {
                super.failure(t, call);
                if (resultsYier != null) resultsYier.onResults(false, call, true);
            }
        });
    }

    public static void reloadWhenAccountSwitched(Context context, Self self){
        SharedPreferencesAccessor.UserProfilePref.clear(context);
        ContentUpdater.updateCurrentUserProfile(context, self);
        DatabaseInitiator.initAll(context);
        BroadcastsFragment.needInitFetchBroadcast = true;
    }

    public static void sendVerifyCode(AppCompatActivity activity, String email) {
        SendVerifyCodePostBody postBody = new SendVerifyCodePostBody(email);
        AuthApiCaller.sendVerifyCode(activity, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(activity, true, true) {
            @Override
            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(activity, new int[]{-101, -102}, () -> {
                    String notice = data.getDetails().get("notice").get(0);
                    new MessageDialog(activity, notice).forShow();
                });
            }
        });
    }

    public static void onOtherOnline(Context context){
        SharedPreferencesAccessor.AuthPref.saveOfflineDetailNeedFetch(context, true);
        AuthApiCaller.fetchOfflineDetail(null, new RetrofitApiCaller.BaseYier<OperationData>(){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(null, new int[]{}, () -> {
                    OfflineDetail offlineDetail = data.getData(OfflineDetail.class);
                    if (ActivityOperator.getActivityList().isEmpty()) {
                        Notifications.notifyGoOfflineBecauseOfOtherOnline(context, offlineDetail);
                    }
                    SharedPreferencesAccessor.ApiJson.OfflineDetails.addRecord(context, offlineDetail);
                    SharedPreferencesAccessor.AuthPref.saveOfflineDetailNeedFetch(context, false);
                    GlobalYiersHolder.getYiers(OfflineDetailShowYier.class).ifPresent(offlineDetailShowYiers -> {
                        offlineDetailShowYiers.forEach(OfflineDetailShowYier::showOfflineDetail);
                    });
                }, new OperationStatus.HandleResult(-101, () -> {
                    ErrorLogger.log("获取离线详情 Code: " + data.getCode() + ", Message: " + data.getMessage());
                }), new OperationStatus.HandleResult(-200, () -> {
                    ErrorLogger.log("获取离线详情 Code: " + data.getCode() + ", Message: " + data.getMessage());
                }));
            }
        });
        SharedPreferencesAccessor.NetPref.saveLoginState(context, false);
        try {
            ServerMessageService.stop();
        }catch (Exception ignore){}
        ActivityOperator.switchToAuth(context);
    }
}
