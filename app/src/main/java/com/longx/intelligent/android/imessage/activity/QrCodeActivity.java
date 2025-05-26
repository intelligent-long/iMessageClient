package com.longx.intelligent.android.imessage.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChannelQrCode;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelQrCode;
import com.longx.intelligent.android.imessage.data.QrCodeData;
import com.longx.intelligent.android.imessage.databinding.ActivityQrCodeBinding;
import com.longx.intelligent.android.imessage.dialog.OperatingDialog;
import com.longx.intelligent.android.imessage.util.AppUtil;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.JsonUtil;
import com.longx.intelligent.android.imessage.util.QRCodeUtil;
import com.longx.intelligent.android.imessage.util.ResourceUtil;
import com.longx.intelligent.android.imessage.util.ShareUtil;

import java.io.IOException;
import java.util.Date;

public class QrCodeActivity extends BaseActivity {
    private ActivityQrCodeBinding binding;
    private String description;
    private Object typeObject;
    private Bitmap qrCodeBitmap;
    private String id;

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
            id = channel.getImessageId();
            ChannelQrCode channelQrCode = new ChannelQrCode(AppUtil.getVersionCode(this), AppUtil.getVersionName(this), id, new Date());
            QrCodeData<ChannelQrCode> qrCodeData = new QrCodeData<>(QrCodeData.Type.CHANNEL, channelQrCode);
            return JsonUtil.toJson(qrCodeData);
        }else if(typeObject instanceof GroupChannel){
            GroupChannel groupChannel = (GroupChannel) typeObject;
            id = groupChannel.getGroupChannelId();
            GroupChannelQrCode groupChannelQrCode = new GroupChannelQrCode(AppUtil.getVersionCode(this), AppUtil.getVersionName(this), id, new Date());
            QrCodeData<GroupChannelQrCode> qrCodeData = new QrCodeData<>(QrCodeData.Type.GROUP_CHANNEL, groupChannelQrCode);
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
        Bitmap appLogo = ResourceUtil.getMipMapBitmapSquare(this, R.mipmap.ic_launcher, 1.6F);
        int qrCodeDark = ColorUtil.getColor(this, R.color.qr_code);
        int qrCodeLight = ColorUtil.getAttrColor(this, android.R.attr.colorBackground);
        qrCodeBitmap = QRCodeUtil.createQRCodeBitmap(getQrCodeContent(), 5000, 5000,
                "UTF-8", "H", 0, qrCodeDark, qrCodeLight, appLogo, 0.17F);
        binding.qrCode.setImageBitmap(qrCodeBitmap);
    }

    private void setupYiers() {
        binding.saveQrCodeButton.setOnClickListener(v -> {
            new Thread(() -> {
                OperatingDialog operatingDialog = new OperatingDialog(this);
                operatingDialog.create().show();
                if(PublicFileAccessor.QrCode.saveQrCode(this, id, qrCodeBitmap) != null){
                    MessageDisplayer.autoShow(this, "已保存", MessageDisplayer.Duration.SHORT);
                }else {
                    MessageDisplayer.autoShow(this, "保存失败", MessageDisplayer.Duration.SHORT);
                }
                operatingDialog.dismiss();
            }).start();
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.share){
                ShareUtil.shareBitmap(QrCodeActivity.this, qrCodeBitmap, id);
                return true;
            }
            return false;
        });
    }
}