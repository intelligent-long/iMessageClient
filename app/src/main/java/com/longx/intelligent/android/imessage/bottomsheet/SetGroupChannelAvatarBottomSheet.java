package com.longx.intelligent.android.imessage.bottomsheet;

import android.app.Activity;
import android.view.View;

import com.longx.intelligent.android.imessage.databinding.BottomSheetSetGroupChannelAvatarBinding;

/**
 * Created by LONG on 2025/4/22 at 上午3:43.
 */
public class SetGroupChannelAvatarBottomSheet extends AbstractBottomSheet{
    private BottomSheetSetGroupChannelAvatarBinding binding;
    private final boolean chose;
    private final View.OnClickListener chooseYier;
    private final View.OnClickListener removeYier;

    public SetGroupChannelAvatarBottomSheet(Activity activity, boolean chose, View.OnClickListener chooseYier, View.OnClickListener removeYier) {
        super(activity);
        this.chose = chose;
        this.chooseYier = chooseYier;
        this.removeYier = removeYier;
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetSetGroupChannelAvatarBinding.inflate(getActivity().getLayoutInflater());
        if(!chose) binding.remove.setVisibility(View.GONE);
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.choose.setOnClickListener(v -> {
            dismiss();
            chooseYier.onClick(v);
        });
        binding.remove.setOnClickListener(v -> {
            dismiss();
            removeYier.onClick(v);
        });
    }
}
