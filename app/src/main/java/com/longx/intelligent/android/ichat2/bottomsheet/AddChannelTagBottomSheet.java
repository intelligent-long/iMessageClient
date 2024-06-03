package com.longx.intelligent.android.ichat2.bottomsheet;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.databinding.BottomSheetAddChannelTagBinding;

/**
 * Created by LONG on 2024/6/3 at 5:49 PM.
 */
public class AddChannelTagBottomSheet extends AbstractBottomSheet{
    private BottomSheetAddChannelTagBinding binding;

    public AddChannelTagBottomSheet(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddChannelTagBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.addButton.setOnClickListener(v -> {
            dismiss();

        });
    }
}
