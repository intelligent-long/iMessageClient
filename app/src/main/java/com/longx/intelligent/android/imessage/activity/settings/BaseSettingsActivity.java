package com.longx.intelligent.android.imessage.activity.settings;

import android.os.Bundle;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;

/**
 * Created by LONG on 2025/2/2 at 上午8:25.
 */
public class BaseSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFontThemes(R.style.SettingsActivity_Font1, R.style.SettingsActivity_Font2);
        super.onCreate(savedInstanceState);
    }
}
