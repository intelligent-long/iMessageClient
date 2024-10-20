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
import com.longx.intelligent.android.ichat2.procedure.GlobalBehaviors;
import com.longx.intelligent.android.ichat2.procedure.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.databinding.DialogServerSettingBinding;
import com.longx.intelligent.android.ichat2.data.ServerSetting;
import com.longx.intelligent.android.ichat2.net.OkHttpClientCreator;
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
        boolean useCentral = SharedPreferencesAccessor.ServerSettingPref.getServerSetting(getActivity()).isUseCentral();
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
        ServerSetting serverSetting = SharedPreferencesAccessor.ServerSettingPref.getServerSetting(getActivity());
        String host = serverSetting.getHost();
        int port = serverSetting.getPort();
        String dataFolderWithoutSuffix = serverSetting.getDataFolderWithoutSuffix();
        if(host != null) binding.hostInput.setText(host);
        if(port != -1) binding.portInput.setText(String.valueOf(port));
        if(dataFolderWithoutSuffix != null) binding.dataFolderInput.setText(dataFolderWithoutSuffix);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String dataFolderWithoutSuffix = ServerSetting.buildDataFolderWithoutSuffix(
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
                                    onChangeServerSetting();
                                }
                            });
                        })
                        .setNegativeButton(null)
                        .create().show();
            } else {
                onChangeServerSetting();
            }
        });
    }

    private void onChangeServerSetting() {
        ServerSetting previousServerSetting = SharedPreferencesAccessor.ServerSettingPref.getServerSetting(getActivity());
        boolean saved = saveServerSetting();
        if(saved) {
            boolean allSettingValid = true;
            try {
                OkHttpClientCreator.create();
                RetrofitCreator.create(getActivity());
            } catch (Exception e) {
                allSettingValid = false;
                ErrorLogger.log(getClass(), e);
                MessageDisplayer.showSnackbar(getActivity(), "服务器设置不合法", Snackbar.LENGTH_SHORT);
            }
            try {
                File dataFolder = new File(DataPaths.PrivateFile.getPrivateFileRootPath(getActivity()));
                dataFolder.mkdirs();
                String dataFolderName = SharedPreferencesAccessor.ServerSettingPref.getServerSetting(getActivity()).getDataFolder();
                boolean dataFolderExist = FileUtil.dirContainsFile(Objects.requireNonNull(dataFolder.getParentFile()), dataFolderName);
                if(!dataFolderExist) throw  new Exception("数据文件夹创建失败");
            } catch (Exception e) {
                allSettingValid = false;
                ErrorLogger.log(getClass(), e);
                MessageDisplayer.showSnackbar(getActivity(), "数据文件夹不合法", Snackbar.LENGTH_SHORT);
            }
            if (!allSettingValid) {
                SharedPreferencesAccessor.ServerSettingPref.saveServerSetting(getActivity(), previousServerSetting);
                try {
                    OkHttpClientCreator.create();
                    RetrofitCreator.create(getActivity());
                    new File(DataPaths.PrivateFile.getPrivateFileRootPath(getActivity())).mkdirs();
                } catch (Exception e) {
                    ErrorLogger.log(getClass(), e);
                    MessageDisplayer.showSnackbar(getActivity(), "出错了", Snackbar.LENGTH_LONG);
                }
            }
        }
        dismiss();
    }

    private boolean saveServerSetting() {
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
        SharedPreferencesAccessor.ServerSettingPref.saveServerSetting(getActivity(),
                new ServerSetting(Objects.equals(serverTypeName, serverTypeNames[0]), host, portInt, dataFolder, true));
        return true;
    }
}
