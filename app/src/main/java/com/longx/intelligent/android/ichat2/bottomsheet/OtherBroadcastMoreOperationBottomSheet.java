package com.longx.intelligent.android.ichat2.bottomsheet;

import android.app.Activity;
import android.view.View;

import com.longx.intelligent.android.ichat2.databinding.BottomSheetOtherBroadcastMoreOperationBinding;

/**
 * Created by LONG on 2024/8/14 at 上午12:24.
 */
public class OtherBroadcastMoreOperationBottomSheet extends AbstractBottomSheet{
    private BottomSheetOtherBroadcastMoreOperationBinding binding;
    private View.OnClickListener excludeBroadcastChannelClickYier;

    public OtherBroadcastMoreOperationBottomSheet(Activity activity) {
        super(activity);
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetOtherBroadcastMoreOperationBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.excludeBroadcastChannel.setOnClickListener(v -> {
            dismiss();
            if(excludeBroadcastChannelClickYier != null) excludeBroadcastChannelClickYier.onClick(v);
        });
    }

    public void setExcludeBroadcastChannelClickYier(View.OnClickListener excludeBroadcastChannelClickYier) {
        this.excludeBroadcastChannelClickYier = excludeBroadcastChannelClickYier;
    }
}
