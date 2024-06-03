package com.longx.intelligent.android.ichat2.bottomsheet;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.data.request.AddTagPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.BottomSheetAddChannelTagBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;

import retrofit2.Call;
import retrofit2.Response;

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
            String inputtedTagName = UiUtil.getEditTextString(binding.addTagInput);
            AddTagPostBody postBody = new AddTagPostBody(inputtedTagName);
            ChannelApiCaller.addTag(getActivity(), postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(getActivity()){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                    super.ok(data, row, call);
                    data.commonHandleResult(getActivity(), new int[]{}, () -> {
                        dismiss();
                    });
                }
            });
        });
    }
}
