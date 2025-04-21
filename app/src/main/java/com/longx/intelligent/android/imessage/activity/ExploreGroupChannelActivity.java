package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.databinding.ActivityExploreGroupChannelBinding;

public class ExploreGroupChannelActivity extends BaseActivity {
    private ActivityExploreGroupChannelBinding binding;
    private String[] searchByNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchByNames = new String[]{getString(R.string.search_by_group_channel_id)};
        binding = ActivityExploreGroupChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupViews();
        setupYiers();
    }

    private void setupViews() {
        binding.searchByAutoComplete.setText(searchByNames[0]);
        binding.searchTextInput.setHint(searchByNames[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.layout_auto_complete_text_view_text, searchByNames);
        binding.searchByAutoComplete.setAdapter(adapter);
    }

    private void setupYiers() {

    }
}