package com.longx.intelligent.android.imessage.net.retrofit.caller;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.imessage.Application;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.da.FileHelper;
import com.longx.intelligent.android.imessage.da.ProgressCallback;
import com.longx.intelligent.android.imessage.dialog.OperatingDialog;
import com.longx.intelligent.android.imessage.dialog.ProgressOperatingDialog;
import com.longx.intelligent.android.imessage.net.retrofit.RetrofitCreator;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.yier.ResultsYier;
import com.xcheng.retrofit.Callback;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by LONG on 2024/1/12 at 5:56 PM.
 */
public abstract class RetrofitApiCaller {

    protected static <T> T getApiImplementation(Class<T> clazz){
        if(RetrofitCreator.retrofit == null){
            RetrofitCreator.create(Application.application);
        }
        return RetrofitCreator.retrofit.create(clazz);
    }

    protected static <T> T getApiImplementation(Retrofit retrofit, Class<T> clazz){
        return retrofit.create(clazz);
    }

    public interface Yier<T> {
        void start(Call<T> call);
        void ok(T data, Response<T> raw, Call<T> call);
        void notOk(int code, String message, Response<T> raw, Call<T> call);
        void failure(Throwable raw, Call<T> call);
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
        public void ok(T data, Response<T> raw, Call<T> call) {

        }

        @Override
        public void notOk(int code, String message, Response<T> raw, Call<T> call) {

        }

        @Override
        public void failure(Throwable t, Call<T> call) {

        }

        @Override
        public void complete(Call<T> call) {

        }
    }

    public static class FailureResponseCodeException extends Exception{
        public FailureResponseCodeException(String message) {
            super(message);
        }
    }

    public static class BaseCommonYier<T> extends BaseYier<T> {
        private Context context;
        private boolean showErrorInfo = true;
        private boolean beCanceled;
        private boolean showWrongMessageOnlyWithToast;

        public BaseCommonYier() {
        }

        public BaseCommonYier(Context context) {
            this.context = context;
        }

        public BaseCommonYier(Activity activity, boolean showErrorInfo) {
            this(activity);
            this.showErrorInfo = showErrorInfo;
        }

        public BaseCommonYier<T> showWrongMessageOnlyWithToast(boolean showWrongMessageOnlyWithToast) {
            this.showWrongMessageOnlyWithToast = showWrongMessageOnlyWithToast;
            return this;
        }

        @Override
        public void notOk(int code, String message, Response<T> raw, Call<T> call) {
            ErrorLogger.log(getClass(), new FailureResponseCodeException("HTTP 状态码异常 [" + raw.raw().request().url() +"]  >  " + code));
            if(showErrorInfo) {
                if (context != null) {
                    String showMessage = "HTTP 状态码异常  >  " + code;
                    if(showWrongMessageOnlyWithToast){
                        MessageDisplayer.showToast(context, showMessage, Toast.LENGTH_LONG);
                    }else {
                        MessageDisplayer.autoShow(context, showMessage, MessageDisplayer.Duration.LONG);
                    }
                }
            }
        }

        @Override
        public void failure(Throwable t, Call<T> call) {
            if(beCanceled) return;
            ErrorLogger.log(getClass(), t);
            if(showErrorInfo) {
                if (context != null) {
                    String showMessage = "无法连接到服务器 > " + t.getClass().getName();
                    if(showWrongMessageOnlyWithToast){
                        MessageDisplayer.showToast(context, showMessage, Toast.LENGTH_LONG);
                    }else {
                        MessageDisplayer.autoShow(context, showMessage, MessageDisplayer.Duration.LONG);
                    }
                }
            }
        }

        public void setBeCanceled(boolean beCanceled) {
            this.beCanceled = beCanceled;
        }

        public Context getContext() {
            return context;
        }
    }

    public static class CommonYier<T> extends BaseCommonYier<T> {
        private Activity activity;
        private boolean showOperationDialog = true;
        private OperatingDialog operatingDialog;
        private boolean canceled;

        public CommonYier() {
        }

        public CommonYier(Activity activity) {
            super(activity);
            this.activity = activity;
        }

        public CommonYier(Activity activity, boolean showOperationDialog, boolean showErrorInfo) {
            super(activity, showErrorInfo);
            this.activity = activity;
            this.showOperationDialog = showOperationDialog;
        }

        @Override
        public void start(Call<T> call) {
            if(showOperationDialog){
                if(getActivity() != null) {
                    runAction(call);
                }
            }
        }

        protected void runAction(Call<T> call) {
            getActivity().runOnUiThread(() -> {
                operatingDialog = new OperatingDialog(getActivity(), () -> {
                    setBeCanceled(true);
                    call.cancel();
                    onCancel();
                });
                operatingDialog.create().show();
            });
        }

