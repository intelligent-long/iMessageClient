package com.longx.intelligent.android.imessage.bottomsheet;

import android.app.Activity;
import android.view.View;

import com.longx.intelligent.android.imessage.databinding.BottomSheetAddChannelBinding;

/**
 * Created by LONG on 2025/4/14 at 下午12:17.
 */
public class AddChannelBottomSheet extends AbstractBottomSheet{
    private BottomSheetAddChannelBinding binding;
    private View.OnClickListener exploreChannelClickYier;
    private View.OnClickListener qrCodeClickYier;

    public AddChannelBottomSheet(Activity activity) {
        super(activity);
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddChannelBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.optionSearchChannel.setOnClickListener(v -> {
            dismiss();
            if(exploreChannelClickYier != null) exploreChannelClickYier.onClick(v);
        });
        binding.optionQrCode.setOnClickListener(v -> {
            dismiss();
            if(qrCodeClickYier != null) qrCodeClickYier.onClick(v);
        });
    }

    public void setExploreChannelClickYier(View.OnClickListener exploreChannelClickYier) {
        this.exploreChannelClickYier = exploreChannelClickYier;
    }

    public void setQrCodeClickYier(View.OnClickListener qrCodeClickYier) {
        this.qrCodeClickYier = qrCodeClickYier;
    }
}
