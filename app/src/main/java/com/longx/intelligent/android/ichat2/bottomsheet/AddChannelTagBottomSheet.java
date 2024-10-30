package com.longx.intelligent.android.ichat2.bottomsheet;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.ichat2.data.request.AddChannelTagPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.BottomSheetAddChannelTagBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/6/3 at 5:49 PM.
 */
public class AddChannelTagBottomSheet extends AbstractBottomSheet{
    private BottomSheetAddChannelTagBinding binding;
    private final ResultsYier resultsYier;

    public AddChannelTagBottomSheet(AppCompatActivity activity, ResultsYier resultsYier) {
        super(activity);
        this.resultsYier = resultsYier;
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddChannelTagBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.addButton.setOnClickListener(v -> {
            String inputtedTagName = UiUtil.getEditTextString(binding.addTagInput);
            AddChannelTagPostBody postBody = new AddChannelTagPostBody(inputtedTagName);
            ChannelApiCaller.addTag((AppCompatActivity)getActivity(), postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(getActivity()){
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
