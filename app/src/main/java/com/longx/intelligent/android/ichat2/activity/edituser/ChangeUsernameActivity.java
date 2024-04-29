package com.longx.intelligent.android.ichat2.activity.edituser;

import android.os.Bundle;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.data.request.ChangeIchatIdUserPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeUsernamePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityChangeUsernameBinding;
import com.longx.intelligent.android.ichat2.dialog.MessageDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;

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
        setupToolbar();
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.change){
                String inputtedUsername = UiUtil.getEditTextString(binding.usernameInput);
                ChangeUsernamePostBody postBody = new ChangeUsernamePostBody(inputtedUsername);
                UserApiCaller.changeUsername(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                    @Override
                    public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                        super.ok(data, row, call);
                        data.commonHandleResult(ChangeUsernameActivity.this, new int[]{-101}, () -> {
                            new MessageDialog(ChangeUsernameActivity.this, "修改成功").show();
                        });
                    }
                });
            }
            return true;
        });
    }
}