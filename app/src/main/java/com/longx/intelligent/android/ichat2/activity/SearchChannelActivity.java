package com.longx.intelligent.android.ichat2.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.databinding.ActivitySearchChannelBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.yier.TextChangedYier;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class SearchChannelActivity extends BaseActivity {
    private ActivitySearchChannelBinding binding;
    private String[] searchByNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchByNames = new String[]{
                getString(R.string.search_by_ichat_id_user),
                getString(R.string.search_by_email)};
        binding = ActivitySearchChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupViews();
        setupYiers();
    }

    private void setupViews() {
        String searchChannelBy = SharedPreferencesAccessor.DefaultPref.getSearchChannelBy(this);
        if(searchChannelBy == null || searchChannelBy.equals(searchByNames[0])) {
            binding.searchByAutoComplete.setText(searchByNames[0]);
            binding.searchTextInput.setHint(searchByNames[0]);
        }else if(searchChannelBy.equals(searchByNames[1])){
            binding.searchByAutoComplete.setText(searchByNames[1]);
            binding.searchTextInput.setHint(searchByNames[1]);
        }else {
            binding.searchByAutoComplete.setText(searchByNames[0]);
            binding.searchTextInput.setHint(searchByNames[0]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.layout_auto_complete_text_view_text, searchByNames);
        binding.searchByAutoComplete.setAdapter(adapter);
        binding.searchByAutoComplete.addTextChangedListener(new TextChangedYier(){
            @Override
            public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
                if(s.toString().equals(searchByNames[0])){
                    binding.searchTextInput.setHint(searchByNames[0]);
                    SharedPreferencesAccessor.DefaultPref.saveSearchChannelBy(SearchChannelActivity.this, searchByNames[0]);
                }else if(s.toString().equals(searchByNames[1])){
                    binding.searchTextInput.setHint(searchByNames[1]);
                    SharedPreferencesAccessor.DefaultPref.saveSearchChannelBy(SearchChannelActivity.this, searchByNames[1]);
                }
            }
        });
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            String searchText = UiUtil.getEditTextString(binding.searchTextInput);
            if(searchText == null || searchText.equals("")) {
                MessageDisplayer.autoShow(this, "请输入内容", MessageDisplayer.Duration.SHORT);
                return true;
            }
            if(item.getItemId() == R.id.search_channel){
                if(Objects.equals(UiUtil.getEditTextString(binding.searchByAutoComplete), searchByNames[0])){
                    ChannelApiCaller.findChannelByIchatIdUser(this, searchText, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this){
                        @Override
                        public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                            super.ok(data, raw, call);
                            data.commonHandleResult(SearchChannelActivity.this, new int[]{-101}, () -> {
                                Channel channel = data.getData(Channel.class);
                                Intent intent = new Intent(SearchChannelActivity.this, ChannelActivity.class);
                                intent.putExtra(ExtraKeys.CHANNEL, channel);
                                intent.putExtra(ExtraKeys.NETWORK_FETCH, true);
                                startActivity(intent);
                            });
                        }
                    });
                }else if(Objects.equals(UiUtil.getEditTextString(binding.searchByAutoComplete), searchByNames[1])){
                    ChannelApiCaller.findChannelByEmail(this, searchText, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this){
                        @Override
                        public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                            super.ok(data, raw, call);
                            data.commonHandleResult(SearchChannelActivity.this, new int[]{-101}, () -> {
                                Channel channel = data.getData(Channel.class);
                                Intent intent = new Intent(SearchChannelActivity.this, ChannelActivity.class);
                                intent.putExtra(ExtraKeys.CHANNEL, channel);
                                intent.putExtra(ExtraKeys.NETWORK_FETCH, true);
                                startActivity(intent);
                            });
                        }
                    });
                }
            }
            return true;
        });
    }
}