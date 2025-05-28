package com.longx.intelligent.android.imessage.activity.edituser;

import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.request.ChangeImessageIdUserPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityChangeImessageIdUserBinding;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class ChangeImessageIdUserActivity extends BaseActivity {
    private ActivityChangeImessageIdUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeImessageIdUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setDefaultValue();
        checkNowCanChange();
        setupToolbar();
    }

    private void setDefaultValue() {
        String imessageIdUser = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this).getImessageIdUser();
        binding.imessageIdUserInput.setText(imessageIdUser);
        UiUtil.setIconMenuEnabled(binding.toolbar.getMenu().findItem(R.id.change), false);
    }

    private void checkNowCanChange() {
        UserApiCaller.imessageIdUserNowCanChange(null, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this, 500L){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(ChangeImessageIdUserActivity.this, new int[]{}, null,
                        new OperationStatus.HandleResult(101, () -> {
                            String desc = data.getMessage();
                            binding.desc.setText(desc);
                            binding.content.setVisibility(View.VISIBLE);
                            UiUtil.setIconMenuEnabled(binding.toolbar.getMenu().findItem(R.id.change), true);
                        }),
                        new OperationStatus.HandleResult(102, () -> {
                            String desc = data.getMessage();
                            binding.desc.setText(desc);
                            binding.imessageIdUserLayout.setVisibility(View.GONE);
                            binding.content.setVisibility(View.VISIBLE);
                        })
                );
            }
        });
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.change){
                String inputtedImessageIdUser = UiUtil.getEditTextString(binding.imessageIdUserInput);
                ChangeImessageIdUserPostBody postBody = new ChangeImessageIdUserPostBody(inputtedImessageIdUser);
                UserApiCaller.changeImessageIdUser(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                    @Override
                    public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ChangeImessageIdUserActivity.this, new int[]{-101, -102, -103, -104}, () -> {
                            new CustomViewMessageDialog(ChangeImessageIdUserActivity.this, "修改成功").create().show();
                        });
                    }
                });
            }
            return true;
        });
    }
}