package com.longx.intelligent.android.ichat2.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.longx.intelligent.android.ichat2.behaviorcomponents.MessageDisplayer;

import java.io.File;

/**
 * Created by LONG on 2024/10/30 at 上午9:03.
 */
public class AppInstaller {
    public static void installApk(Context context, String filePath) {
        File apkFile = new File(filePath);
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri;
            apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else {
            ErrorLogger.log("应用安装文件不存在");
            MessageDisplayer.autoShow(context, "应用安装文件不存在", MessageDisplayer.Duration.LONG);
        }
    }
}
