package com.longx.intelligent.android.imessage.ui;

import android.content.Context;
import android.view.View;

import com.longx.intelligent.android.imessage.R;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by LONG on 2024/5/3 at 4:54 PM.
 */
public class BadgeDisplayer {

    public static Badge initBadge( Context context, View targetView, int initNumber, int gravity){
        return new QBadgeView(context)
                .bindTarget(targetView)
                .setBadgeNumber(initNumber)
                .setBadgeGravity(gravity)
                .setShowShadow(false)
                .setBadgeBackgroundColor(context.getColor(R.color.badge_red));
    }

    public static Badge initIndicatorBadge(Context context, View targetView, int gravity){
        return new QBadgeView(context)
                .bindTarget(targetView)
                .setBadgeText("")
                .setBadgeGravity(gravity)
                .setShowShadow(false)
                .setBadgeBackgroundColor(context.getColor(R.color.badge_red));
    }

    public static Badge initIndicatorBadge(Context context, View targetView, int gravity, int offsetX, int offsetY, boolean isDpValue){
        return new QBadgeView(context)
                .bindTarget(targetView)
                .setBadgeText("")
                .setBadgeGravity(gravity)
                .setShowShadow(false)
                .setGravityOffset(offsetX, offsetY, isDpValue)
                .setBadgeBackgroundColor(context.getColor(R.color.badge_red));
    }
}
