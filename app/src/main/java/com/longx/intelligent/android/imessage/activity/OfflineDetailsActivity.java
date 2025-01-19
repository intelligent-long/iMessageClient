package com.longx.intelligent.android.imessage.activity;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.OfflineDetailsRecyclerAdapter;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.OfflineDetail;
import com.longx.intelligent.android.imessage.databinding.ActivityOfflineDetailsBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;

import java.util.List;

public class OfflineDetailsActivity extends BaseActivity {
    private ActivityOfflineDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOfflineDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupYiers();
        showContent();
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.clear){
                new ConfirmDialog(this, "是否继续？")
                        .setPositiveButton((dialog, which) -> {
                            SharedPreferencesAccessor.ApiJson.OfflineDetails.clearRecords(this);
                            showContent();
                        })
                        .create().show();
            }
            return true;
        });
    }

    private void showContent() {
        List<OfflineDetail> offlineDetails = SharedPreferencesAccessor.ApiJson.OfflineDetails.getAllRecords(this);
        if(offlineDetails.size() == 0){
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        }else {
            binding.noContentLayout.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.recyclerView.setLayoutManager(layoutManager);
            OfflineDetailsRecyclerAdapter adapter = new OfflineDetailsRecyclerAdapter(this, offlineDetails);
            binding.recyclerView.setAdapter(adapter);
        }
    }
}