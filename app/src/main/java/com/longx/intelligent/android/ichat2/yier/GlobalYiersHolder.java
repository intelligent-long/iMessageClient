package com.longx.intelligent.android.ichat2.yier;

import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.service.ServerMessageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by LONG on 2024/1/27 at 12:28 AM.
 */
public class GlobalYiersHolder {
    private static final Map<Class<?>, List<?>> yiersMap = new HashMap<>();

    private static <T> void addToMap(Class<T> clazz, T yier) {
        if (!yiersMap.containsKey(clazz)) {
            yiersMap.put(clazz, new ArrayList<>());
        }
        List<T> yierList = (List<T>) yiersMap.get(clazz);
        yierList.add(yier);
    }

    private static <T> void removeFromMap(Class<T> clazz, T yier) {
        if(!yiersMap.containsKey(clazz)){
            return;
        }
        List<T> yierList = (List<T>) yiersMap.get(clazz);
        yierList.remove(yier);
    }

    public static <T> void holdYier(Class<T> clazz, T yier){
        triggerStickyEvent(clazz, yier);
        addToMap(clazz, yier);
    }

    public static <T> void removeYier(Class<T> clazz, T yier){
        triggerStickyEvent(clazz, yier);
        removeFromMap(clazz, yier);
    }

    public static <T> Optional<List<T>> getYiers(Class<T> clazz){
        if(!yiersMap.containsKey(clazz)){
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable((List<T>) yiersMap.get(clazz));
    }

    private static <T> void triggerStickyEvent(Class<T> clazz, T yier){
        if(yier instanceof ServerMessageService.OnOnlineStateChangeYier && clazz.isAssignableFrom(ServerMessageService.OnOnlineStateChangeYier.class)){
            checkAndTriggerOnlineStateEvent((ServerMessageService.OnOnlineStateChangeYier)yier);
        }else if(yier instanceof ContentUpdater.OnServerContentUpdateYier && clazz.isAssignableFrom(ContentUpdater.OnServerContentUpdateYier.class)){
            checkAndTriggerContentUpdateEvent((ContentUpdater.OnServerContentUpdateYier) yier);
        }
    }

    private static void checkAndTriggerOnlineStateEvent(ServerMessageService.OnOnlineStateChangeYier onOnlineStateChangeYier){
        AtomicBoolean online = new AtomicBoolean(false);
        ServerMessageService.getInstance().ifPresent(cloudService -> {
            online.set(cloudService.isOnline());
        });
        if(online.get()){
            onOnlineStateChangeYier.onOnline();
        }else {
            onOnlineStateChangeYier.onOffline();
        }
    }

    private static void checkAndTriggerContentUpdateEvent(ContentUpdater.OnServerContentUpdateYier onServerContentUpdateYier){
        synchronized (ContentUpdater.class) {
            boolean updating = ContentUpdater.isUpdating();
            if (updating) {
                List<String> updatingIds = ContentUpdater.getUpdatingIds();
                updatingIds.forEach(onServerContentUpdateYier::onStartUpdate);
            }
        }
    }
}
