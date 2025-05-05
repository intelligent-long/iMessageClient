package com.longx.intelligent.android.imessage.bottomsheet;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.data.request.AddChannelTagPostBody;
import com.longx.intelligent.android.imessage.data.request.AddGroupChannelTagPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.BottomSheetAddChannelTagBinding;
import com.longx.intelligent.android.imessage.databinding.BottomSheetAddGroupChannelTagBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.yier.ResultsYier;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/6/3 at 5:49 PM.
 */
public class AddGroupChannelTagBottomSheet extends AbstractBottomSheet{
    private BottomSheetAddGroupChannelTagBinding binding;
    private final ResultsYier resultsYier;

    public AddGroupChannelTagBottomSheet(AppCompatActivity activity, ResultsYier resultsYier) {
        super(activity);
        this.resultsYier = resultsYier;
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddGroupChannelTagBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.addButton.setOnClickListener(v -> {
            String inputtedTagName = UiUtil.getEditTextString(binding.addTagInput);
            AddGroupChannelTagPostBody postBody = new AddGroupChannelTagPostBody(inputtedTagName);
            GroupChannelApiCaller.addTag((AppCompatActivity)getActivity(), postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(getActivity()){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(getActivity(), new int[]{}, () -> {
                        MessageDisplayer.autoShow(getActivity(), "已添加", MessageDisplayer.Duration.SHORT);
                        dismiss();
                        resultsYier.onResults();
                    });
                }
            }.showWrongMessageOnlyWithToast(true));
        });
    }
}
