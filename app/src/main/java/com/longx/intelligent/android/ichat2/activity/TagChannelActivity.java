package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.bottomsheet.AddChannelToTagBottomSheet;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.databinding.ActivityTagChannelBinding;

public class TagChannelActivity extends BaseActivity {
    private ActivityTagChannelBinding binding;
    private ChannelTag channelTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        channelTag = getIntent().getParcelableExtra(ExtraKeys.CHANNEL_TAG);
        showContent();
        setupYiers();
    }

    private void showContent() {
        binding.toolbar.setTitle(channelTag.getName());
        if(channelTag.getChannelIchatIdList().isEmpty()){
            toNoContent();
        }else {
            toContent();
        }
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.add_channel){
                new AddChannelToTagBottomSheet(this, channelTag).show();
            }
            return true;
        });
    }

    private void toNoContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
        binding.noContentLayout.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    private void toContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        binding.noContentLayout.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }
}