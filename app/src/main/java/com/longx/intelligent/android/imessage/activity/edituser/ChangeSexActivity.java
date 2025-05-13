package com.longx.intelligent.android.imessage.activity.edituser;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.data.request.ChangeSexPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityChangeSexBinding;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class ChangeSexActivity extends BaseActivity {
    private ActivityChangeSexBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeSexBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar();
        setupDefaultBackNavigation(binding.toolbar);
        onCreateSetupChangeSexAutoCompleteTextView();
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.change){
                String sexName = UiUtil.getEditTextString(binding.sexAutoCompleteTextView);
                Integer sex = Self.sexStringToValue(this, sexName);
                ChangeSexPostBody postBody = new ChangeSexPostBody(sex);
                UserApiCaller.changeSex(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                    @Override
                    public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ChangeSexActivity.this, new int[]{-101}, () -> {
                            new CustomViewMessageDialog(ChangeSexActivity.this, "修改成功").create().show();
                        });
                    }
                });
            }
            return true;
        });
    }

    private void onCreateSetupChangeSexAutoCompleteTextView() {
        Integer sex = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this).getSex();
        String currentSexName = Self.sexValueToString(this, sex);
        binding.sexAutoCompleteTextView.setText(currentSexName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumeSetupChangeSexAutoCompleteTextView();
    }

    private void onResumeSetupChangeSexAutoCompleteTextView() {
        String[] sexNames = {getString(R.string.do_not_set), getString(R.string.sex_nv), getString(R.string.sex_nan)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.layout_auto_complete_text_view_text, sexNames);
        binding.sexAutoCompleteTextView.setAdapter(adapter);
    }
}