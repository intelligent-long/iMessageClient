package com.longx.intelligent.android.ichat2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.bottomsheet.AddChannelTagBottomSheet;
import com.longx.intelligent.android.ichat2.databinding.ActivityTagBinding;

public class TagActivity extends BaseActivity {
    private ActivityTagBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        showContent();
        setUpYiers();
    }

    private void showContent() {

    }

    private void setUpYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.add){
                new AddChannelTagBottomSheet(this).show();
            }
            return true;
        });
    }
}