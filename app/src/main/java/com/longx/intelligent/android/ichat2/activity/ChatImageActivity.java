package com.longx.intelligent.android.ichat2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.databinding.ActivityChatImageBinding;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

import java.util.List;
import java.util.Objects;

public class ChatImageActivity extends BaseActivity {
    private ActivityChatImageBinding binding;
    private List<Uri> imageUriList;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.checkAndExtendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupDefaultBackNavigation(binding.toolbar, getColor(R.color.white));
        getIntentData();
        binding.appBar.bringToFront();
        showContent();
    }

    private void getIntentData() {
        Parcelable[] parcelableArrayExtra = getIntent().getParcelableArrayExtra(ExtraKeys.URIS);
        imageUriList = Utils.parseParcelableArray(Objects.requireNonNull(parcelableArrayExtra));
        position = getIntent().getIntExtra(ExtraKeys.POSITION, 0);
    }

    private void showContent() {

    }
}