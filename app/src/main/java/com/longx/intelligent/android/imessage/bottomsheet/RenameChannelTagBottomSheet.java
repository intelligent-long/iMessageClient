package com.longx.intelligent.android.imessage.bottomsheet;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.data.request.ChangeChannelTagNamePostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.BottomSheetRenameChannelTagBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/6/3 at 5:49 PM.
 */
public class RenameChannelTagBottomSheet extends AbstractBottomSheet{
    private BottomSheetRenameChannelTagBinding binding;
    private final ChannelTag channelTag;

    public RenameChannelTagBottomSheet(AppCompatActivity activity, ChannelTag channelTag) {
        super(activity);
        this.channelTag = channelTag;
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetRenameChannelTagBinding.inflate(getActivity().getLayoutInflater());
        binding.tagNameInput.setText(channelTag.getName());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.doneButton.setOnClickListener(v -> {
            String inputtedTagName = UiUtil.getEditTextString(binding.tagNameInput);
            ChangeChannelTagNamePostBody postBody = new ChangeChannelTagNamePostBody(channelTag.getTagId(), inputtedTagName);
            ChannelApiCaller.changeTagName((AppCompatActivity)getActivity(), postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(getActivity()){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(getActivity(), new int[]{-101}, () -> {
                        MessageDisplayer.autoShow(getActivity(), "更改成功", MessageDisplayer.Duration.SHORT);
                        dismiss();
                    });
                }
            }.showWrongMessageOnlyWithToast(true));
        });
    }
}
