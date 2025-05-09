package com.longx.intelligent.android.imessage.yier;

import android.content.Context;

import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.service.ServerMessageService;

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

    public static <T> void holdYier(Context context, Class<T> clazz, T yier, Object... objects){
        triggerStickyEvent(context, clazz, yier, objects);
        addToMap(clazz, yier);
    }

    public static <T> void removeYier(Context context, Class<T> clazz, T yier, Object... objects){
        triggerStickyEvent(context, clazz, yier, objects);
        removeFromMap(clazz, yier);
    }

    public static <T> Optional<List<T>> getYiers(Class<T> clazz){
        if(!yiersMap.containsKey(clazz)){
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable((List<T>) yiersMap.get(clazz));
    }

    private static <T> void triggerStickyEvent(Context context, Class<T> clazz, T yier, Object... objects){
        if(yier instanceof ServerMessageService.OnOnlineStateChangeYier && clazz.isAssignableFrom(ServerMessageService.OnOnlineStateChangeYier.class)){
            checkAndTriggerOnlineStateEvent((ServerMessageService.OnOnlineStateChangeYier)yier);
        }else if(yier instanceof ContentUpdater.OnServerContentUpdateYier && clazz.isAssignableFrom(ContentUpdater.OnServerContentUpdateYier.class)){
            checkAndTriggerContentUpdateEvent((ContentUpdater.OnServerContentUpdateYier) yier);
        }else if(yier instanceof NewContentBadgeDisplayYier && clazz.isAssignableFrom(NewContentBadgeDisplayYier.class)){
            NewContentBadgeDisplayYier.ID[] ids = new NewContentBadgeDisplayYier.ID[objects.length];
            for (int i = 0; i < objects.length; i++) {
                ids[i] = (NewContentBadgeDisplayYier.ID) objects[i];
            }
            triggerNewContentBadgeDisplayEvent(context, (NewContentBadgeDisplayYier) yier, ids);
        }else if(yier instanceof ChannelAdditionActivitiesUpdateYier && clazz.isAssignableFrom(ChannelAdditionActivitiesUpdateYier.class)){
            triggerChannelAdditionActivitiesUpdateEvent((ChannelAdditionActivitiesUpdateYier) yier);
        }else if(yier instanceof OpenedChatsUpdateYier && clazz.isAssignableFrom(OpenedChatsUpdateYier.class)){
            triggerOpenedChatUpdateEvent((OpenedChatsUpdateYier) yier);
        }else if(yier instanceof GroupChannelAdditionActivitiesUpdateYier && clazz.isAssignableFrom(GroupChannelAdditionActivitiesUpdateYier.class)) {
            triggerGroupChannelAdditionActivitiesUpdateEvent((GroupChannelAdditionActivitiesUpdateYier) yier);
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
                updatingIds.forEach(updatingId -> {
                    onServerContentUpdateYier.onStartUpdate(updatingId, updatingIds,null);
                });
            }
        }
    }

    private static void triggerNewContentBadgeDisplayEvent(Context context, NewContentBadgeDisplayYier yier, NewContentBadgeDisplayYier.ID... ids){
        for (NewContentBadgeDisplayYier.ID id : ids) {
            yier.autoShowNewContentBadge(context, id);
        }
    }

    private static void triggerChannelAdditionActivitiesUpdateEvent(ChannelAdditionActivitiesUpdateYier yier){
        yier.onChannelAdditionActivitiesUpdate();
    }

    private static void triggerOpenedChatUpdateEvent(OpenedChatsUpdateYier yier){
        yier.onOpenedChatsUpdate();
    }

    private static void triggerGroupChannelAdditionActivitiesUpdateEvent(GroupChannelAdditionActivitiesUpdateYier yier){
        yier.onGroupChannelAdditionActivitiesUpdate();
    }
}
