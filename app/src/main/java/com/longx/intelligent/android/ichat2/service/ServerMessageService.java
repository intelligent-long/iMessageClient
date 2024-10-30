package com.longx.intelligent.android.ichat2.service;

import android.annotation.SuppressLint;
import android.content.Context;

import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.ForegroundNotification;
import com.fanjun.keeplive.config.KeepLiveService;
import com.longx.intelligent.android.ichat2.Application;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.interfce.OnlineKeeper;
import com.longx.intelligent.android.ichat2.net.stomp.ServerMessageServiceStomp;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.ServerEventYier;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LONG on 2024/4/4 at 8:55 PM.
 */
public class ServerMessageService implements KeepLiveService, ServerEventYier, OnlineKeeper {
    private static final int TRY_BACK_ONLINE_INTERVAL_MILLI_SEC = 20 * 1000;
    @SuppressLint("StaticFieldLeak")
    private static ServerMessageService instance;
    private final Context CONTEXT;
    private boolean working;
    private boolean backingOnline;
    private boolean online;
    private Timer backingOnlineTimer;

    public static synchronized void work(Application application){
        if(ServerMessageService.instance == null){
            ServerMessageService.instance = new ServerMessageService(application);
            ForegroundNotification foregroundNotification = new ForegroundNotification(
                    application.getString(R.string.notification_title_server_message_service_foreground),
                    application.getString(R.string.notification_message_server_message_service_foreground),
                    R.drawable.do_not_disturb_on_fill_24px);
            KeepLive.startWork(application, KeepLive.RunMode.ENERGY, foregroundNotification, ServerMessageService.instance);
        }else {
            if(!ServerMessageService.instance.isWorking()){
                rework();
            }
        }
    }

    public static synchronized void rework(){
        if(ServerMessageService.instance == null){
            throw new RuntimeException("Server Message Service is not started.");
        }
        Optional<ServerMessageService> serverMessageServiceOptional = ServerMessageService.getInstance();
        serverMessageServiceOptional.ifPresent(serverMessageService -> {
            serverMessageService.onStop();
            serverMessageService.onWorking();
        });
    }

    public static synchronized void stop(){
        if(ServerMessageService.instance == null){
            throw new RuntimeException("Server Message Service is not started.");
        }
        Optional<ServerMessageService> serverMessageServiceOptional = ServerMessageService.getInstance();
        serverMessageServiceOptional.ifPresent(ServerMessageService::onStop);
    }

    public static synchronized Optional<ServerMessageService> getInstance() {
        return Optional.ofNullable(ServerMessageService.instance);
    }

    private ServerMessageService(Context context) {
        this.CONTEXT = context;
    }

    @Override
    public synchronized void onWorking() {
        new Thread(() -> {
            if (working) {
                return;
            }
            working = true;
            ServerMessageServiceStomp.launchWith(this);
        }).start();
    }

    @Override
    public synchronized void onStop() {
        working = false;
        ServerMessageServiceStomp.disconnect();
        toOffline();
    }

    public synchronized boolean isWorking() {
        return working;
    }

    @Override
    public synchronized void onGetDisconnected() {
        toOffline();
        cancelBackingOnline();
        tryBackOnline();
    }

    private synchronized void toOffline() {
        online = false;
        saveOfflineTime();
        GlobalYiersHolder.getYiers(OnOnlineStateChangeYier.class)
                .ifPresent(yierList -> yierList.forEach(OnOnlineStateChangeYier::onOffline));
    }

    private void saveOfflineTime() {
        long offlineTime = SharedPreferencesAccessor.NetPref.getOfflineTime(CONTEXT);
        if(offlineTime == -1) {
            SharedPreferencesAccessor.NetPref.saveOfflineTime(CONTEXT, System.currentTimeMillis());
        }
    }

    @Override
    public synchronized void onGetOnline() {
        toOnline();
        cancelBackingOnline();
    }

    private synchronized void toOnline() {
        online = true;
        clearOfflineTime();
        GlobalYiersHolder.getYiers(OnOnlineStateChangeYier.class)
                .ifPresent(yierList -> yierList.forEach(OnOnlineStateChangeYier::onOnline));
    }

    private void clearOfflineTime() {
        SharedPreferencesAccessor.NetPref.saveOfflineTime(CONTEXT, -1);
    }

    @Override
    public synchronized void tryBackOnline() {
        if(backingOnline){
            return;
        }
        backingOnline = true;
        scheduleBackingOnlineTimer();
    }

    @Override
    public synchronized void cancelBackingOnline() {
        if(backingOnline){
            cancelBackingOnlineTimer();
            backingOnline = false;
        }
    }

    private synchronized void scheduleBackingOnlineTimer(){
        cancelBackingOnlineTimer();
        backingOnlineTimer = new Timer();
        backingOnlineTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!ServerMessageServiceStomp.isConnected()){
                    rework();
                }
            }
        }, TRY_BACK_ONLINE_INTERVAL_MILLI_SEC, TRY_BACK_ONLINE_INTERVAL_MILLI_SEC);
    }

    private synchronized void cancelBackingOnlineTimer(){
        if(backingOnlineTimer != null){
            backingOnlineTimer.cancel();
        }
    }

    public Context getContext() {
        return CONTEXT;
    }

    public boolean isOnline() {
        return online;
    }

    public interface OnOnlineStateChangeYier {
        void onOnline();

        void onOffline();
    }
}
