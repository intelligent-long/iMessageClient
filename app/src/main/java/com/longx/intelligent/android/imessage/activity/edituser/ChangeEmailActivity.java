package com.longx.intelligent.android.imessage.activity.edituser;

import android.os.Bundle;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlobalBehaviors;
import com.longx.intelligent.android.imessage.data.request.ChangeEmailPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityChangeEmailBinding;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class ChangeEmailActivity extends BaseActivity {
    private ActivityChangeEmailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupToolbar();
        setupYiers();
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.change){
                String inputtedEmail = UiUtil.getEditTextString(binding.emailInput);
                String inputtedVerifyCode = UiUtil.getEditTextString(binding.verifyCodeInput);
                ChangeEmailPostBody postBody = new ChangeEmailPostBody(inputtedEmail, inputtedVerifyCode);
                UserApiCaller.changeEmail(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                    @Override
                    public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ChangeEmailActivity.this, new int[]{-101, -102, -103}, () -> {
                            new MessageDialog(ChangeEmailActivity.this, "修改成功").create().show();
                        });
                    }
                });
            }
            return true;
        });
    }

    private void setupYiers() {
        binding.verifyCodeLayout.setEndIconOnClickListener(view -> {
            GlobalBehaviors.sendVerifyCode(this, UiUtil.getEditTextString(binding.emailInput));
        });
    }
}