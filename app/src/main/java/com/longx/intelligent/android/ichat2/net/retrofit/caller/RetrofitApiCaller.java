package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.dialog.OperationDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.RetrofitCreator;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.xcheng.retrofit.Callback;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by LONG on 2024/1/12 at 5:56 PM.
 */
public abstract class RetrofitApiCaller {

    protected static <T> T getApiImplementation(Class<T> clazz){
        return RetrofitCreator.retrofit.create(clazz);
    }

    protected static <T> T getApiImplementation(Retrofit retrofit, Class<T> clazz){
        return retrofit.create(clazz);
    }

    public interface Yier<T> {
        void start(Call<T> call);
        void ok(T data, Response<T> row, Call<T> call);
        void notOk(int code, String message, Response<T> row, Call<T> call);
        void failure(Throwable row, Call<T> call);
        void complete(Call<T> call);
    }

    public static class BaseYier<T> implements Callback<T>, Yier<T> {

        @Override
        public final void onStart(Call<T> call) {
            start(call);
        }

        @Override
        public final void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
            if (response.isSuccessful()) {
                ok(response.body(), response, call);
            } else {
                notOk(response.code(), response.message(), response, call);
            }
        }

        @Override
        public final void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            failure(t, call);
        }

        @Override
        public final void onCompleted(Call<T> call) {
            complete(call);
        }

        @Override
        public void start(Call<T> call) {

        }

        @Override
        public void ok(T data, Response<T> row, Call<T> call) {

        }

        @Override
        public void notOk(int code, String message, Response<T> row, Call<T> call) {

        }

        @Override
        public void failure(Throwable t, Call<T> call) {

        }

        @Override
        public void complete(Call<T> call) {

        }
    }

    public static class BaseCommonYier<T> extends BaseYier<T> {
        private AppCompatActivity activity;
        private boolean showErrorInfo = true;
        private boolean beCanceled;

        public BaseCommonYier() {
        }

        public BaseCommonYier(AppCompatActivity activity) {
            this.activity = activity;
        }

        public BaseCommonYier(AppCompatActivity activity, boolean showErrorInfo) {
            this(activity);
            this.showErrorInfo = showErrorInfo;
        }

        @Override
        public void notOk(int code, String message, Response<T> row, Call<T> call) {
            if(showErrorInfo) {
                if (activity != null) {
                    MessageDisplayer.autoShow(activity, "HTTP 状态码异常  >  " + code, MessageDisplayer.Duration.LONG);
                }
            }
        }

        @Override
        public void failure(Throwable t, Call<T> call) {
            if(beCanceled) return;
            ErrorLogger.log(getClass(), t);
            if(showErrorInfo) {
                if (activity != null) {
                    MessageDisplayer.autoShow(activity, "无法连接到服务器 > " + t.getClass().getName(), MessageDisplayer.Duration.LONG);
                }
            }
        }

        public AppCompatActivity getActivity() {
            return activity;
        }

        public void setBeCanceled(boolean beCanceled) {
            this.beCanceled = beCanceled;
        }
    }

    public static class CommonYier<T> extends BaseCommonYier<T> {
        private boolean showOperationDialog = true;
        private OperationDialog operationDialog;

        public CommonYier() {
        }

        public CommonYier(AppCompatActivity activity) {
            super(activity);
        }

        public CommonYier(AppCompatActivity activity, boolean showOperationDialog, boolean showErrorInfo) {
            super(activity, showErrorInfo);
            this.showOperationDialog = showOperationDialog;
        }

        @Override
        public void start(Call<T> call) {
            if(showOperationDialog){
                if(getActivity() != null) {
                    showOperationDialog(call);
                }
            }
        }

        protected void showOperationDialog(Call<T> call) {
            getActivity().runOnUiThread(() -> {
                operationDialog = new OperationDialog(getActivity(), () -> {
                    setBeCanceled(true);
                    call.cancel();
                });
                operationDialog.show();
            });
        }

        @Override
        public void complete(Call<T> call) {
            if (operationDialog != null) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> operationDialog.dismiss());
                }
            }
        }
    }

    public static class DelayedShowDialogCommonYier<T> extends CommonYier<T> {
        private OperationDialog operationDialog;
        private Timer waitToShowOperationDialogTimer;
        private long showOperationDialogDelay = 200L;
        private boolean showOperationDialogCanceled;

        public DelayedShowDialogCommonYier(AppCompatActivity activity) {
            super(activity);
        }

        public DelayedShowDialogCommonYier(AppCompatActivity activity, long showOperationDialogDelay) {
            this(activity, showOperationDialogDelay, true);
        }

        public DelayedShowDialogCommonYier(AppCompatActivity activity, long showOperationDialogDelay, boolean showErrorInfo) {
            super(activity, true, showErrorInfo);
            this.showOperationDialogDelay = showOperationDialogDelay;
        }

        @Override
        public synchronized void start(Call<T> call) {
            if (showOperationDialogDelay > 0) {
                waitToShowOperationDialogTimer = new Timer();
                waitToShowOperationDialogTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        showOperationDialog(call);
                    }
                }, showOperationDialogDelay);
            } else {
                showOperationDialog(call);
            }
        }

        @Override
        public synchronized void complete(Call<T> call) {
            showOperationDialogCanceled = true;
            if (operationDialog != null) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        synchronized (DelayedShowDialogCommonYier.this) {
                            operationDialog.dismiss();
                            if(waitToShowOperationDialogTimer != null) {
                                waitToShowOperationDialogTimer.cancel();
                            }
                        }
                    });
                }
            }
        }

        protected void showOperationDialog(Call<T> call) {
            getActivity().runOnUiThread(() -> {
                synchronized (DelayedShowDialogCommonYier.this) {
                    if (!showOperationDialogCanceled) {
                        operationDialog = new OperationDialog(getActivity(), () -> {
                            setBeCanceled(true);
                            call.cancel();
                        });
                        operationDialog.show();
                    }
                }
            });
        }
    }
}
