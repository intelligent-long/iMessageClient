package com.longx.intelligent.android.imessage.bottomsheet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.adapter.AddChannelCollectionRecyclerAdapter;
import com.longx.intelligent.android.imessage.adapter.AddGroupChannelCollectionRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.request.AddChannelCollectionPostBody;
import com.longx.intelligent.android.imessage.data.request.AddGroupChannelCollectionPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.BottomSheetAddChannelCollectionBinding;
import com.longx.intelligent.android.imessage.databinding.BottomSheetAddGroupChannelCollectionBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2025/6/29 at 上午3:56.
 */
public class AddGroupChannelCollectionBottomSheet extends AbstractBottomSheet{
    private BottomSheetAddGroupChannelCollectionBinding binding;
    private final List<GroupChannel> canAddGroupChannels;
    private AddGroupChannelCollectionRecyclerAdapter adapter;

    public AddGroupChannelCollectionBottomSheet(AppCompatActivity activity, List<GroupChannel> canAddGroupChannels) {
        super(activity);
        this.canAddGroupChannels = canAddGroupChannels;
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddGroupChannelCollectionBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        showContent();
        setupYiers();
    }

    private void showContent() {
        adapter = new AddGroupChannelCollectionRecyclerAdapter(getActivity(), canAddGroupChannels);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupYiers() {
        binding.addButton.setOnClickListener(v -> {
            List<GroupChannel> checkedGroupChannels = adapter.getCheckedGroupChannels();
            List<String> checkedGroupChannelIds = new ArrayList<>();
            checkedGroupChannels.forEach(groupChannel -> checkedGroupChannelIds.add(groupChannel.getGroupChannelId()));
            AddGroupChannelCollectionPostBody postBody = new AddGroupChannelCollectionPostBody(checkedGroupChannelIds);
            GroupChannelApiCaller.addGroupCollection((AppCompatActivity)getActivity(), postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(getActivity()){
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
