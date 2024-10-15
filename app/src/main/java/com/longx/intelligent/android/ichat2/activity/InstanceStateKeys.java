package com.longx.intelligent.android.ichat2.activity;

/**
 * Created by LONG on 2024/5/20 at 4:23 PM.
 */
public class InstanceStateKeys {

    public static class ChannelsFragment{
        public static final String RECYCLER_VIEW_STATE = "recycler_view_state";
        public static final String APP_BAR_LAYOUT_STATE = "app_bar_layout_state";
        public static final String FAB_EXPANDED_STATE = "fab_expanded_state";
    }

    public static class MessagesFragment{
        public static final String RECYCLER_VIEW_STATE = "recycler_view_state";
        public static final String APP_BAR_LAYOUT_STATE = "app_bar_layout_state";
        public static final String FAB_EXPANDED_STATE = "fab_expanded_state";
    }

    public static class RootSettingsActivity{
        public static final String APP_BAR_LAYOUT_STATE = "app_bar_layout_state";
    }

    public static class BroadcastFragment{
        public static final String APP_BAR_LAYOUT_STATE = "app_bar_layout_state";
        public static final String SEND_BROADCAST_FAB_EXPANDED_STATE = "send_broadcast_fab_expanded_state";
        public static final String TO_START_FAB_VISIBILITY_STATE = "to_start_fab_visibility_state";
        public static final String CURRENT_PN = "current_pn";
        public static final String STOP_FETCH_NEXT_PAGE = "stop_fetch_next_page";
        public static final String HISTORY_BROADCASTS_DATA = "history_broadcasts_data";
        public static final String HEADER_ERROR_TEXT = "header_error_text";
        public static final String FOOTER_ERROR_TEXT = "footer_error_text";
        public static final String HEADER_NO_BROADCAST = "header_no_broadcast";
    }
}
