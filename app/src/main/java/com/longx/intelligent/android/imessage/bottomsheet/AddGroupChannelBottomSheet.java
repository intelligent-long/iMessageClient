package com.longx.intelligent.android.imessage.bottomsheet;

import android.app.Activity;
import android.view.View;

import com.longx.intelligent.android.imessage.databinding.BottomSheetAddGroupChannelBinding;

/**
 * Created by LONG on 2025/4/14 at 上午11:53.
 */
public class AddGroupChannelBottomSheet extends AbstractBottomSheet{
    private BottomSheetAddGroupChannelBinding binding;
    private View.OnClickListener exploreChannelClickYier;
    private View.OnClickListener createClickYier;
    private View.OnClickListener inviteClickYier;

    public AddGroupChannelBottomSheet(Activity activity) {
        super(activity);
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddGroupChannelBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.optionSearchChannel.setOnClickListener(v -> {
            dismiss();
            if(exploreChannelClickYier != null) exploreChannelClickYier.onClick(v);
        });
        binding.optionCreate.setOnClickListener(v -> {
            dismiss();
            if(createClickYier != null) createClickYier.onClick(v);
        });
        binding.optionInviteJoinChannel.setOnClickListener(v -> {
            dismiss();
            if(inviteClickYier != null) inviteClickYier.onClick(v);
        });
    }

    public void setExploreChannelClickYier(View.OnClickListener exploreChannelClickYier) {
        this.exploreChannelClickYier = exploreChannelClickYier;
    }

    public void setCreateClickYier(View.OnClickListener createClickYier) {
        this.createClickYier = createClickYier;
    }

    public void setInviteClickYier(View.OnClickListener inviteClickYier) {
        this.inviteClickYier = inviteClickYier;
    }
}
