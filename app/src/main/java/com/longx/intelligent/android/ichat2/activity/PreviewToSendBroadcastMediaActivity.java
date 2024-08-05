package com.longx.intelligent.android.ichat2.activity;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.databinding.ActivityPreviewToSendBroadcastMediaBinding;
import com.longx.intelligent.android.ichat2.media.data.Media;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

import java.util.List;

public class PreviewToSendBroadcastMediaActivity extends BaseActivity {
    private ActivityPreviewToSendBroadcastMediaBinding binding;
    private List<Media> medias;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewToSendBroadcastMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupBackNavigation(binding.toolbar, getColor(R.color.white));
        binding.appBar.bringToFront();
        getIntentData();
        showContent();
        setupYiers();
    }

    private void getIntentData() {
        medias = getIntent().getParcelableArrayListExtra(ExtraKeys.MEDIAS);
        position = getIntent().getIntExtra(ExtraKeys.POSITION, 0);
    }

    private void showContent() {
        binding.toolbar.setTitle((position + 1) + " / " + medias.size());

    }

    private void setupYiers() {

    }
}