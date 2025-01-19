package com.longx.intelligent.android.imessage.activity.helper;

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

    @Override
    protected void onStart() {
        super.onStart();
        ActivityHolder.addToList(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ActivityHolder.removeFromList(this);
    }
}
