package com.longx.intelligent.android.imessage.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanOptions;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.bottomsheet.ScanQrCodeByBottomSheet;
import com.longx.intelligent.android.imessage.data.ChannelQrCode;
import com.longx.intelligent.android.imessage.data.QrCodeData;
import com.longx.intelligent.android.imessage.databinding.ActivityExploreChannelBinding;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.QRCodeUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.yier.TextChangedYier;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class ExploreChannelActivity extends BaseActivity {
    private ActivityExploreChannelBinding binding;
    private String[] searchByNames;
    private ActivityResultLauncher<Intent> qrScanLauncher;
    private ActivityResultLauncher<Intent> imageChosenActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchByNames = new String[]{
                getString(R.string.search_by_imessage_id_user),
                getString(R.string.search_by_email)};
        binding = ActivityExploreChannelBinding.inflate(getLayoutInflater());
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
                    String qrText = null;
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        qrText = data.getStringExtra("SCAN_RESULT");
                        if (qrText == null) {
                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                qrText = extras.get("SCAN_RESULT").toString();
                            }
                        }
                    }
                    onQrCodeRecognized(qrText);
                }
        );

        imageChosenActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            String qrText = QRCodeUtil.decodeQRCode(bitmap);
                            if (qrText != null) {
                                onQrCodeRecognized(qrText);
                            } else {
                                MessageDisplayer.autoShow(this, "未识别到二维码", MessageDisplayer.Duration.SHORT);
                            }
                        } catch (IOException e) {
                            ErrorLogger.log(e);
                            MessageDisplayer.autoShow(this, "二维码解析失败", MessageDisplayer.Duration.SHORT);
                        }
                    }
                }
        );
    }

    private void onQrCodeRecognized(String qrText) {
        if (qrText != null){
            ChannelQrCode channelQrCode = null;
            try {
                QrCodeData<?> qrCodeData = QrCodeData.toObject(qrText);
                channelQrCode = qrCodeData.getData(ChannelQrCode.class);
            }catch (Exception e){
                ErrorLogger.log(e);
                MessageDisplayer.autoShow(this, "二维码解析失败", MessageDisplayer.Duration.SHORT);
            }
            if(channelQrCode != null) {
                ChannelApiCaller.findChannelByImessageId(this, channelQrCode.getChannelId(), new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this) {
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ExploreChannelActivity.this, new int[]{-101}, () -> {
                            Channel channel = data.getData(Channel.class);
                            Intent intent = new Intent(ExploreChannelActivity.this, ChannelActivity.class);
                            intent.putExtra(ExtraKeys.CHANNEL, channel);
                            intent.putExtra(ExtraKeys.MAY_NOT_ASSOCIATED, true);
                            startActivity(intent);
                        });
                    }
                });
            }
        }
    }

    private void setupViews() {
        String searchChannelBy = SharedPreferencesAccessor.DefaultPref.getSearchChannelBy(this);
        if(searchChannelBy == null || searchChannelBy.equals(searchByNames[0])) {
            binding.searchByAutoComplete.setText(searchByNames[0]);
            binding.searchTextInput.setHint(searchByNames[0]);
        }else if(searchChannelBy.equals(searchByNames[1])){
            binding.searchByAutoComplete.setText(searchByNames[1]);
            binding.searchTextInput.setHint(searchByNames[1]);
        }else {
            binding.searchByAutoComplete.setText(searchByNames[0]);
            binding.searchTextInput.setHint(searchByNames[0]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.layout_auto_complete_text_view_text, searchByNames);
        binding.searchByAutoComplete.setAdapter(adapter);
        binding.searchByAutoComplete.addTextChangedListener(new TextChangedYier(){
            @Override
            public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
                if(s.toString().equals(searchByNames[0])){
                    binding.searchTextInput.setHint(searchByNames[0]);
                    SharedPreferencesAccessor.DefaultPref.saveSearchChannelBy(ExploreChannelActivity.this, searchByNames[0]);
                }else if(s.toString().equals(searchByNames[1])){
                    binding.searchTextInput.setHint(searchByNames[1]);
                    SharedPreferencesAccessor.DefaultPref.saveSearchChannelBy(ExploreChannelActivity.this, searchByNames[1]);
                }
            }
        });
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.search_channel) {
                String searchText = UiUtil.getEditTextString(binding.searchTextInput);
                if (searchText == null || searchText.isEmpty()) {
                    MessageDisplayer.autoShow(this, "请输入内容", MessageDisplayer.Duration.SHORT);
                    return true;
                }
                String selectedSearchType = UiUtil.getEditTextString(binding.searchByAutoComplete);
                if (Objects.equals(selectedSearchType, searchByNames[0])) {
                    ChannelApiCaller.findChannelByImessageIdUser(this, searchText, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this) {
                        @Override
                        public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                            super.ok(data, raw, call);
                            data.commonHandleResult(ExploreChannelActivity.this, new int[]{-101}, () -> {
                                Channel channel = data.getData(Channel.class);
                                Intent intent = new Intent(ExploreChannelActivity.this, ChannelActivity.class);
                                intent.putExtra(ExtraKeys.CHANNEL, channel);
                                intent.putExtra(ExtraKeys.MAY_NOT_ASSOCIATED, true);
                                startActivity(intent);
                            });
                        }
                    });
                } else if (Objects.equals(selectedSearchType, searchByNames[1])) {
                    ChannelApiCaller.findChannelByEmail(this, searchText, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this) {
                        @Override
                        public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                            super.ok(data, raw, call);
                            data.commonHandleResult(ExploreChannelActivity.this, new int[]{-101}, () -> {
                                Channel channel = data.getData(Channel.class);
                                Intent intent = new Intent(ExploreChannelActivity.this, ChannelActivity.class);
                                intent.putExtra(ExtraKeys.CHANNEL, channel);
                                intent.putExtra(ExtraKeys.MAY_NOT_ASSOCIATED, true);
                                startActivity(intent);
                            });
                        }
                    });
                }
            } else if (item.getItemId() == R.id.search_by_qr_code) {
                new ScanQrCodeByBottomSheet(this, v -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    imageChosenActivityResultLauncher.launch(intent);
                }, v -> {
                    ScanOptions options = new ScanOptions();
                    options.setPrompt("请将频道二维码置于取景框内扫描。");
                    options.setCaptureActivity(QrScanActivity.class);
                    qrScanLauncher.launch(options.createScanIntent(this));
                }).show();
            }
            return true;
        });
    }
}