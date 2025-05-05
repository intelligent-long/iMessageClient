package com.longx.intelligent.android.imessage.bottomsheet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.adapter.AddGroupChannelToTagRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.request.AddGroupChannelsToTagPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.BottomSheetAddGroupChannelToTagBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/6/3 at 5:49 PM.
 */
public class AddGroupChannelsToTagBottomSheet extends AbstractBottomSheet{
    private BottomSheetAddGroupChannelToTagBinding binding;
    private final String tagId;
    private final List<GroupChannel> canAddChannels;
    private AddGroupChannelToTagRecyclerAdapter adapter;

    public AddGroupChannelsToTagBottomSheet(AppCompatActivity activity, String tagId, List<GroupChannel> canAddChannels) {
        super(activity);
        this.tagId = tagId;
        this.canAddChannels = canAddChannels;
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddGroupChannelToTagBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        showContent();
        setupYiers();
    }

    private void showContent() {
        adapter = new AddGroupChannelToTagRecyclerAdapter(getActivity(), canAddChannels);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupYiers() {
        binding.addButton.setOnClickListener(v -> {
            List<GroupChannel> checkedGroupChannels = adapter.getCheckedGroupChannels();
            List<String> checkedGroupChannelIds = new ArrayList<>();
            checkedGroupChannels.forEach(groupChannel -> checkedGroupChannelIds.add(groupChannel.getGroupChannelId()));
            AddGroupChannelsToTagPostBody postBody = new AddGroupChannelsToTagPostBody(tagId, checkedGroupChannelIds);
            GroupChannelApiCaller.addGroupChannelsToTag((LifecycleOwner) getActivity(), postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(getActivity()){
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
