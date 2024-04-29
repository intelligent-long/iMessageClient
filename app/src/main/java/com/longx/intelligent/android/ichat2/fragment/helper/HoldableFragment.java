package com.longx.intelligent.android.ichat2.fragment.helper;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by LONG on 2024/1/10 at 6:21 PM.
 */
public class HoldableFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentHolder.holdFragment(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentHolder.removeFragment(this);
    }

}
