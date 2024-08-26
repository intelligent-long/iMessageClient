package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastChannelBinding;
import com.longx.intelligent.android.ichat2.databinding.LayoutBroadcastRecyclerFooterBinding;
import com.longx.intelligent.android.ichat2.databinding.LayoutBroadcastRecyclerHeaderBinding;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

public class BroadcastChannelActivity extends BaseActivity {
    private ActivityBroadcastChannelBinding binding;
    private LayoutBroadcastRecyclerHeaderBinding headerBinding;
    private LayoutBroadcastRecyclerFooterBinding footerBinding;
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastChannelBinding.inflate(getLayoutInflater());
        headerBinding = LayoutBroadcastRecyclerHeaderBinding.inflate(getLayoutInflater());
        footerBinding = LayoutBroadcastRecyclerFooterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        setupFab();
        showContent();
    }

    private void intentData() {
        channel = getIntent().getParcelableExtra(ExtraKeys.CHANNEL);
    }

    private void setupFab() {
        String currentUserIchatId = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this).getIchatId();
        float fabMarginTop;
        float smallFabMarginTop;
        float fabMarginEnd = getResources().getDimension(R.dimen.fab_margin_end);
        if(currentUserIchatId.equals(channel.getIchatId())){
            smallFabMarginTop = WindowAndSystemUiUtil.getStatusBarHeight(this) + WindowAndSystemUiUtil.getActionBarSize(this) + getResources().getDimension(R.dimen.fab_margin_bottom);
            UiUtil.setViewMargin(binding.toStartFab, 0, (int) smallFabMarginTop, (int) fabMarginEnd, 0);
            binding.toStartFab.setVisibility(View.VISIBLE);
        }else {
            fabMarginTop = WindowAndSystemUiUtil.getStatusBarHeight(this) + WindowAndSystemUiUtil.getActionBarSize(this) + getResources().getDimension(R.dimen.fab_margin_bottom);
            smallFabMarginTop = fabMarginTop + UiUtil.dpToPx(this, 70);
            UiUtil.setViewMargin(binding.sendBroadcastFab, 0, (int) fabMarginTop, (int) fabMarginEnd, 0);
            UiUtil.setViewMargin(binding.toStartFab, 0, (int) smallFabMarginTop, (int) fabMarginEnd, 0);
            binding.sendBroadcastFab.setVisibility(View.VISIBLE);
            binding.toStartFab.setVisibility(View.VISIBLE);
        }
    }

    private void showContent() {
        binding.toolbar.setTitle(channel.getName() + " 的广播");
    }
}