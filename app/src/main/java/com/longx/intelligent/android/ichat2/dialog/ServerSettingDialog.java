package com.longx.intelligent.android.ichat2.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.behaviorcomponents.GlobalBehaviors;
import com.longx.intelligent.android.ichat2.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.databinding.DialogServerSettingBinding;
import com.longx.intelligent.android.ichat2.net.ServerConfig;
import com.longx.intelligent.android.ichat2.net.okhttp.OkHttpClientCreator;
import com.longx.intelligent.android.ichat2.net.retrofit.RetrofitCreator;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;
import com.longx.intelligent.android.ichat2.util.NetworkUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;

import java.io.File;
import java.util.Objects;

/**
 * Created by LONG on 2024/1/10 at 9:32 PM.
 */
public class ServerSettingDialog extends AbstractDialog{
    private DialogServerSettingBinding binding;
    private final String[] serverTypeNames;

    public ServerSettingDialog(Activity activity) {
        super(activity, true);
        serverTypeNames = new String[]{
                getActivity().getString(R.string.server_type_central),
                getActivity().getString(R.string.server_type_custom)};
    }

    @Override
    protected View createView(LayoutInflater layoutInflater) {
        binding = DialogServerSettingBinding.inflate(layoutInflater);
        setupServerTypeAutoCompleteTextView();
        setupCustomServerTypeViews();
        return binding.getRoot();
    }

