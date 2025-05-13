package com.longx.intelligent.android.imessage.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.imessage.adapter.AddChannelToTagRecyclerAdapter;
import com.longx.intelligent.android.imessage.adapter.ChooseOneChannelRecyclerAdapter;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.databinding.DialogChooseOneChannelBinding;

import java.util.List;

/**
 * Created by LONG on 2025/5/13 at 上午3:54.
 */
public class ChooseOneChannelDialog extends AbstractDialog<ChooseOneChannelDialog>{
    private DialogChooseOneChannelBinding binding;
    private String title;
    private final List<Channel> channels;
    private ChooseOneChannelRecyclerAdapter adapter;
    private DialogButtonInfo positiveButtonInfo;
    private DialogButtonInfo negativeDialogButtonInfo;
    private DialogButtonInfo neutralButtonInfo;
    private Channel choseChannel;

    public ChooseOneChannelDialog(Activity activity, String title, List<Channel> channels, Channel choseChannel) {
        super(activity);
        this.title = title;
        this.channels = channels;
        this.choseChannel = choseChannel;
    }

    @Override
    protected AlertDialog onCreate(MaterialAlertDialogBuilder builder) {
        builder
                .setTitle(title);
        if(positiveButtonInfo != null){
            builder.setPositiveButton(positiveButtonInfo.getText() == null ? "确定" : positiveButtonInfo.getText(), positiveButtonInfo.getYier());
        }
        if(negativeDialogButtonInfo != null){
            builder.setNegativeButton(negativeDialogButtonInfo.getText() == null ? "取消" : negativeDialogButtonInfo.getText(), negativeDialogButtonInfo.getYier());
        }
        if(neutralButtonInfo != null){
            builder.setNeutralButton(neutralButtonInfo.getText() == null ? "其他选项" : neutralButtonInfo.getText(), neutralButtonInfo.getYier());
        }
        return builder.create();
    }

    @Override
    protected View onCreateView(LayoutInflater layoutInflater) {
        binding = DialogChooseOneChannelBinding.inflate(layoutInflater);
        showContent();
        return binding.getRoot();
    }

    private void showContent() {
        adapter = new ChooseOneChannelRecyclerAdapter(getActivity(), channels, choseChannel);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    public ChooseOneChannelDialog setPositiveButton(DialogInterface.OnClickListener yier){
        positiveButtonInfo = new DialogButtonInfo(null, yier);
        return this;
    }

    public ChooseOneChannelDialog setNegativeButton(DialogInterface.OnClickListener yier){
        negativeDialogButtonInfo = new DialogButtonInfo(null, yier);
        return this;
    }

    public ChooseOneChannelDialog setNeutralButton(DialogInterface.OnClickListener yier){
        neutralButtonInfo = new DialogButtonInfo(null, yier);
        return this;
    }

    public ChooseOneChannelDialog setPositiveButton(String text, DialogInterface.OnClickListener yier){
        positiveButtonInfo = new DialogButtonInfo(text, yier);
        return this;
    }

    public ChooseOneChannelDialog setNegativeButton(String text, DialogInterface.OnClickListener yier){
        negativeDialogButtonInfo = new DialogButtonInfo(text, yier);
        return this;
    }

    public ChooseOneChannelDialog setNeutralButton(String text, DialogInterface.OnClickListener yier){
        neutralButtonInfo = new DialogButtonInfo(text, yier);
        return this;
    }

    public ChooseOneChannelRecyclerAdapter getAdapter() {
        return adapter;
    }
}
