package com.longx.intelligent.android.imessage.behaviorcomponents;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.Application;
import com.longx.intelligent.android.imessage.activity.AuthActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.VersionActivity;
import com.longx.intelligent.android.imessage.activity.helper.ActivityOperator;
import com.longx.intelligent.android.imessage.da.database.DatabaseInitiator;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.OfflineDetail;
import com.longx.intelligent.android.imessage.data.Release;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.data.request.EmailLoginPostBody;
import com.longx.intelligent.android.imessage.data.request.ImessageIdUserLoginPostBody;
import com.longx.intelligent.android.imessage.data.request.SendVerifyCodePostBody;
import com.longx.intelligent.android.imessage.data.request.VerifyCodeLoginPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.fragment.main.BroadcastsFragment;
import com.longx.intelligent.android.imessage.net.CookieJar;
import com.longx.intelligent.android.imessage.net.retrofit.RetrofitCreator;
import com.longx.intelligent.android.imessage.net.retrofit.caller.AuthApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ImessageWebApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.UrlMapApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.notification.Notifications;
import com.longx.intelligent.android.imessage.service.ServerMessageService;
import com.longx.intelligent.android.imessage.util.AppUtil;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.OfflineDetailShowYier;
import com.longx.intelligent.android.imessage.yier.ResultsYier;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/3/30 at 3:49 PM.
 */
public class GlobalBehaviors {
    public enum LoginWay{IMESSAGE_ID, EMAIL, VERIFY_CODE}

    public static void doLogin(AppCompatActivity activity, String loginImessageIdUser, String loginEmail, String loginPassword, String loginVerifyCode, LoginWay loginWay) {
        switch (loginWay){
            case IMESSAGE_ID:{
                ImessageIdUserLoginPostBody postBody = new ImessageIdUserLoginPostBody(loginImessageIdUser, loginPassword);
                AuthApiCaller.imessageIdUserLogin(activity, postBody, new RetrofitApiCaller.CommonYier<OperationData>(activity){
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
        InitFetcher.doAll(context);
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
                            .create().show();
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
                    new CustomViewMessageDialog(activity, notice).create().show();
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
                    SharedPreferencesAccessor.ApiJson.OfflineDetails.addRecord(context, offlineDetail);
                    SharedPreferencesAccessor.AuthPref.saveOfflineDetailNeedFetch(context, false);
                    GlobalYiersHolder.getYiers(OfflineDetailShowYier.class).ifPresent(offlineDetailShowYiers -> {
                        offlineDetailShowYiers.forEach(OfflineDetailShowYier::showOfflineDetail);
                    });
                    if (!Application.foreground) {
                        Notifications.notifyGoOfflineBecauseOfOtherOnline(context, offlineDetail);
                    }
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

    public static void onAppNeedUpdate(Context context){
        try {
            ServerMessageService.stop();
        }catch (Exception ignore){}
        String title = "软件有新版本";
        String message = "你已下线，软件有新的版本，请更新软件。";
        Intent intent = new Intent(context, AuthActivity.class);
        intent.putExtra(ExtraKeys.TITLE, title);
        intent.putExtra(ExtraKeys.MESSAGE, message);
        ActivityOperator.switchTo(context, intent, true);
        if (!Application.foreground) {
            Notifications.notifyVersionCompatibilityOffline(context, title, message);
        }
        SharedPreferencesAccessor.NetPref.saveLoginState(context, false);
    }

    public static void onAppVersionHigher(Context context){
        try {
            ServerMessageService.stop();
        }catch (Exception ignore){}
        String title = "软件版本过高";
        String message = "你已下线，软件版本过高，请使用和服务端兼容的版本。";
        Intent intent = new Intent(context, AuthActivity.class);
        intent.putExtra(ExtraKeys.TITLE, title);
        intent.putExtra(ExtraKeys.MESSAGE, message);
        ActivityOperator.switchTo(context, intent, true);
        if (!Application.foreground) {
            Notifications.notifyVersionCompatibilityOffline(context, title, message);
        }
        SharedPreferencesAccessor.NetPref.saveLoginState(context, false);
    }

    public static void checkAndNotifySoftwareUpdate(AppCompatActivity activity){
        if(RetrofitCreator.retrofit == null) return;
        Date lastCheckSoftwareUpdatableTime = SharedPreferencesAccessor.DefaultPref.getLastCheckSoftwareUpdatableTime(activity);
        if(lastCheckSoftwareUpdatableTime != null) {
            if (!TimeUtil.isDateAfter(lastCheckSoftwareUpdatableTime, new Date(), Constants.MIN_CHECK_SOFTWARE_UPDATABLE_INTERVAL_MILLI_SEC)) {
                return;
            }
        }
        UrlMapApiCaller.fetchImessageWebUpdatableReleaseDataUrl(activity, new RetrofitApiCaller.BaseYier<OperationData>(){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    String updatableReleaseUrl = data.getData(String.class);
                    ImessageWebApiCaller.fetchUpdatableReleaseData(activity, updatableReleaseUrl, new RetrofitApiCaller.BaseCommonYier<OperationData>(activity) {
                        @Override
                        public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                            data.commonHandleResult(activity, new int[]{}, () -> {
                                Release updatableRelease = data.getData(Release.class);
                                if(SharedPreferencesAccessor.DefaultPref.getIgnoreUpdateVersionCode(activity) == updatableRelease.getVersionCode()) {
                                    return;
                                }
                                if (AppUtil.getVersionCode(activity) >= updatableRelease.getVersionCode()) {
                                    return;
                                }
                                String message = "有新的软件版本可更新。\n新版本: " + updatableRelease.getVersionName()
                                        + "\n新版本号: " + updatableRelease.getVersionCode()
                                        + "\n是否更新？";
                                ConfirmDialog updateDialog = new ConfirmDialog(activity, "软件更新", message, false);
                                updateDialog
                                        .setNegativeButton("下次提醒", (dialog, which) -> SharedPreferencesAccessor.DefaultPref.saveLastCheckSoftwareUpdatableTime(activity, new Date()))
                                        .setPositiveButton((dialog, which) -> activity.startActivity(new Intent(activity, VersionActivity.class)))
                                        .setNeutralButton("忽略此版本", v -> {
                                            new ConfirmDialog(activity)
                                                    .setNegativeButton()
                                                    .setPositiveButton((dialog1, which1) -> {
                                                        updateDialog.dismiss();
                                                        SharedPreferencesAccessor.DefaultPref.saveIgnoreUpdateVersionCode(activity, updatableRelease.getVersionCode());
                                                    })
                                                    .create().show();
                                        })
                                        .create().show();
                            });
                        }
                    });
                });
            }
        });
    }
}
