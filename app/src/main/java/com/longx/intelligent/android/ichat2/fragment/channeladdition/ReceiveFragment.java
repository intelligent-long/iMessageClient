package com.longx.intelligent.android.ichat2.fragment.channeladdition;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ChannelAdditionActivitiesActivity;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionInfo;
import com.longx.intelligent.android.ichat2.databinding.FragmentReceiveBinding;
import com.longx.intelligent.android.ichat2.yier.ChannelAdditionActivitiesFetchYier;

import java.util.List;

public class ReceiveFragment extends Fragment implements ChannelAdditionActivitiesFetchYier {
    private FragmentReceiveBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReceiveBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStartFetch() {

    }

    @Override
    public void onFetched(List<ChannelAdditionInfo> channelAdditionInfos) {

    }

    @Override
    public void onFailure(String failureMessage) {

    }
}