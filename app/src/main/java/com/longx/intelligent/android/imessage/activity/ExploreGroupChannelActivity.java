package com.longx.intelligent.android.imessage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.journeyapps.barcodescanner.ScanOptions;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelQrCode;
import com.longx.intelligent.android.imessage.data.QrCodeData;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.databinding.ActivityExploreGroupChannelBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

public class ExploreGroupChannelActivity extends BaseActivity {
    private ActivityExploreGroupChannelBinding binding;
    private String[] searchByNames;
    private ActivityResultLauncher<Intent> qrScanLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchByNames = new String[]{getString(R.string.search_by_group_channel_id)};
        binding = ActivityExploreGroupChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupViews();
        registerResultLauncher();
        setupYiers();
    }

    private void registerResultLauncher() {
        qrScanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    String qrContent = null;
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        qrContent = data.getStringExtra("SCAN_RESULT");
                        if (qrContent == null) {
                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                qrContent = extras.get("SCAN_RESULT").toString();
                            }
                        }
                    }
                    if (qrContent != null){
                        GroupChannelQrCode groupChannelQrCode = null;
                        try {
                            QrCodeData<?> qrCodeData = QrCodeData.toObject(qrContent);
                            groupChannelQrCode = qrCodeData.getData(GroupChannelQrCode.class);
                        }catch (Exception e){
                            ErrorLogger.log(e);
                            MessageDisplayer.autoShow(this, "二维码解析失败", MessageDisplayer.Duration.SHORT);
                        }
                        if(groupChannelQrCode != null) {
                            GroupChannelApiCaller.findGroupChannelByGroupChannelId(this, groupChannelQrCode.getGroupChannelId(), new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this) {
                                @Override
                                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                                    super.ok(data, raw, call);
                                    data.commonHandleResult(ExploreGroupChannelActivity.this, new int[]{-101}, () -> {
                                        GroupChannel groupChannel = data.getData(GroupChannel.class);
                                        Intent intent = new Intent(ExploreGroupChannelActivity.this, GroupChannelActivity.class);
                                        intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                                        intent.putExtra(ExtraKeys.MAY_NOT_ASSOCIATED, true);
                                        startActivity(intent);
                                    });
                                }
                            });
                        }
                    }
                }
        );
    }

    private void setupViews() {
        binding.searchByAutoComplete.setText(searchByNames[0]);
        binding.searchTextInput.setHint(searchByNames[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.layout_auto_complete_text_view_text, searchByNames);
        binding.searchByAutoComplete.setAdapter(adapter);
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.search_group_channel) {
                String searchText = UiUtil.getEditTextString(binding.searchTextInput);
                if (searchText == null || searchText.isEmpty()) {
                    MessageDisplayer.autoShow(this, "请输入内容", MessageDisplayer.Duration.SHORT);
                    return true;
                }
                GroupChannelApiCaller.findGroupChannelByGroupChannelId(this, searchText, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this) {
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ExploreGroupChannelActivity.this, new int[]{-101}, () -> {
                            GroupChannel groupChannel = data.getData(GroupChannel.class);
                            Intent intent = new Intent(ExploreGroupChannelActivity.this, GroupChannelActivity.class);
                            intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                            intent.putExtra(ExtraKeys.MAY_NOT_ASSOCIATED, true);
                            startActivity(intent);
                        });
                    }
                });
            } else if (item.getItemId() == R.id.search_by_qr_code) {
                ScanOptions options = new ScanOptions();
                options.setPrompt("请将群频道二维码置于取景框内扫描。");
                options.setCaptureActivity(QrScanActivity.class);
                qrScanLauncher.launch(options.createScanIntent(this));
            }
            return true;
        });
    }
}