package com.longx.intelligent.android.ichat2.da.sharedpref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.preference.PreferenceManager;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.procedure.ChatVoicePlayer;
import com.longx.intelligent.android.ichat2.data.Avatar;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastChannelPermission;
import com.longx.intelligent.android.ichat2.data.ChannelAddition;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionNotViewedCount;
import com.longx.intelligent.android.ichat2.data.OfflineDetail;
import com.longx.intelligent.android.ichat2.data.ServerSetting;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.UserInfo;
import com.longx.intelligent.android.ichat2.net.ServerProperties;
import com.longx.intelligent.android.ichat2.util.JsonUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LONG on 2024/3/26 at 11:22 PM.
 */
public class SharedPreferencesAccessor {

    public static SharedPreferences getCurrentUserSharedPreferences(Context context, String name){
        String currentUserIchatId = UserProfilePref.getCurrentUserProfile(context).getIchatId();
        return context.getSharedPreferences(name + "_" + currentUserIchatId, Context.MODE_PRIVATE);
    }

    public static class DefaultPref {
        private static class Key {
            private static final String IGNORE_REQUEST_IGNORE_BATTERY_OPTIMIZE = "ignore_request_ignore_battery_optimize";
            private static final String SEARCH_CHANNEL_BY = "search_channel_by";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        }

        public static boolean getUseDynamicColorEnabled(Context context){
            return getSharedPreferences(context).getBoolean(context.getString(R.string.preference_key_use_dynamic_color), false);
        }

        public static int getNightMode(Context context){
            return Integer.parseInt(getSharedPreferences(context).getString(context.getString(R.string.preference_key_night_mode), "-1"));
        }

        public static int getChatBubbleColor(Context context){
            return Integer.parseInt(getSharedPreferences(context).getString(context.getString(R.string.preference_key_chat_bubble_color), "-1"));
        }

        public static int getBottomNavigationViewLabelVisibilityMode(Context context){
            return Integer.parseInt(getSharedPreferences(context).getString(context.getString(R.string.preference_key_bottom_navigation_view_label_visibility_mode), "-1"));
        }

        public static void enableRequestIgnoreBatteryOptimize(Context context){
            saveRequestIgnoreBatteryOptimize(context, true);
        }

        public static void disableRequestIgnoreBatteryOptimize(Context context){
            saveRequestIgnoreBatteryOptimize(context, false);
        }

        private static void saveRequestIgnoreBatteryOptimize(Context context, boolean value){
            getSharedPreferences(context)
                    .edit()
                    .putBoolean(Key.IGNORE_REQUEST_IGNORE_BATTERY_OPTIMIZE, value)
                    .apply();
        }

        public static boolean isRequestIgnoreBatteryOptimizeStateEnabled(Context context){
            return getSharedPreferences(context)
                    .getBoolean(Key.IGNORE_REQUEST_IGNORE_BATTERY_OPTIMIZE, true);
        }

        public static void saveSearchChannelBy(Context context, String searchChannelBy){
            getSharedPreferences(context)
                    .edit()
                    .putString(Key.SEARCH_CHANNEL_BY, searchChannelBy)
                    .apply();
        }

        public static String getSearchChannelBy(Context context){
            return getSharedPreferences(context)
                    .getString(Key.SEARCH_CHANNEL_BY, null);
        }
    }

    public static class NetPref {
        private static final String NAME = "net";
        private static class Key {
            private static final String LOGIN_STATE = "login_state";
            private static final String OFFLINE_TIME = "offline_time";
        }

        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }

        public static boolean getLoginState(Context context){
            return getSharedPreferences(context).getBoolean(Key.LOGIN_STATE, false);
        }

        public static void saveLoginState(Context context, boolean loggedIn) {
            getSharedPreferences(context)
                    .edit()
                    .putBoolean(Key.LOGIN_STATE, loggedIn)
                    .apply();
        }

        public static long getOfflineTime(Context context){
            return getSharedPreferences(context).getLong(Key.OFFLINE_TIME, -1);
        }

