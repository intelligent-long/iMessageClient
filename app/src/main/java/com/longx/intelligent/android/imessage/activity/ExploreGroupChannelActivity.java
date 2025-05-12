package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.databinding.ActivityExploreGroupChannelBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

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
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.search_group_channel) {
                String searchText = UiUtil.getEditTextString(binding.searchTextInput);
                if (searchText == null || searchText.isEmpty()) {
                    MessageDisplayer.autoShow(this, "请输入内容", MessageDisplayer.Duration.SHORT);
                    return true;
                }
                GroupChannelApiCaller.findGroupChannelByGroupChannelId(this, searchText, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this) {
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ExploreGroupChannelActivity.this, new int[]{-101}, () -> {
                            GroupChannel groupChannel = data.getData(GroupChannel.class);
                            Intent intent = new Intent(ExploreGroupChannelActivity.this, GroupChannelActivity.class);
                            intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                            intent.putExtra(ExtraKeys.MAY_NOT_ASSOCIATED, true);
                            startActivity(intent);
                        });
                    }
                });
            } else if (item.getItemId() == R.id.search_by_qr_code) {

            }
            return true;
        });
    }
}