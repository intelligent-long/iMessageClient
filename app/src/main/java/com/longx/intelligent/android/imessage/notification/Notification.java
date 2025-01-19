package com.longx.intelligent.android.imessage.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.longx.intelligent.android.imessage.util.Utils;

/**
 * Created by LONG on 2024/1/25 at 1:18 PM.
 */
public class Notification {
    private Context context;
    private Intent intent;
    private Integer id = Utils.getRandomNumberInRange(1000, Integer.MAX_VALUE), smallIcon, importance = NotificationManager.IMPORTANCE_DEFAULT;
    private String channelId, channelName, title, text;
    private Boolean ongoing, autoCancel = true;
    private NotificationCompat.Style style = new NotificationCompat.BigTextStyle();

    public static class Builder{
        private Notification notification;
        public Builder(Context context, String channelId, String channelName){
            notification = new Notification();
            notification.context = context;
            notification.channelId = channelId;
            notification.channelName = channelName;
        }

        public Builder intent(Intent intent){
            notification.intent = intent;
            return this;
        }

        public Builder id(int id){
            notification.id = id;
            return this;
        }

        public Builder smallIcon(int iconResId){
            notification.smallIcon = iconResId;
            return this;
        }

        public Builder title(String title){
            notification.title = title;
            return this;
        }

        public Builder text(String text){
            notification.text = text;
            return this;
        }

        public Builder ongoing(boolean ongoing){
            notification.ongoing = ongoing;
            return this;
        }

        public Builder autoCancel(boolean autoCancel){
            notification.autoCancel = autoCancel;
            return this;
        }

        public Builder importance(int importance){
            notification.importance = importance;
            return this;
        }

        public Builder style(NotificationCompat.Style style){
            notification.style = style;
            return this;
        }

        public Notification build(){
            return notification;
        }
    }

    private Notification(){}

    public void show(){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setStyle(style)
                .setDefaults(android.app.Notification.DEFAULT_ALL)
                .setCategory(android.app.Notification.CATEGORY_EVENT);
        if(smallIcon != null){
            builder.setSmallIcon(smallIcon);
        }
        if (title != null){
            builder.setContentTitle(title);
        }
        if(text != null){
            builder.setContentText(text);
        }
        if(ongoing != null){
            builder.setOngoing(ongoing);
        }
        if(autoCancel != null){
            builder.setAutoCancel(autoCancel);
        }
        if(intent != null) {
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            }
            builder.setContentIntent(pendingIntent);
        }
        builder.setPriority(android.app.Notification.PRIORITY_MAX);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.setDescription(channelName);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        android.app.Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

}