    private void setupServerTypeAutoCompleteTextView() {
        boolean useCentral = SharedPreferencesAccessor.ServerPref.isUseCentral(getActivity());
        if(useCentral){
            binding.serverTypeAutoCompleteTextView.setText(serverTypeNames[0]);
            changeUiToCentralServerType();
        }else {
            binding.serverTypeAutoCompleteTextView.setText(serverTypeNames[1]);
            changeUiToCustomServerType();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.layout_auto_complete_text_view_text, serverTypeNames);
        binding.serverTypeAutoCompleteTextView.setAdapter(adapter);
        binding.serverTypeAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(serverTypeNames[0])){
                    changeUiToCentralServerType();
                }else if(s.toString().equals(serverTypeNames[1])){
                    changeUiToCustomServerType();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupCustomServerTypeViews() {
        ServerConfig serverConfig = SharedPreferencesAccessor.ServerPref.getCustomServerConfig(getActivity());
        String host = serverConfig.getHost();
        int port = serverConfig.getPort();
        String dataFolderWithoutSuffix = serverConfig.getDataFolderWithoutSuffix();
        if(host != null) binding.hostInput.setText(host);
        if(port != -1) binding.portInput.setText(String.valueOf(port));
        if(dataFolderWithoutSuffix != null) binding.dataFolderInput.setText(dataFolderWithoutSuffix);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String dataFolderWithoutSuffix = ServerConfig.buildDataFolderWithoutSuffix(
                        UiUtil.getEditTextString(binding.hostInput), UiUtil.getEditTextString(binding.portInput));
                binding.dataFolderInput.setText(dataFolderWithoutSuffix);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        binding.hostInput.addTextChangedListener(textWatcher);
        binding.portInput.addTextChangedListener(textWatcher);
    }

    private void changeUiToCentralServerType() {
        binding.hostLayout.setVisibility(View.GONE);
        binding.portLayout.setVisibility(View.GONE);
        binding.dataFolderLayout.setVisibility(View.GONE);
    }

    private void changeUiToCustomServerType() {
        binding.hostLayout.setVisibility(View.VISIBLE);
        binding.portLayout.setVisibility(View.VISIBLE);
        binding.dataFolderLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        return builder
                .setTitle(getActivity().getString(R.string.server_setting))
                .setIcon(R.drawable.cloud_outline_24px_primary_color)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .create();
    }

    @Override
    protected void onDialogShowed() {
        super.onDialogShowed();
        getDialog().getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
            boolean loginState = SharedPreferencesAccessor.NetPref.getLoginState(getActivity());
            String host = UiUtil.getEditTextString(binding.hostInput);
            String port = UiUtil.getEditTextString(binding.portInput);
            String baseUrlTemporary = "http://" + host + ":" + port + "/";
            if (loginState) {
                new ConfirmDialog(getActivity(), "必须退出登陆，才能重设服务器。\n是否继续？")
                        .setPositiveButton((dialogInterface, i) -> {
                            GlobalBehaviors.doLogout(getActivity(), baseUrlTemporary, results -> {
                                Boolean success = (Boolean) results[0];
                                if(success) {
                                    changeServerConfig();
                                }
                            });
                        })
                        .setNegativeButton()
                        .create().show();
            } else {
                changeServerConfig();
            }
        });
    }

    private void changeServerConfig() {
        boolean previousUseCentral = SharedPreferencesAccessor.ServerPref.isUseCentral(getActivity());
        if(saveServerConfig()) {
            OperatingDialog operatingDialog = new OperatingDialog(getActivity());
            operatingDialog.create().show();
            new Thread(() -> {
                onServerConfigChanged(previousUseCentral);
                getActivity().runOnUiThread(operatingDialog::dismiss);
            }).start();

        }
    }

    private boolean saveServerConfig() {
        String serverTypeName = UiUtil.getEditTextString(binding.serverTypeAutoCompleteTextView);
        String host = UiUtil.getEditTextString(binding.hostInput);
        if(host == null || host.isEmpty()){
            MessageDisplayer.showSnackbar(getActivity(), "主机不合法", Snackbar.LENGTH_SHORT);
            return false;
        }
        String port = UiUtil.getEditTextString(binding.portInput);
        int portInt;
        try {
            portInt = Integer.parseInt(Objects.requireNonNull(port));
            if(!NetworkUtil.isPortValid(portInt)) throw new Exception("端口值不在合法范围");
        }catch (Exception e){
            ErrorLogger.log(getClass(), e);
            MessageDisplayer.showSnackbar(getActivity(), "端口不合法", Snackbar.LENGTH_SHORT);
            return false;
        }
        String dataFolder = UiUtil.getEditTextString(binding.dataFolderInput);
        if(dataFolder == null || dataFolder.isEmpty()){
            MessageDisplayer.showSnackbar(getActivity(), "数据文件夹不合法", Snackbar.LENGTH_SHORT);
            return false;
        }
        boolean equals = Objects.equals(serverTypeName, serverTypeNames[0]);
        SharedPreferencesAccessor.ServerPref.saveUseCentral(getActivity(), equals);
        SharedPreferencesAccessor.ServerPref.saveCustomServerConfig(getActivity(),
                new ServerConfig(host, portInt, null, dataFolder + ServerConfig.DATA_FOLDER_SUFFIX));
        return true;
    }

    private void onServerConfigChanged(boolean previousUseCentral) {
        boolean useCentral = SharedPreferencesAccessor.ServerPref.isUseCentral(getActivity());
        boolean allSuccess = true;
        ServerConfig previousServerConfig;
        if(useCentral) {
            previousServerConfig = SharedPreferencesAccessor.ServerPref.getCentralServerConfig(getActivity());
        }else {
            previousServerConfig = SharedPreferencesAccessor.ServerPref.getCustomServerConfig(getActivity());
        }
        try {
            OkHttpClientCreator.create();
            RetrofitCreator.create(getActivity());
        } catch (Exception e) {
            allSuccess = false;
            ErrorLogger.log(getClass(), e);
            getActivity().runOnUiThread(() -> MessageDisplayer.showSnackbar(getActivity(), "服务器设置不合法", Snackbar.LENGTH_LONG));
        }
        String dataFolderName = null;
        if(useCentral){
            try {
                dataFolderName = SharedPreferencesAccessor.ServerPref.getCentralServerConfig(getActivity()).getDataFolder();
            } catch (Exception e) {
                allSuccess = false;
                ErrorLogger.log(getClass(), e);
                getActivity().runOnUiThread(() -> MessageDisplayer.showSnackbar(getActivity(), "服务器设置不合法", Snackbar.LENGTH_LONG));
            }
        }else {
            dataFolderName = SharedPreferencesAccessor.ServerPref.getCustomServerConfig(getActivity()).getDataFolder();
        }
        try {
            File dataFolder = new File(DataPaths.PrivateFile.privateFileRootPath(getActivity()));
            dataFolder.mkdirs();
            boolean dataFolderExist = FileUtil.dirContainsFile(Objects.requireNonNull(dataFolder.getParentFile()), dataFolderName);
            if(!dataFolderExist) throw new Exception("数据文件夹创建失败");
        } catch (Exception e) {
            allSuccess = false;
            ErrorLogger.log(getClass(), e);
            getActivity().runOnUiThread(() -> MessageDisplayer.showSnackbar(getActivity(), "数据文件夹不合法", Snackbar.LENGTH_LONG));
        }
        if (!allSuccess) {
            SharedPreferencesAccessor.ServerPref.saveUseCentral(getActivity(), previousUseCentral);
            if(useCentral){
                SharedPreferencesAccessor.ServerPref.saveCentralServerConfig(getActivity(), previousServerConfig);
            }else {
                SharedPreferencesAccessor.ServerPref.saveCustomServerConfig(getActivity(), previousServerConfig);
            }
            try {
                OkHttpClientCreator.create();
                RetrofitCreator.create(getActivity());
                new File(DataPaths.PrivateFile.privateFileRootPath(getActivity())).mkdirs();
            } catch (Exception e) {
                ErrorLogger.log(getClass(), e);
                getActivity().runOnUiThread(() -> MessageDisplayer.showSnackbar(getActivity(), "出错了", Snackbar.LENGTH_LONG));
            }
        }
        dismiss();
    }
}
