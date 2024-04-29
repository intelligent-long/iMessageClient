package com.longx.intelligent.android.ichat2.activity.helper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by LONG on 2024/1/9 at 8:40 PM.
 */
public abstract class HoldableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHolder.holdActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityHolder.removeActivity(this);
    }

}