        @Override
        public void complete(Call<T> call) {
            if (operatingDialog != null) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> operatingDialog.dismiss());
                }
            }
        }

        public void onCancel(){
            canceled = true;
        }

        public Activity getActivity() {
            return activity;
        }

        protected OperatingDialog getOperatingDialog() {
            return operatingDialog;
        }

        protected boolean isCanceled(){
            return canceled;
        }
    }

    public static class DelayedShowDialogCommonYier<T> extends CommonYier<T> {
        private OperatingDialog operatingDialog;
        private Timer waitToShowOperationDialogTimer;
        private long showOperationDialogDelay = 300L;
        private boolean showOperationDialogCanceled;

        public DelayedShowDialogCommonYier(Activity activity) {
            super(activity);
        }

        public DelayedShowDialogCommonYier(Activity activity, long showOperationDialogDelay) {
            this(activity, showOperationDialogDelay, true);
        }

        public DelayedShowDialogCommonYier(Activity activity, long showOperationDialogDelay, boolean showErrorInfo) {
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
                        showOperatingDialog(call);
                    }
                }, showOperationDialogDelay);
            } else {
                showOperatingDialog(call);
            }
        }

        @Override
        public synchronized void complete(Call<T> call) {
            showOperationDialogCanceled = true;
            if (operatingDialog != null) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        synchronized (DelayedShowDialogCommonYier.this) {
                            operatingDialog.dismiss();
                            if(waitToShowOperationDialogTimer != null) {
                                waitToShowOperationDialogTimer.cancel();
                            }
                        }
                    });
                }
            }
        }

        protected void showOperatingDialog(Call<T> call) {
            getActivity().runOnUiThread(() -> {
                synchronized (DelayedShowDialogCommonYier.this) {
                    if (!showOperationDialogCanceled) {
                        operatingDialog = new OperatingDialog(getActivity(), () -> {
                            setBeCanceled(true);
                            call.cancel();
                        });
                        operatingDialog.create().show();
                    }
                }
            });
        }
    }

    public static class DelayedActionCommonYier<T> extends CommonYier<T> {
        private final ResultsYier resultsYier;
        private Timer waitToShowOperationDialogTimer;
        private static long runActionDelay = 300L;
        private boolean showOperationDialogCanceled;

        public DelayedActionCommonYier(Activity activity, ResultsYier resultsYier) {
            this(activity, runActionDelay, resultsYier);
        }

        public DelayedActionCommonYier(Activity activity, long runActionDelay, ResultsYier resultsYier) {
            this(activity, runActionDelay, true, resultsYier);
        }

        public DelayedActionCommonYier(Activity activity, boolean showErrorInfo, ResultsYier resultsYier) {
            this(activity, runActionDelay, showErrorInfo, resultsYier);
        }

        public DelayedActionCommonYier(Activity activity, long runActionDelay, boolean showErrorInfo, ResultsYier resultsYier) {
            super(activity, true, showErrorInfo);
            DelayedActionCommonYier.runActionDelay = runActionDelay;
            this.resultsYier = resultsYier;
        }

        @Override
        public synchronized void start(Call<T> call) {
            if (runActionDelay > 0) {
                waitToShowOperationDialogTimer = new Timer();
                waitToShowOperationDialogTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runAction(call);
                    }
                }, runActionDelay);
            } else {
                runAction(call);
            }
        }

        @Override
        public synchronized void complete(Call<T> call) {
            showOperationDialogCanceled = true;
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        synchronized (DelayedActionCommonYier.this) {
                            resultsYier.onResults(false, call);
                            if(waitToShowOperationDialogTimer != null) {
                                waitToShowOperationDialogTimer.cancel();
                            }
                        }
                    });
                }
        }

        protected void runAction(Call<T> call) {
            getActivity().runOnUiThread(() -> {
                synchronized (DelayedActionCommonYier.this) {
                    if (!showOperationDialogCanceled) {
                        resultsYier.onResults(true, call);
                    }
                }
            });
        }
    }

    public static class DownloadCommonYier extends CommonYier<okhttp3.ResponseBody> {
        private final String saveTo;
        private final ProgressCallback progressCallback;
        private final boolean[] cancel = new boolean[1];
        private ProgressOperatingDialog progressOperatingDialog;
        private final boolean showProgressDialog;
        private final ResultsYier resultsYier;

        public DownloadCommonYier(Activity activity, String saveTo, boolean showProgressDialog, ResultsYier resultsYier) {
            this(activity, saveTo, showProgressDialog, null, resultsYier);
        }

        public DownloadCommonYier(Activity activity, String saveTo, boolean showProgressDialog, ProgressCallback progressCallback, ResultsYier resultsYier) {
            super(activity, false, true);
            this.showProgressDialog = showProgressDialog;
            this.saveTo = saveTo;
            this.progressCallback = progressCallback;
            this.resultsYier = resultsYier;
        }

        @Override
        public void start(Call<ResponseBody> call) {
            super.start(call);
            if(getActivity() != null && showProgressDialog) {
                progressOperatingDialog = new ProgressOperatingDialog(getActivity(), () -> {
                    setBeCanceled(true);
                    call.cancel();
                    onCancel();
                });
                progressOperatingDialog.create();
                progressOperatingDialog.updateText("下载中...");
                progressOperatingDialog.show();
            }
        }

        @Override
        public void ok(ResponseBody data, Response<ResponseBody> raw, Call<ResponseBody> call) {
            new Thread(() -> {
                try (InputStream inputStream = data.source().inputStream()) {
                    long contentLength = data.contentLength();
                    if (progressCallback != null) {
                        FileHelper.save(inputStream, saveTo, contentLength, progressCallback, cancel);
                    } else if (progressOperatingDialog != null) {
                        FileHelper.save(inputStream, saveTo, contentLength, (current, total) -> {
                            progressOperatingDialog.updateProgress(current, total);
                        }, cancel);
                        progressOperatingDialog.dismiss();
                    }
                    resultsYier.onResults(Boolean.TRUE, saveTo);
                } catch (IOException e) {
                    ErrorLogger.log(e);
                    resultsYier.onResults(Boolean.FALSE, saveTo);
                }
            }).start();
        }

        @Override
        public void onCancel() {
            super.onCancel();
            cancel[0] = isCanceled();
            resultsYier.onResults(null, saveTo);
        }
    }
}