        public static void saveOfflineTime(Context context, long time) {
            getSharedPreferences(context)
                    .edit()
                    .putLong(Key.OFFLINE_TIME, time)
                    .apply();
        }

    }

    public static class ServerSettingPref{
        public static final String NAME = "server_setting";
        private static class Key {
            public static final String USE_CENTRAL = "use_central";
            public static final String HOST = "host";
            public static final String PORT = "port";
            public static final String DATA_FOLDER = "data_folder";
        }

        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }

        public static void saveServerSetting(Context context, ServerSetting serverSetting) {
            getSharedPreferences(context)
                    .edit()
                    .putBoolean(Key.USE_CENTRAL, serverSetting.isUseCentral())
                    .putString(Key.HOST, serverSetting.getHost())
                    .putInt(Key.PORT, serverSetting.getPort())
                    .putString(Key.DATA_FOLDER, serverSetting.getDataFolder())
                    .apply();
        }

        public static ServerSetting getServerSetting(Context context){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            boolean useCentral = sharedPreferences.getBoolean(Key.USE_CENTRAL, ServerProperties.DEFAULT_USE_CENTRAL);
            String host = sharedPreferences.getString(Key.HOST, null);
            int port = sharedPreferences.getInt(Key.PORT, -1);
            String dataFolder = sharedPreferences.getString(Key.DATA_FOLDER, null);
            if(host == null || port == -1 || dataFolder == null) {
                dataFolder = ServerSetting.buildDataFolderWithoutSuffix(ServerProperties.DEFAULT_HOST, String.valueOf(ServerProperties.DEFAULT_PORT));
                saveServerSetting(context, new ServerSetting(useCentral, ServerProperties.DEFAULT_HOST, ServerProperties.DEFAULT_PORT, dataFolder, true));
                useCentral = sharedPreferences.getBoolean(Key.USE_CENTRAL, ServerProperties.DEFAULT_USE_CENTRAL);
                host = sharedPreferences.getString(Key.HOST, null);
                port = sharedPreferences.getInt(Key.PORT, -1);
                dataFolder = sharedPreferences.getString(Key.DATA_FOLDER, null);
            }
            return new ServerSetting(useCentral, host, port, dataFolder, false);
        }
    }

    public static class UserProfilePref {
        private static final String NAME_USER_PROFILE = "user_profile";
        private static class Key{
            private static final String ICHAT_ID = "ichat_id";
            private static final String ICHAT_ID_USER = "ichat_id_user";
            private static final String EMAIL = "email";
            private static final String REGISTER_TIME = "register_time";
            private static final String USERNAME = "username";
            private static final String AVATAR_HASH = "avatar_hash";
            private static final String AVATAR_ICHAT_ID = "avatar_ichat_id";
            private static final String AVATAR_EXTENSION = "avatar_extension";
            private static final String AVATAR_TIME = "avatar_time";
            private static final String SEX = "sex";
            private static final String FIRST_REGION_ADCODE = "first_region_adcode";
            private static final String FIRST_REGION_NAME = "first_region_name";
            private static final String SECOND_REGION_ADCODE = "second_region_adcode";
            private static final String SECOND_REGION_NAME = "second_region_name";
            private static final String THIRD_REGION_ADCODE = "third_region_adcode";
            private static final String THIRD_REGION_NAME = "third_region_name";
            private static final String SERVER_EMAIL_VISIBLE = "email_visible";
            private static final String SERVER_SEX_VISIBLE = "sex_visible";
            private static final String SERVER_REGION_VISIBLE = "region_visible";
            private static final String APP_EMAIL_VISIBLE = "app_email_visible";
            private static final String APP_SEX_VISIBLE = "app_sex_visible";
            private static final String APP_REGION_VISIBLE = "app_region_visible";
            private static final String SERVER_FIND_ME_BY_ICHAT_ID = "server_find_me_by_ichat_id";
            private static final String SERVER_FIND_ME_BY_EMAIL = "server_find_me_by_email";
            private static final String APP_FIND_ME_BY_ICHAT_ID = "app_find_me_by_ichat_id";
            private static final String APP_FIND_ME_BY_EMAIL = "app_find_me_by_email";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME_USER_PROFILE, Context.MODE_PRIVATE);
        }

        @SuppressLint("ApplySharedPref")
        public static void saveCurrentUserProfile(Context context, Self self){
            getSharedPreferences(context)
                    .edit()
                    .putString(Key.ICHAT_ID, self.getIchatId())
                    .putString(Key.ICHAT_ID_USER, self.getIchatIdUser())
                    .putString(Key.EMAIL, self.getEmail())
                    .putLong(Key.REGISTER_TIME, self.getRegisterTime().getTime())
                    .putString(Key.USERNAME, self.getUsername())
                    .putString(Key.AVATAR_HASH, self.getAvatar() == null ? null : self.getAvatar().getHash())
                    .putString(Key.AVATAR_ICHAT_ID, self.getAvatar() == null ? null : self.getAvatar().getIchatId())
                    .putString(Key.AVATAR_EXTENSION, self.getAvatar() == null ? null : self.getAvatar().getExtension())
                    .putLong(Key.AVATAR_TIME, self.getAvatar() == null ? -1 : self.getAvatar().getTime().getTime())
                    .putInt(Key.SEX, self.getSex() == null ? -1 : self.getSex())
                    .putInt(Key.FIRST_REGION_ADCODE, self.getFirstRegion() == null ? -1 : self.getFirstRegion().getAdcode())
                    .putString(Key.FIRST_REGION_NAME, self.getFirstRegion() == null ? null : self.getFirstRegion().getName())
                    .putInt(Key.SECOND_REGION_ADCODE, self.getSecondRegion() == null ? -1 : self.getSecondRegion().getAdcode())
                    .putString(Key.SECOND_REGION_NAME, self.getSecondRegion() == null ? null : self.getSecondRegion().getName())
                    .putInt(Key.THIRD_REGION_ADCODE, self.getThirdRegion() == null ? -1 : self.getThirdRegion().getAdcode())
                    .putString(Key.THIRD_REGION_NAME, self.getThirdRegion() == null ? null : self.getThirdRegion().getName())
                    .putBoolean(Key.SERVER_EMAIL_VISIBLE, self.getUserProfileVisibility().isEmailVisible())
                    .putBoolean(Key.APP_EMAIL_VISIBLE, self.getUserProfileVisibility().isEmailVisible())
                    .putBoolean(Key.SERVER_SEX_VISIBLE, self.getUserProfileVisibility().isSexVisible())
                    .putBoolean(Key.APP_SEX_VISIBLE, self.getUserProfileVisibility().isSexVisible())
                    .putBoolean(Key.SERVER_REGION_VISIBLE, self.getUserProfileVisibility().isRegionVisible())
                    .putBoolean(Key.APP_REGION_VISIBLE, self.getUserProfileVisibility().isRegionVisible())
                    .putBoolean(Key.SERVER_FIND_ME_BY_ICHAT_ID, self.getWaysToFindMe().isByIchatIdUser())
                    .putBoolean(Key.APP_FIND_ME_BY_ICHAT_ID, self.getWaysToFindMe().isByIchatIdUser())
                    .putBoolean(Key.SERVER_FIND_ME_BY_EMAIL, self.getWaysToFindMe().isByEmail())
                    .putBoolean(Key.APP_FIND_ME_BY_EMAIL, self.getWaysToFindMe().isByEmail())
                    .commit();
        }

        public static Self getCurrentUserProfile(Context context){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            String ichatId = sharedPreferences.getString(Key.ICHAT_ID, null);
            String ichatIdUser = sharedPreferences.getString(Key.ICHAT_ID_USER, null);
            String email = sharedPreferences.getString(Key.EMAIL, null);
            long registerTimeLong = sharedPreferences.getLong(Key.REGISTER_TIME, -1);
            String username = sharedPreferences.getString(Key.USERNAME, null);
            String avatarHash = sharedPreferences.getString(Key.AVATAR_HASH, null);
            String avatarIchatId = sharedPreferences.getString(Key.AVATAR_ICHAT_ID, null);
            String avatarExtension = sharedPreferences.getString(Key.AVATAR_EXTENSION, null);
            long avatarTimeLong = sharedPreferences.getLong(Key.AVATAR_TIME, -1);
            Date registerTime = null;
            Date avatarTime = null;
            if(registerTimeLong != -1){
                registerTime = new Date(registerTimeLong);
            }
            if(avatarTimeLong != -1){
                avatarTime = new Date(avatarTimeLong);
            }
            int sex = sharedPreferences.getInt(Key.SEX, -1);
            int firstRegionAdcode = sharedPreferences.getInt(Key.FIRST_REGION_ADCODE, -1);
            String firstRegionName = sharedPreferences.getString(Key.FIRST_REGION_NAME, null);
            int secondRegionAdcode = sharedPreferences.getInt(Key.SECOND_REGION_ADCODE, -1);
            String secondRegionName = sharedPreferences.getString(Key.SECOND_REGION_NAME, null);
            int thirdRegionAdcode = sharedPreferences.getInt(Key.THIRD_REGION_ADCODE, -1);
            String thirdRegionName = sharedPreferences.getString(Key.THIRD_REGION_NAME, null);
            boolean emailVisible = sharedPreferences.getBoolean(Key.SERVER_EMAIL_VISIBLE, true);
            boolean sexVisible = sharedPreferences.getBoolean(Key.SERVER_SEX_VISIBLE, true);
            boolean regionVisible = sharedPreferences.getBoolean(Key.SERVER_REGION_VISIBLE, true);
            boolean findMeByIchatId = sharedPreferences.getBoolean(Key.SERVER_FIND_ME_BY_ICHAT_ID, true);
            boolean findMeByEmail = sharedPreferences.getBoolean(Key.SERVER_FIND_ME_BY_EMAIL, true);
            return new Self(ichatId, ichatIdUser, email, registerTime, username,
                    new Avatar(avatarHash, avatarIchatId, avatarExtension, avatarTime),
                    sex == -1 ? null : sex,
                    (firstRegionAdcode == -1 && firstRegionName == null) ? null : new UserInfo.Region(firstRegionAdcode, firstRegionName),
                    (secondRegionAdcode == -1 && secondRegionName == null) ? null : new UserInfo.Region(secondRegionAdcode, secondRegionName),
                    (thirdRegionAdcode == -1 && thirdRegionName == null) ? null : new UserInfo.Region(thirdRegionAdcode, thirdRegionName),
                    new UserInfo.UserProfileVisibility(emailVisible, sexVisible, regionVisible),
                    new UserInfo.WaysToFindMe(findMeByIchatId, findMeByEmail));
        }

        public static void saveServerUserProfileVisibility(Context context, UserInfo.UserProfileVisibility userProfileVisibility){
            getSharedPreferences(context)
                    .edit()
                    .putBoolean(Key.SERVER_EMAIL_VISIBLE, userProfileVisibility.isEmailVisible())
                    .putBoolean(Key.SERVER_SEX_VISIBLE, userProfileVisibility.isSexVisible())
                    .putBoolean(Key.SERVER_REGION_VISIBLE, userProfileVisibility.isRegionVisible())
                    .apply();
        }

        public static UserInfo.UserProfileVisibility getServerUserProfileVisibility(Context context){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            if(!(sharedPreferences.contains(Key.SERVER_EMAIL_VISIBLE) && sharedPreferences.contains(Key.SERVER_SEX_VISIBLE) && sharedPreferences.contains(Key.SERVER_REGION_VISIBLE))){
                return null;
            }
            boolean appEmailVisible = sharedPreferences.getBoolean(Key.SERVER_EMAIL_VISIBLE, true);
            boolean appSexVisible = sharedPreferences.getBoolean(Key.SERVER_SEX_VISIBLE, true);
            boolean appRegionVisible = sharedPreferences.getBoolean(Key.SERVER_REGION_VISIBLE, true);
            return new UserInfo.UserProfileVisibility(appEmailVisible, appSexVisible, appRegionVisible);
        }

        public static void saveAppUserProfileVisibility(Context context, UserInfo.UserProfileVisibility userProfileVisibility){
            getSharedPreferences(context)
                    .edit()
                    .putBoolean(Key.APP_EMAIL_VISIBLE, userProfileVisibility.isEmailVisible())
                    .putBoolean(Key.APP_SEX_VISIBLE, userProfileVisibility.isSexVisible())
                    .putBoolean(Key.APP_REGION_VISIBLE, userProfileVisibility.isRegionVisible())
                    .apply();
        }

        public static UserInfo.UserProfileVisibility getAppUserProfileVisibility(Context context){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            if(!(sharedPreferences.contains(Key.APP_EMAIL_VISIBLE) && sharedPreferences.contains(Key.APP_SEX_VISIBLE) && sharedPreferences.contains(Key.APP_REGION_VISIBLE))){
                return null;
            }
            boolean appEmailVisible = sharedPreferences.getBoolean(Key.APP_EMAIL_VISIBLE, true);
            boolean appSexVisible = sharedPreferences.getBoolean(Key.APP_SEX_VISIBLE, true);
            boolean appRegionVisible = sharedPreferences.getBoolean(Key.APP_REGION_VISIBLE, true);
            return new UserInfo.UserProfileVisibility(appEmailVisible, appSexVisible, appRegionVisible);
        }

        public static void saveServerWaysToFindMe(Context context, UserInfo.WaysToFindMe waysToFindMe){
            getSharedPreferences(context)
                    .edit()
                    .putBoolean(Key.SERVER_FIND_ME_BY_ICHAT_ID, waysToFindMe.isByIchatIdUser())
                    .putBoolean(Key.SERVER_FIND_ME_BY_EMAIL, waysToFindMe.isByEmail())
                    .apply();
        }

        public static UserInfo.WaysToFindMe getServerWaysToFindMe(Context context){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            if(!(sharedPreferences.contains(Key.SERVER_FIND_ME_BY_ICHAT_ID) && sharedPreferences.contains(Key.SERVER_FIND_ME_BY_EMAIL))){
                return null;
            }
            boolean appFindMeByIchatId = sharedPreferences.getBoolean(Key.SERVER_FIND_ME_BY_ICHAT_ID, true);
            boolean appFindMeByEmail = sharedPreferences.getBoolean(Key.SERVER_FIND_ME_BY_EMAIL, true);
            return new UserInfo.WaysToFindMe(appFindMeByIchatId, appFindMeByEmail);
        }

        public static void saveAppWaysToFindMe(Context context, UserInfo.WaysToFindMe waysToFindMe){
            getSharedPreferences(context)
                    .edit()
                    .putBoolean(Key.APP_FIND_ME_BY_ICHAT_ID, waysToFindMe.isByIchatIdUser())
                    .putBoolean(Key.APP_FIND_ME_BY_EMAIL, waysToFindMe.isByEmail())
                    .apply();
        }

        public static UserInfo.WaysToFindMe getAppWaysToFindMe(Context context){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            if(!(sharedPreferences.contains(Key.APP_FIND_ME_BY_ICHAT_ID) && sharedPreferences.contains(Key.APP_FIND_ME_BY_EMAIL))){
                return null;
            }
            boolean appFindMeByIchatId = sharedPreferences.getBoolean(Key.APP_FIND_ME_BY_ICHAT_ID, true);
            boolean appFindMeByEmail = sharedPreferences.getBoolean(Key.APP_FIND_ME_BY_EMAIL, true);
            return new UserInfo.WaysToFindMe(appFindMeByIchatId, appFindMeByEmail);
        }

        @SuppressLint("ApplySharedPref")
        public static void clear(Context context){
            getSharedPreferences(context)
                    .edit()
                    .clear()
                    .commit();
        }
    }

    public static class ServerMessageServicePref {
        private static final String NAME = "server_message_service";
        private static class Key{
            private static final String RUNNING_TIME = "running_time";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }

        public static void recordRunningTime(Context context, long time){
            getSharedPreferences(context)
                    .edit()
                    .putLong(Key.RUNNING_TIME, time)
                    .apply();
        }

        public static long getRunningTime(Context context){
            return getSharedPreferences(context).getLong(Key.RUNNING_TIME, -1);
        }
    }

    public static class NewContentCount {
        private static final String NAME = "new_content_count";
        private static class Key{
            private static final String CHANNEL_ADDITION_ACTIVITIES_REQUESTER = "channel_addition_activities_requester";
            private static final String CHANNEL_ADDITION_ACTIVITIES_RESPONDER = "channel_addition_activities_responder";
            private static final String BROADCAST_LIKE_NEWS_COUNT = "broadcast_like_news_count";
            private static final String BROADCAST_COMMENT_NEWS_COUNT = "broadcast_comment_news_count";
            private static final String BROADCAST_REPLY_COMMENT_NEWS_COUNT = "broadcast_reply_comment_news_count";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return getCurrentUserSharedPreferences(context, NAME);
        }

        public static void saveChannelAdditionActivities(Context context, ChannelAdditionNotViewedCount newContentCount){
            getSharedPreferences(context)
                    .edit()
                    .putInt(Key.CHANNEL_ADDITION_ACTIVITIES_REQUESTER, newContentCount.getRequester())
                    .putInt(Key.CHANNEL_ADDITION_ACTIVITIES_RESPONDER, newContentCount.getResponder())
                    .apply();
        }

        public static int getChannelAdditionActivitiesRequester(Context context){
            return getSharedPreferences(context)
                    .getInt(Key.CHANNEL_ADDITION_ACTIVITIES_REQUESTER, 0);
        }

        public static int getChannelAdditionActivitiesResponder(Context context){
            return getSharedPreferences(context)
                    .getInt(Key.CHANNEL_ADDITION_ACTIVITIES_RESPONDER, 0);
        }

        public static void saveBroadcastLikeNewsCount(Context context, int newsCount){
            getSharedPreferences(context)
                    .edit()
                    .putInt(Key.BROADCAST_LIKE_NEWS_COUNT, newsCount)
                    .apply();
        }

        public static int getBroadcastLikeNewsCount(Context context){
            return getSharedPreferences(context)
                    .getInt(Key.BROADCAST_LIKE_NEWS_COUNT, 0);
        }

        public static void saveBroadcastCommentNewsCount(Context context, int newsCount){
            getSharedPreferences(context)
                    .edit()
                    .putInt(Key.BROADCAST_COMMENT_NEWS_COUNT, newsCount)
                    .apply();
        }

        public static int getBroadcastCommentNewsCount(Context context){
            return getSharedPreferences(context)
                    .getInt(Key.BROADCAST_COMMENT_NEWS_COUNT, 0);
        }

        public static void saveBroadcastReplyCommentNewsCount(Context context, int newsCount){
            getSharedPreferences(context)
                    .edit()
                    .putInt(Key.BROADCAST_REPLY_COMMENT_NEWS_COUNT, newsCount)
                    .apply();
        }

        public static int getBroadcastReplyCommentNewsCount(Context context){
            return getSharedPreferences(context)
                    .getInt(Key.BROADCAST_REPLY_COMMENT_NEWS_COUNT, 0);
        }
    }

    public static class ApiJson{
        private static class IndexedApiJson {
            private final int index;
            private final String json;

            public IndexedApiJson() {
                this(0, null);
            }

            public IndexedApiJson(int index, String json) {
                this.index = index;
                this.json = json;
            }

            public int getIndex() {
                return index;
            }

            public String getJson() {
                return json;
            }
        }
        private static final String NAME = "api_json";
        private static class Key{
            private static final String CHANNEL_ADDITION_ACTIVITIES = "channel_addition_activities";
            private static final String OFFLINE_DETAILS = "offline_details";
            private static final String BROADCASTS = "broadcasts";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return getCurrentUserSharedPreferences(context, NAME);
        }

        public static class ChannelAdditionActivities{
            private static final int MAX_CHANNEL_ADDITION_ACTIVITIES_SIZE = 500;

            public static void addRecord(Context context, ChannelAddition channelAddition){
                String json = JsonUtil.toJson(channelAddition);
                Set<String> paginatedJsonSet = getSharedPreferences(context).getStringSet(Key.CHANNEL_ADDITION_ACTIVITIES, new HashSet<>());
                if(paginatedJsonSet.size() > MAX_CHANNEL_ADDITION_ACTIVITIES_SIZE) return;
                int index = paginatedJsonSet.size();
                IndexedApiJson indexedApiJson = new IndexedApiJson(index, json);
                String paginatedJson = JsonUtil.toJson(indexedApiJson);
                HashSet<String> paginatedJsonSetCopy = new HashSet<>(paginatedJsonSet);
                paginatedJsonSetCopy.add(paginatedJson);
                getSharedPreferences(context)
                        .edit()
                        .putStringSet(Key.CHANNEL_ADDITION_ACTIVITIES, paginatedJsonSetCopy)
                        .apply();
            }

            public static void clearRecords(Context context){
                getSharedPreferences(context)
                        .edit()
                        .remove(Key.CHANNEL_ADDITION_ACTIVITIES)
                        .apply();
            }

            public static List<ChannelAddition> getAllRecords(Context context){
                List<ChannelAddition> result = new ArrayList<>();
                Set<String> jsonSet = getSharedPreferences(context).getStringSet(Key.CHANNEL_ADDITION_ACTIVITIES, new HashSet<>());
                List<IndexedApiJson> indexedApiJsonList = new ArrayList<>();
                jsonSet.forEach(paginatedJson -> {
                    IndexedApiJson indexedApiJson = JsonUtil.toObject(paginatedJson, IndexedApiJson.class);
                    indexedApiJsonList.add(indexedApiJson);
                });
                indexedApiJsonList.sort(Comparator.comparingInt(o -> o.index));
                indexedApiJsonList.forEach(indexedApiJson -> {
                    result.add(JsonUtil.toObject(indexedApiJson.json, ChannelAddition.class));
                });
                return result;
            }
        }

        public static class OfflineDetails{
            public static void addRecord(Context context, OfflineDetail offlineDetail){
                if(offlineDetail == null) return;
                String json = JsonUtil.toJson(offlineDetail);
                Set<String> jsonSet = getSharedPreferences(context).getStringSet(Key.OFFLINE_DETAILS, new HashSet<>());
                HashSet<String> jsonSetCopy = new HashSet<>(jsonSet);
                jsonSetCopy.add(json);
                getSharedPreferences(context)
                        .edit()
                        .putStringSet(Key.OFFLINE_DETAILS, jsonSetCopy)
                        .apply();
            }

            public static void clearRecords(Context context){
                getSharedPreferences(context)
                        .edit()
                        .remove(Key.OFFLINE_DETAILS)
                        .apply();
            }

            public static List<OfflineDetail> getAllRecords(Context context){
                Set<String> jsonSet = getSharedPreferences(context).getStringSet(Key.OFFLINE_DETAILS, new HashSet<>());
                List<OfflineDetail> offlineDetails = new ArrayList<>();
                jsonSet.forEach(s -> {
                    OfflineDetail offlineDetail = JsonUtil.toObject(s, OfflineDetail.class);
                    offlineDetails.add(offlineDetail);
                });
                offlineDetails.sort(Comparator.comparing(OfflineDetail::getTime));
                return offlineDetails;
            }
        }

        public static class Broadcasts{
            public static void addRecords(Context context, List<Broadcast> broadcasts){
                List<String> jsonsToAdd = new ArrayList<>();
                broadcasts.forEach(broadcast -> {
                    jsonsToAdd.add(JsonUtil.toJson(broadcast));
                });
                Set<String> jsonSet = getSharedPreferences(context).getStringSet(Key.BROADCASTS, new HashSet<>());
                HashSet<String> jsonSetCopy = new HashSet<>(jsonSet);
                jsonSetCopy.addAll(jsonsToAdd);
                getSharedPreferences(context)
                        .edit()
                        .putStringSet(Key.BROADCASTS, jsonSetCopy)
                        .apply();
            }

            public static void clearRecords(Context context){
                getSharedPreferences(context)
                        .edit()
                        .remove(Key.BROADCASTS)
                        .apply();
            }

            public static List<Broadcast> getAllRecords(Context context){
                Set<String> jsonSet = getSharedPreferences(context).getStringSet(Key.BROADCASTS, new HashSet<>());
                List<Broadcast> broadcasts = new ArrayList<>();
                jsonSet.forEach(s -> {
                    Broadcast broadcast = JsonUtil.toObject(s, Broadcast.class);
                    broadcasts.add(broadcast);
                });
                broadcasts.sort(Comparator.comparing(Broadcast::getTime));
                return broadcasts;
            }

            public static void deleteRecord(Context context, String broadcastId){
                Set<String> jsonSet = getSharedPreferences(context).getStringSet(Key.BROADCASTS, new HashSet<>());
                List<Broadcast> broadcasts = new ArrayList<>();
                jsonSet.forEach(s -> {
                    Broadcast broadcast = JsonUtil.toObject(s, Broadcast.class);
                    broadcasts.add(broadcast);
                });
                broadcasts.removeIf(broadcast -> broadcast.getBroadcastId().equals(broadcastId));
                clearRecords(context);
                addRecords(context, broadcasts);
            }

            public static void updateRecord(Context context, Broadcast broadcast){
                Set<String> jsonSet = getSharedPreferences(context).getStringSet(Key.BROADCASTS, new HashSet<>());
                List<Broadcast> broadcasts = new ArrayList<>();
                jsonSet.forEach(s -> {
                    Broadcast broadcast1 = JsonUtil.toObject(s, Broadcast.class);
                    broadcasts.add(broadcast1);
                });
                broadcasts.removeIf(broadcast1 -> broadcast1.getBroadcastId().equals(broadcast.getBroadcastId()));
                broadcasts.add(broadcast);
                clearRecords(context);
                addRecords(context, broadcasts);
            }
        }
    }

    public static class AuthPref{
        private static final String NAME = "auth";
        private static class Key{
            private static final String OFFLINE_DETAIL_NEED_FETCH = "offline_detail_need_fetch";
            private static final String SHOWED_OFFLINE_DETAIL_TIME = "showed_offline_detail_time";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }

        public static boolean isOfflineDetailNeedFetch(Context context){
            return getSharedPreferences(context)
                    .getBoolean(Key.OFFLINE_DETAIL_NEED_FETCH, false);
        }

        public static void saveOfflineDetailNeedFetch(Context context, boolean need){
            getSharedPreferences(context)
                    .edit()
                    .putBoolean(Key.OFFLINE_DETAIL_NEED_FETCH, need)
                    .apply();
        }

        public static void saveShowedOfflineDetailTime(Context context, Date time){
            getSharedPreferences(context)
                    .edit()
                    .putLong(Key.SHOWED_OFFLINE_DETAIL_TIME, time.getTime())
                    .apply();
        }

        public static Date getShowedOfflineDetailTime(Context context){
            long timeLong = getSharedPreferences(context).getLong(Key.SHOWED_OFFLINE_DETAIL_TIME, -1);
            if(timeLong == -1) return null;
            return new Date(timeLong);
        }

    }

    public static class ChatPref{
        private static final String NAME = "chat";
        private static class Key {
            private static final String CHAT_VOICE_PLAYER_URI_STRING = "chat_voice_player_uri_string";
            private static final String CHAT_VOICE_PLAYER_ID = "chat_voice_player_id";
            private static final String CHAT_VOICE_PLAYER_POSITION = "chat_voice_player_position";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return getCurrentUserSharedPreferences(context, NAME);
        }

        public static void saveChatVoicePlayerState(Context context, ChatVoicePlayer.State state){
            getSharedPreferences(context)
                    .edit()
                    .putString(Key.CHAT_VOICE_PLAYER_ID, state.getId())
                    .putString(Key.CHAT_VOICE_PLAYER_URI_STRING, state.getUri().toString())
                    .putInt(Key.CHAT_VOICE_PLAYER_POSITION, state.getPosition())
                    .apply();
        }

        public static ChatVoicePlayer.State getChatVoicePlayerState(Context context){
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            String id = sharedPreferences.getString(Key.CHAT_VOICE_PLAYER_ID, null);
            String uriString = sharedPreferences.getString(Key.CHAT_VOICE_PLAYER_URI_STRING, null);
            int position = sharedPreferences.getInt(Key.CHAT_VOICE_PLAYER_POSITION, -1);
            return new ChatVoicePlayer.State(id, uriString == null ? null : Uri.parse(uriString), position);
        }
    }

    public static class BroadcastPref{
        private static final String NAME = "broadcast";
        private static class Key {
            private static final String APP_BROADCAST_CHANNEL_PERMISSION = "app_broadcast_channel_permission";
            private static final String APP_BROADCAST_CHANNEL_PERMISSION_EXCLUDE_CONNECTED_CHANNELS = "app_broadcast_channel_permission_exclude_connected_channels";
            private static final String SERVER_BROADCAST_CHANNEL_PERMISSION = "server_broadcast_channel_permission";
            private static final String SERVER_BROADCAST_CHANNEL_PERMISSION_EXCLUDE_CONNECTED_CHANNELS = "server_broadcast_channel_permission_exclude_connected_channels";
            private static final String BROADCASTS_RELOADED_TIME = "broadcasts_reloaded_time";
        }
        private static SharedPreferences getSharedPreferences(Context context) {
            return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }

        public static void saveBroadcastReloadedTime(Context context, Date time){
            getSharedPreferences(context)
                    .edit()
                    .putLong(Key.BROADCASTS_RELOADED_TIME, time.getTime())
                    .apply();
        }

        public static Date getBroadcastReloadedTime(Context context){
            long time = getSharedPreferences(context)
                    .getLong(Key.BROADCASTS_RELOADED_TIME, -1);
            if(time == -1) return null;
            return new Date(time);
        }

        public static void saveAppBroadcastChannelPermission(Context context, BroadcastChannelPermission broadcastChannelPermission){
            getSharedPreferences(context)
                    .edit()
                    .putInt(Key.APP_BROADCAST_CHANNEL_PERMISSION, broadcastChannelPermission.getPermission())
                    .putStringSet(Key.APP_BROADCAST_CHANNEL_PERMISSION_EXCLUDE_CONNECTED_CHANNELS, broadcastChannelPermission.getExcludeConnectedChannels())
                    .apply();

        }

        public static BroadcastChannelPermission getAppBroadcastChannelPermission(Context context) {
            int permission = getSharedPreferences(context).getInt(Key.APP_BROADCAST_CHANNEL_PERMISSION, -1);
            Set<String> excludeConnectedChannels = getSharedPreferences(context).getStringSet(Key.APP_BROADCAST_CHANNEL_PERMISSION_EXCLUDE_CONNECTED_CHANNELS, new HashSet<>());
            if(permission == -1) return null;
            return new BroadcastChannelPermission(permission, excludeConnectedChannels);
        }

        public static void saveServerBroadcastChannelPermission(Context context, BroadcastChannelPermission broadcastChannelPermission){
            getSharedPreferences(context)
                    .edit()
                    .putInt(Key.SERVER_BROADCAST_CHANNEL_PERMISSION, broadcastChannelPermission.getPermission())
                    .putStringSet(Key.SERVER_BROADCAST_CHANNEL_PERMISSION_EXCLUDE_CONNECTED_CHANNELS, broadcastChannelPermission.getExcludeConnectedChannels())
                    .apply();

        }

        public static BroadcastChannelPermission getServerBroadcastChannelPermission(Context context) {
            int permission = getSharedPreferences(context).getInt(Key.SERVER_BROADCAST_CHANNEL_PERMISSION, -1);
            Set<String> excludeConnectedChannels = getSharedPreferences(context).getStringSet(Key.SERVER_BROADCAST_CHANNEL_PERMISSION_EXCLUDE_CONNECTED_CHANNELS, new HashSet<>());
            if(permission == -1) return null;
            return new BroadcastChannelPermission(permission, excludeConnectedChannels);
        }

    }

}
