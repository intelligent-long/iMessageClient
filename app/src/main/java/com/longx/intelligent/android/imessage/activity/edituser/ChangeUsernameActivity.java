package com.longx.intelligent.android.imessage.activity.edituser;

import android.os.Bundle;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.request.ChangeUsernamePostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityChangeUsernameBinding;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class ChangeUsernameActivity extends BaseActivity {
    private ActivityChangeUsernameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeUsernameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setDefaultValue();
        setupToolbar();
    }

    private void setDefaultValue() {
        String username = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this).getUsername();
        binding.usernameInput.setText(username);
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.change){
                String inputtedUsername = UiUtil.getEditTextString(binding.usernameInput);
                ChangeUsernamePostBody postBody = new ChangeUsernamePostBody(inputtedUsername);
                UserApiCaller.changeUsername(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                    @Override
                    public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ChangeUsernameActivity.this, new int[]{-101}, () -> {
                            new CustomViewMessageDialog(ChangeUsernameActivity.this, "修改成功").create().show();
                        });
                    }
                });
            }
            return true;
        });
    }
}