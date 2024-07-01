package com.longx.intelligent.android.ichat2.value;

import com.longx.intelligent.android.ichat2.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by LONG on 2024/5/3 at 8:15 PM.
 */
public class Variables {
    private static final String REQUEST_ADD_CHANNEL_DEFAULT_MESSAGE = "我是{NAME}。";
    public static String getRequestAddChannelDefaultMessage(String name){
        return REQUEST_ADD_CHANNEL_DEFAULT_MESSAGE.replace("{NAME}", name);
    }

    public static String getTimeRangeStr(long firstCompletelyVisibleItemTime, long lastCompletelyVisibleItemTime){
        String timeRange;
        String startTimeStr = new SimpleDateFormat(Constants.TIME_FORMAT_PATTERN_Y_M_D, Locale.getDefault())
                .format(firstCompletelyVisibleItemTime);
        if(TimeUtil.isInSameDay(firstCompletelyVisibleItemTime, lastCompletelyVisibleItemTime)){
            timeRange = startTimeStr;
        }else if(TimeUtil.isInSameMonth(firstCompletelyVisibleItemTime, lastCompletelyVisibleItemTime)){
            String endTimeStr = new SimpleDateFormat(Constants.TIME_FORMAT_PATTERN_D, Locale.getDefault())
                    .format(lastCompletelyVisibleItemTime);
            timeRange = startTimeStr + " 至 " + endTimeStr;
        }else if(TimeUtil.isInSameYear(firstCompletelyVisibleItemTime, lastCompletelyVisibleItemTime)){
            String endTimeStr = new SimpleDateFormat(Constants.TIME_FORMAT_PATTERN_M_D, Locale.getDefault())
                    .format(lastCompletelyVisibleItemTime);
            timeRange = startTimeStr + " 至 " + endTimeStr;
        }else {
            String endTimeStr = new SimpleDateFormat(Constants.TIME_FORMAT_PATTERN_Y_M_D, Locale.getDefault())
                    .format(lastCompletelyVisibleItemTime);
            timeRange = startTimeStr + " 至 " + endTimeStr;
        }
        return timeRange;
    }
}
