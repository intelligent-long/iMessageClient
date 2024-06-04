package com.longx.intelligent.android.ichat2.bottomsheet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.ichat2.adapter.AddChannelToTagRecyclerAdapter;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChannelAssociation;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.data.request.AddChannelTagPostBody;
import com.longx.intelligent.android.ichat2.data.request.AddChannelsToTagPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.BottomSheetAddChannelTagBinding;
import com.longx.intelligent.android.ichat2.databinding.BottomSheetAddChannelToTagBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/6/3 at 5:49 PM.
 */
public class AddChannelToTagBottomSheet extends AbstractBottomSheet{
    private BottomSheetAddChannelToTagBinding binding;
    private final ChannelTag channelTag;
    private AddChannelToTagRecyclerAdapter adapter;

    public AddChannelToTagBottomSheet(AppCompatActivity activity, ChannelTag channelTag) {
        super(activity);
        this.channelTag = channelTag;
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddChannelToTagBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        showContent();
        setupYiers();
    }

    private void showContent() {
        List<ChannelAssociation> allAssociations = ChannelDatabaseManager.getInstance().findAllAssociations();
        List<Channel> channels = new ArrayList<>();
        allAssociations.forEach(association -> {
            channels.add(association.getChannel());
        });
        adapter = new AddChannelToTagRecyclerAdapter(getActivity(), channels);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupYiers() {
        binding.addButton.setOnClickListener(v -> {
            List<Channel> checkedChannels = adapter.getCheckedChannels();
            List<String> checkedChannelIchatIds = new ArrayList<>();
            checkedChannels.forEach(channel -> checkedChannelIchatIds.add(channel.getIchatId()));
            AddChannelsToTagPostBody postBody = new AddChannelsToTagPostBody(channelTag.getId(), checkedChannelIchatIds);
            ChannelApiCaller.addChannelsToTag(getActivity(), postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(getActivity()){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                    super.ok(data, row, call);
                    data.commonHandleResult(getActivity(), new int[]{}, () -> {
                        MessageDisplayer.autoShow(getActivity(), "已添加", MessageDisplayer.Duration.SHORT);
                        dismiss();
                    });
                }
            });
        });
    }
}
