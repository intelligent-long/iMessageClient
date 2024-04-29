package com.longx.intelligent.android.ichat2.fragment.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

/**
 * Created by LONG on 2024/1/19 at 6:53 PM.
 */
public abstract class BasePreferenceFragmentCompat extends PreferenceFragmentCompat {

    @Override
    public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        init(savedInstanceState, rootKey);
    }

    protected abstract void init(Bundle savedInstanceState, String rootKey);

    protected abstract void bindPreferences();

    protected abstract void showInfo();

    protected abstract void setupYiers();

    protected void doDefaultActions(){
        bindPreferences();
        showInfo();
        setupYiers();
    }
}
