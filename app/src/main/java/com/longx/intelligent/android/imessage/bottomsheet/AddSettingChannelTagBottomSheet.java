package com.longx.intelligent.android.imessage.bottomsheet;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.databinding.BottomSheetAddSettingChannelTagBinding;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.yier.ResultsYier;

/**
 * Created by LONG on 2024/6/3 at 5:49 PM.
 */
public class AddSettingChannelTagBottomSheet extends AbstractBottomSheet{
    private BottomSheetAddSettingChannelTagBinding binding;
    private final ResultsYier resultsYier;

    public AddSettingChannelTagBottomSheet(AppCompatActivity activity, ResultsYier resultsYier) {
        super(activity);
        this.resultsYier = resultsYier;
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddSettingChannelTagBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.addButton.setOnClickListener(v -> {
            dismiss();
            String inputtedTagName = UiUtil.getEditTextString(binding.addTagInput);
            resultsYier.onResults(inputtedTagName);
        });
    }
}
