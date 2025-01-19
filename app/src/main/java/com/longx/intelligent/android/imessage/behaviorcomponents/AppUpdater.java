package com.longx.intelligent.android.imessage.behaviorcomponents;

import android.app.Activity;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.da.cachefile.CacheFilesAccessor;
import com.longx.intelligent.android.imessage.dialog.ProgressOperatingDialog;
import com.longx.intelligent.android.imessage.net.download.Downloader;
import com.longx.intelligent.android.imessage.util.ColorUtil;

/**
 * Created by LONG on 2024/10/30 at 上午7:13.
 */
public class AppUpdater {
    private final Activity activity;
    private final String url;
    private final ProgressOperatingDialog progressOperatingDialog;
    private Downloader downloader;

    public AppUpdater(Activity activity, String url) {
        this.activity = activity;
        this.url = url;
        progressOperatingDialog = new ProgressOperatingDialog(activity, () -> {
            if(downloader != null) downloader.stop();
        });
    }

    public void start(){
        progressOperatingDialog.create().show();
        progressOperatingDialog.updateText("准备中...");

        downloader = new Downloader(url, CacheFilesAccessor.App.prepareAppUpdateCacheFile(activity));
        downloader
                .setSuccessYier(file -> {
                    progressOperatingDialog.dismiss();
                    AppInstaller.installApk(activity, file.getAbsolutePath());
                })
                .setFailureYier(failure -> {
                    progressOperatingDialog.getBinding().indicator.setIndicatorColor(ColorUtil.getColor(activity, R.color.negative_red));
                    progressOperatingDialog.updateText("下载失败 > " + failure.getException().getClass().getName());
                })
                .setProgressYier(process -> {
                    progressOperatingDialog.updateText("下载中...");
                    progressOperatingDialog.updateProgress(process.getDoneBytes(), process.getTotalBytes());
                })
                .start();
    }
}
