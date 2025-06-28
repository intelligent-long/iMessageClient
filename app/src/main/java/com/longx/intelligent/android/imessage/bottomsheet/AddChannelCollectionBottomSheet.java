package com.longx.intelligent.android.imessage.bottomsheet;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.adapter.AddChannelCollectionRecyclerAdapter;
import com.longx.intelligent.android.imessage.adapter.AddChannelToTagRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.request.AddChannelCollectionPostBody;
import com.longx.intelligent.android.imessage.data.request.AddChannelsToTagPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.BottomSheetAddChannelCollectionBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2025/6/29 at 上午3:56.
 */
public class AddChannelCollectionBottomSheet extends AbstractBottomSheet{
    private BottomSheetAddChannelCollectionBinding binding;
    private final List<Channel> canAddChannels;
    private AddChannelCollectionRecyclerAdapter adapter;

    public AddChannelCollectionBottomSheet(AppCompatActivity activity, List<Channel> canAddChannels) {
        super(activity);
        this.canAddChannels = canAddChannels;
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddChannelCollectionBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        showContent();
        setupYiers();
    }

    private void showContent() {
        adapter = new AddChannelCollectionRecyclerAdapter(getActivity(), canAddChannels);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupYiers() {
        binding.addButton.setOnClickListener(v -> {
            List<Channel> checkedChannels = adapter.getCheckedChannels();
            List<String> checkedChannelImessageIds = new ArrayList<>();
            checkedChannels.forEach(channel -> checkedChannelImessageIds.add(channel.getImessageId()));
            AddChannelCollectionPostBody postBody = new AddChannelCollectionPostBody(checkedChannelImessageIds);
            ChannelApiCaller.addCollection((AppCompatActivity)getActivity(), postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(getActivity()){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(getActivity(), new int[]{}, () -> {
                        MessageDisplayer.autoShow(getActivity(), "已添加", MessageDisplayer.Duration.SHORT);
                        dismiss();
                    });
                }
            }.showWrongMessageOnlyWithToast(true));
        });
    }
}
