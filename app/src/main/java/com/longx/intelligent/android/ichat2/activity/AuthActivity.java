package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.GlobalBehaviors;
import com.longx.intelligent.android.ichat2.bottomsheet.AuthMoreOperationBottomSheet;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.OfflineDetail;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.request.ChangePasswordPostBody;
import com.longx.intelligent.android.ichat2.data.request.RegistrationPostBody;
import com.longx.intelligent.android.ichat2.data.request.ResetPasswordPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityAuthBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.dialog.MessageDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.AuthApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.AppUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.OfflineDetailShowYier;
import com.longx.intelligent.android.ichat2.yier.TextChangedYier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements OfflineDetailShowYier {
    private ActivityAuthBinding binding;
    private String[] loginWayNames;
    private GlobalBehaviors.LoginWay currentLoginWay;

    private enum AuthAction{LOGIN, REGISTER, RESET_PASSWORD}
    private AuthAction currentAuthAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginWayNames = new String[]{
                getString(R.string.login_way_ichat_id),
                getString(R.string.login_way_email),
                getString(R.string.login_way_verify_code)};
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar();
        onCreateSetupLoginWayAutoCompleteTextView();
        setupYiers();
        showVersionInfo();
        checkAndSwitchMode();
        GlobalYiersHolder.holdYier(this, OfflineDetailShowYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, OfflineDetailShowYier.class, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumeSetupLoginWayAutoCompleteTextView();
        showOfflineDetail();
        checkAndFetchAndShowOfflineDetail();
    }

    private void checkAndSwitchMode() {
        if(getIntent().getBooleanExtra(ExtraKeys.CHANGE_PASSWORD_MODE, false)){
            toChangePasswordMode();
        }else {
            toNormalMode();
        }
    }

    private void toNormalMode(){
        if(getSavedInstanceState() == null) {
            showLogin();
        }else {
            currentAuthAction = AuthAction.valueOf(getSavedInstanceState().getString("CURRENT_AUTH_ACTION", AuthAction.LOGIN.toString()));
            switchToCurrentAuthActivity();
        }
    }

    private void switchToCurrentAuthActivity(){
        switch (currentAuthAction){
            case LOGIN:
                showLogin();
                break;
            case REGISTER:
                showRegister();
                break;
            case RESET_PASSWORD:
                showResetPassword();
                break;
        }
    }

    private void toChangePasswordMode(){
        setupDefaultBackNavigation(binding.toolbar);
        showResetPassword();
        binding.resetPasswordEmailLayout.setVisibility(View.GONE);
        binding.resetPasswordCreateUser.setVisibility(View.GONE);
        binding.resetPasswordLogin.setVisibility(View.GONE);
        binding.versionInfo.setVisibility(View.GONE);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(view -> {
            new AuthMoreOperationBottomSheet(this).show();
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.login) {
                GlobalBehaviors.doLogin(
                        this,
                        UiUtil.getEditTextString(binding.loginIchatIdUserInput),
                        UiUtil.getEditTextString(binding.loginEmailInput),
                        UiUtil.getEditTextString(binding.loginPasswordInput),
                        UiUtil.getEditTextString(binding.loginVerifyCodeInput),
                        currentLoginWay);
            } else if (item.getItemId() == R.id.register) {
                doRegister();
            } else if (item.getItemId() == R.id.reset_password) {
                doResetPassword();
            }
            return true;
        });
    }

    private void onCreateSetupLoginWayAutoCompleteTextView() {
        if(getSavedInstanceState() == null) {
            toIchatIdLoginWay(true);
        }else {
            currentLoginWay = GlobalBehaviors.LoginWay.valueOf(getSavedInstanceState().getString("CURRENT_LOGIN_WAY", GlobalBehaviors.LoginWay.ICHAT_ID.toString()));
            switchToCurrentLoginWay();
        }
    }

    private void switchToCurrentLoginWay() {
        switch (currentLoginWay){
            case ICHAT_ID:
                toIchatIdLoginWay(true);
                break;
            case EMAIL:
                toEmailLoginWay(true);
                break;
            case VERIFY_CODE:
                toVerifyCodeLoginWay(true);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CURRENT_LOGIN_WAY", currentLoginWay.toString());
        outState.putString("CURRENT_AUTH_ACTION", currentAuthAction.toString());
    }

    private void onResumeSetupLoginWayAutoCompleteTextView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.layout_auto_complete_text_view_text, loginWayNames);
        binding.loginWayAutoCompleteTextView.setAdapter(adapter);
        binding.loginWayAutoCompleteTextView.addTextChangedListener(new TextChangedYier() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(loginWayNames[0])) {
                    toIchatIdLoginWay(false);
                } else if (s.toString().equals(loginWayNames[1])) {
                    toEmailLoginWay(false);
                } else if (s.toString().equals(loginWayNames[2])) {
                    toVerifyCodeLoginWay(false);
                }
            }
        });
    }

    private void toIchatIdLoginWay(boolean changeAutoCompleteTextView){
        currentLoginWay = GlobalBehaviors.LoginWay.ICHAT_ID;
        if(changeAutoCompleteTextView) binding.loginWayAutoCompleteTextView.setText(loginWayNames[0]);
        binding.loginIchatIdUserLayout.setVisibility(View.VISIBLE);
        binding.loginPasswordLayout.setVisibility(View.VISIBLE);
        binding.loginEmailLayout.setVisibility(View.GONE);
        binding.loginVerifyCodeLayout.setVisibility(View.GONE);
    }

    private void toEmailLoginWay(boolean changeAutoCompleteTextView){
        currentLoginWay = GlobalBehaviors.LoginWay.EMAIL;
        if(changeAutoCompleteTextView) binding.loginWayAutoCompleteTextView.setText(loginWayNames[1]);
        binding.loginIchatIdUserLayout.setVisibility(View.GONE);
        binding.loginPasswordLayout.setVisibility(View.VISIBLE);
        binding.loginEmailLayout.setVisibility(View.VISIBLE);
        binding.loginVerifyCodeLayout.setVisibility(View.GONE);
    }

    private void toVerifyCodeLoginWay(boolean changeAutoCompleteTextView){
        currentLoginWay = GlobalBehaviors.LoginWay.VERIFY_CODE;
        if(changeAutoCompleteTextView) binding.loginWayAutoCompleteTextView.setText(loginWayNames[2]);
        binding.loginIchatIdUserLayout.setVisibility(View.GONE);
        binding.loginPasswordLayout.setVisibility(View.GONE);
        binding.loginEmailLayout.setVisibility(View.VISIBLE);
        binding.loginVerifyCodeLayout.setVisibility(View.VISIBLE);
    }

    private void setupYiers() {
        binding.loginCreateUser.setOnClickListener(view -> {
            showRegister();
        });
        binding.loginForgotPassword.setOnClickListener(view -> {
            showResetPassword();
        });
        binding.registerLogin.setOnClickListener(view -> {
            showLogin();
        });
        binding.registerForgotPassword.setOnClickListener(view -> {
            showResetPassword();
        });
        binding.resetPasswordLogin.setOnClickListener(view -> {
            showLogin();
        });
        binding.resetPasswordCreateUser.setOnClickListener(view -> {
            showRegister();
        });
        binding.loginVerifyCodeLayout.setEndIconOnClickListener(view -> {
            GlobalBehaviors.sendVerifyCode(this, UiUtil.getEditTextString(binding.loginEmailInput));
        });
        binding.registerVerifyCodeLayout.setEndIconOnClickListener(view -> {
            GlobalBehaviors.sendVerifyCode(this, UiUtil.getEditTextString(binding.registerEmailInput));
        });
        binding.resetPasswordVerifyCodeLayout.setEndIconOnClickListener(view -> {
            boolean loginState = SharedPreferencesAccessor.NetPref.getLoginState(this);
            String resetPasswordEmail;
            if(loginState){
                resetPasswordEmail = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(this).getEmail();
            }else {
                resetPasswordEmail = UiUtil.getEditTextString(binding.resetPasswordEmailInput);
            }
            GlobalBehaviors.sendVerifyCode(this, resetPasswordEmail);
        });
    }

    private void doRegister() {
        String registerEmail = UiUtil.getEditTextString(binding.registerEmailInput);
        String registerUsername = UiUtil.getEditTextString(binding.registerUsernameInput);
        String registerPassword = UiUtil.getEditTextString(binding.registerPasswordInput);
        String registerVerifyCode = UiUtil.getEditTextString(binding.registerVerifyCodeInput);
        RegistrationPostBody postBody = new RegistrationPostBody(registerEmail, registerUsername, registerPassword, registerVerifyCode);
        AuthApiCaller.register(this, postBody, new RetrofitApiCaller.CommonYier<OperationData>(this){
            @Override
            public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                super.ok(data, row, call);
                data.commonHandleResult(AuthActivity.this, new int[]{-101, -102, -103}, () -> {
                    Self self = data.getData(Self.class);
                    String message = "邮箱: " + self.getEmail() + "\n注册成功，是否登录？";
                    new ConfirmDialog(AuthActivity.this, message)
                            .setPositiveButton((dialogInterface, i) -> {
                                binding.loginEmailInput.setText(registerEmail);
                                binding.loginPasswordInput.setText(registerPassword);
                                toEmailLoginWay(true);
                                showLogin();
                            })
                            .show();
                });
            }
        });
    }

    private void doResetPassword() {
        boolean loginState = SharedPreferencesAccessor.NetPref.getLoginState(this);
        String resetPasswordEmail = UiUtil.getEditTextString(binding.resetPasswordEmailInput);
        String resetPasswordPassword = UiUtil.getEditTextString(binding.resetPasswordPasswordInput);
        String resetPasswordVerifyCode = UiUtil.getEditTextString(binding.resetPasswordVerifyCodeInput);
        if(loginState){
            ChangePasswordPostBody postBody = new ChangePasswordPostBody(resetPasswordPassword, resetPasswordVerifyCode);
            AuthApiCaller.changePassword(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                    super.ok(data, row, call);
                    data.commonHandleResult(AuthActivity.this, new int[]{-101, -102}, () -> {
                        new MessageDialog(AuthActivity.this, "修改成功").show();
                    });
                }
            });
        }else {
            ResetPasswordPostBody postBody = new ResetPasswordPostBody(resetPasswordEmail, resetPasswordPassword, resetPasswordVerifyCode);
            AuthApiCaller.resetPassword(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                    super.ok(data, row, call);
                    data.commonHandleResult(AuthActivity.this, new int[]{-101, -102}, () -> {
                        new MessageDialog(AuthActivity.this, "修改成功").show();
                    });
                }
            });
        }
    }

    private void showLogin() {
        binding.collapsingToolbarLayout.setTitle(getString(R.string.title_login));
        binding.toolbar.getMenu().getItem(0).setVisible(true);
        binding.toolbar.getMenu().getItem(1).setVisible(false);
        binding.toolbar.getMenu().getItem(2).setVisible(false);
        binding.layoutRegister.setVisibility(View.GONE);
        binding.layoutResetPassword.setVisibility(View.GONE);
        binding.layoutLogin.setVisibility(View.VISIBLE);
        currentAuthAction = AuthAction.LOGIN;
    }

    private void showRegister() {
        binding.collapsingToolbarLayout.setTitle(getString(R.string.title_register));
        binding.toolbar.getMenu().getItem(0).setVisible(false);
        binding.toolbar.getMenu().getItem(1).setVisible(true);
        binding.toolbar.getMenu().getItem(2).setVisible(false);
        binding.layoutResetPassword.setVisibility(View.GONE);
        binding.layoutLogin.setVisibility(View.GONE);
        binding.layoutRegister.setVisibility(View.VISIBLE);
        currentAuthAction = AuthAction.REGISTER;
    }

    private void showResetPassword() {
        binding.collapsingToolbarLayout.setTitle(getString(R.string.title_reset_password));
        binding.toolbar.getMenu().getItem(0).setVisible(false);
        binding.toolbar.getMenu().getItem(1).setVisible(false);
        binding.toolbar.getMenu().getItem(2).setVisible(true);
        binding.layoutRegister.setVisibility(View.GONE);
        binding.layoutLogin.setVisibility(View.GONE);
        binding.layoutResetPassword.setVisibility(View.VISIBLE);
        currentAuthAction = AuthAction.RESET_PASSWORD;
    }

    private void showVersionInfo() {
        String versionName = AppUtil.getVersionName(this);
        int versionCode = AppUtil.getVersionCode(this);
        binding.versionInfo.setText(versionName + " (" + versionCode + ")");
    }

    private void checkAndFetchAndShowOfflineDetail() {
        boolean offlineDetailNeedFetch = SharedPreferencesAccessor.AuthPref.isOfflineDetailNeedFetch(this);
        if(!offlineDetailNeedFetch) return;
        AuthApiCaller.fetchOfflineDetail(null, new RetrofitApiCaller.BaseYier<OperationData>(){
            @Override
            public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                super.ok(data, row, call);
                OfflineDetail offlineDetail = data.getData(OfflineDetail.class);
                SharedPreferencesAccessor.ApiJson.OfflineDetails.addRecord(AuthActivity.this, offlineDetail);
                SharedPreferencesAccessor.AuthPref.saveOfflineDetailNeedFetch(AuthActivity.this, false);
                showOfflineDetail();
            }
        });
    }

    @Override
    public void showOfflineDetail() {
        List<OfflineDetail> offlineDetails = SharedPreferencesAccessor.ApiJson.OfflineDetails.getAllRecords(this);
        Date showedOfflineDetailTime = SharedPreferencesAccessor.AuthPref.getShowedOfflineDetailTime(this);
        List<OfflineDetail> needShowOfflineDetails = new ArrayList<>();
        offlineDetails.forEach(offlineDetail -> {
            if(showedOfflineDetailTime == null || offlineDetail.getTime().after(showedOfflineDetailTime)){
                needShowOfflineDetails.add(offlineDetail);
            }
        });
        needShowOfflineDetails.sort(Comparator.comparing(OfflineDetail::getTime));
        needShowOfflineDetails.forEach(needShowOfflineDetail -> {
            new MessageDialog(this, "登陆会话已失效", needShowOfflineDetail.getDesc()).show();
            SharedPreferencesAccessor.AuthPref.saveShowedOfflineDetailTime(this, needShowOfflineDetail.getTime());
        });
    }
}