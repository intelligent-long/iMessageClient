package com.longx.intelligent.android.ichat2.bottomsheet;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.databinding.BottomSheetChannelMoreOperationBinding;

/**
 * Created by LONG on 2024/5/31 at 1:59 PM.
 */
public class ChannelMoreOperationBottomSheet extends AbstractBottomSheet{
    private BottomSheetChannelMoreOperationBinding binding;

    public ChannelMoreOperationBottomSheet(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetChannelMoreOperationBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupListeners();
    }

    private void setupListeners() {
        binding.removeChannel.setOnClickListener(v -> {
            dismiss();

        });
    }
}
