package com.longx.intelligent.android.ichat2.notification;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.longx.intelligent.android.ichat2.R;

/**
 * Created by LONG on 2024/4/6 at 5:51 PM.
 */
public class Notifications {

    public static void notifyServerMessageServiceNotRunning(Context context){
        new Notification.Builder(context,
                NotificationChannels.ServerMessageServiceNotRunning.ID_SERVER_MESSAGE_SERVICE_NOT_RUNNING,
                NotificationChannels.ServerMessageServiceNotRunning.NAME_SERVER_MESSAGE_SERVICE_NOT_RUNNING)
                .title(context.getString(R.string.notification_title_server_message_service_not_running))
                .text(context.getString(R.string.notification_message_server_message_service_not_running))
                .smallIcon(R.drawable.error_fill_24px)
                .importance(NotificationManager.IMPORTANCE_HIGH)
                .build()
                .show();
    }
}
