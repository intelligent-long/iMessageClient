package com.longx.intelligent.android.ichat2.activity.edituser;

import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.data.request.ChangeIchatIdUserPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityChangeIchatIdUserBinding;
import com.longx.intelligent.android.ichat2.dialog.MessageDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class ChangeIchatIdUserActivity extends BaseActivity {
    private ActivityChangeIchatIdUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeIchatIdUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        checkNowCanChange();
        setupToolbar();
    }

    private void checkNowCanChange() {
        UserApiCaller.ichatIdUserNowCanChange(null, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this, 500L){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(ChangeIchatIdUserActivity.this, new int[]{}, null,
                        new OperationStatus.HandleResult(101, () -> {
                            String desc = data.getMessage();
                            binding.desc.setText(desc);
                            binding.content.setVisibility(View.VISIBLE);
                            binding.toolbar.getMenu().findItem(R.id.change).setEnabled(true);
                        }),
                        new OperationStatus.HandleResult(102, () -> {
                            String desc = data.getMessage();
                            binding.desc.setText(desc);
                            binding.ichatIdUserLayout.setVisibility(View.GONE);
                            binding.content.setVisibility(View.VISIBLE);
                        })
                );
            }
        });
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.change){
                String inputtedIchatIdUser = UiUtil.getEditTextString(binding.ichatIdUserInput);
                ChangeIchatIdUserPostBody postBody = new ChangeIchatIdUserPostBody(inputtedIchatIdUser);
                UserApiCaller.changeIchatIdUser(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                    @Override
                    public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ChangeIchatIdUserActivity.this, new int[]{-101, -102, -103, -104}, () -> {
                            new MessageDialog(ChangeIchatIdUserActivity.this, "修改成功").create().show();
                        });
                    }
                });
            }
            return true;
        });
    }
}