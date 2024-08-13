package com.longx.intelligent.android.ichat2.bottomsheet;

import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.activity.ChatActivity;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.request.DeleteChannelAssociationPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.BottomSheetChannelMoreOperationBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/5/31 at 1:59 PM.
 */
public class ChannelMoreOperationBottomSheet extends AbstractBottomSheet{
    private BottomSheetChannelMoreOperationBinding binding;
    private Channel channel;

    public ChannelMoreOperationBottomSheet(AppCompatActivity activity, Channel channel) {
        super(activity);
        this.channel = channel;
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetChannelMoreOperationBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.note.setOnClickListener(v -> {
            dismiss();
            //TODO
        });
        binding.deleteChannel.setOnClickListener(v -> {
            dismiss();
            new ConfirmDialog(getActivity(), "是否继续？")
                    .setNegativeButton(null)
                    .setPositiveButton((dialog, which) -> {
                        deleteChannel();
                    })
                    .show();
        });
    }

    private void deleteChannel() {
        DeleteChannelAssociationPostBody postBody = new DeleteChannelAssociationPostBody(channel.getIchatId());
        ChannelApiCaller.deleteAssociatedChannel((AppCompatActivity)getActivity(), postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(getActivity()){
            @Override
            public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                super.ok(data, row, call);
                data.commonHandleResult(getActivity(), new int[]{}, () -> {
                    ActivityOperator.getActivitiesOf(ChatActivity.class).forEach(chatActivity -> {
                        if(chatActivity.getChannel().getIchatId().equals(channel.getIchatId())){
                            chatActivity.finish();
                        }
                    });
                    getActivity().finish();
                });
            }
        });
    }
}
