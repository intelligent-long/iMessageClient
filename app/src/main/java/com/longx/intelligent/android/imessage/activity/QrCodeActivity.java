package com.longx.intelligent.android.imessage.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChannelQrCode;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelQrCode;
import com.longx.intelligent.android.imessage.data.QrCodeData;
import com.longx.intelligent.android.imessage.databinding.ActivityQrCodeBinding;
import com.longx.intelligent.android.imessage.util.AppUtil;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.util.JsonUtil;
import com.longx.intelligent.android.imessage.util.QRCodeUtil;

import java.util.Date;

public class QrCodeActivity extends BaseActivity {
    private ActivityQrCodeBinding binding;
    private String description;
    private Object typeObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCloseBackNavigation(binding.toolbar);
        intentData();
        showContent();
        setupYiers();
    }

    private String getQrCodeContent(){
        if(typeObject instanceof Channel){
            Channel channel = (Channel) typeObject;
            ChannelQrCode channelQrCode = new ChannelQrCode(AppUtil.getVersionCode(this), AppUtil.getVersionName(this), channel.getImessageId(), new Date());
            QrCodeData qrCodeData = new QrCodeData(QrCodeData.Type.CHANNEL, channelQrCode);
            return JsonUtil.toJson(qrCodeData);
        }else if(typeObject instanceof GroupChannel){
            GroupChannel groupChannel = (GroupChannel) typeObject;
            GroupChannelQrCode groupChannelQrCode = new GroupChannelQrCode(AppUtil.getVersionCode(this), AppUtil.getVersionName(this), groupChannel.getGroupChannelId(), new Date());
            QrCodeData qrCodeData = new QrCodeData(QrCodeData.Type.GROUP_CHANNEL, groupChannelQrCode);
            return JsonUtil.toJson(qrCodeData);
        }
        return null;
    }

    private void intentData() {
        typeObject = getIntent().getParcelableExtra(ExtraKeys.TYPE_OBJECT);
        description = getIntent().getStringExtra(ExtraKeys.DESCRIPTION);
    }

    private void showContent() {
        binding.description.setText(description);
        Bitmap appLogo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        int qrCodeDark = ColorUtil.getColor(this, R.color.qr_code);
        int qrCodeLight = ColorUtil.getAttrColor(this, android.R.attr.colorBackground);
        Bitmap qrCodeBitmap = QRCodeUtil.createQRCodeBitmap(getQrCodeContent(), 5000, 5000,
                "UTF-8", "H", 0, qrCodeDark, qrCodeLight, appLogo, 0.18F);
        binding.qrCode.setImageBitmap(qrCodeBitmap);
    }

    private void setupYiers() {
        binding.saveQrCodeButton.setOnClickListener(v -> {

        });
    }
}