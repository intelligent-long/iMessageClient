package com.longx.intelligent.android.imessage.activity.settings;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.util.UiUtil;

/**
 * Created by LONG on 2025/2/2 at 上午8:25.
 */
public class BaseSettingsActivity extends BaseActivity {
//    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFontThemes(R.style.SettingsActivity_Font1, R.style.SettingsActivity_Font2);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
//        rootView = view;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Typeface fontStyle = determineFontStyle();
//        if(fontStyle != null) {
//            UiUtil.setTypefaceToViews(fontStyle, rootView);
//        }
    }

//    protected Typeface determineFontStyle() {
//        Typeface gsansPingfangsc = ResourcesCompat.getFont(this, R.font.app_font_gsans_pingfangsc);
//        Typeface gsansNtsanssc = ResourcesCompat.getFont(this, R.font.app_font_gsans_ntsanssc);
//        int font = SharedPreferencesAccessor.DefaultPref.getFont(this);
//        switch (font){
//            case 0:
//                return gsansPingfangsc;
//            case 1:
//                return gsansNtsanssc;
//        }
//        return null;
//    }
}
