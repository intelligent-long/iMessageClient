package com.longx.intelligent.android.ichat2.bottomsheet;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.data.request.ChangeChannelTagNamePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.BottomSheetRenameChannelTagBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;

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
            ChangeChannelTagNamePostBody postBody = new ChangeChannelTagNamePostBody(channelTag.getId(), inputtedTagName);
            ChannelApiCaller.changeTagName((AppCompatActivity)getActivity(), postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(getActivity()){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                    super.ok(data, row, call);
                    data.commonHandleResult(getActivity(), new int[]{-101}, () -> {
                        MessageDisplayer.autoShow(getActivity(), "更改成功", MessageDisplayer.Duration.SHORT);
                        dismiss();
                    });
                }
            }.showWrongMessageOnlyWithToast(true));
        });
    }
}
