package com.longx.intelligent.android.ichat2.bottomsheet;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.databinding.BottomSheetBroadcastMoreOperationBinding;

/**
 * Created by LONG on 2024/8/14 at 上午12:24.
 */
public class BroadcastMoreOperationBottomSheet extends AbstractBottomSheet{
    private BottomSheetBroadcastMoreOperationBinding binding;

    public BroadcastMoreOperationBottomSheet(Activity activity) {
        super(activity);
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetBroadcastMoreOperationBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {

    }
}
