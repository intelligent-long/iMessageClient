package com.longx.intelligent.android.imessage.fragment.groupchanneladdition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.longx.intelligent.android.imessage.databinding.FragmentGroupChannelAdditionPendingBinding;
import com.longx.intelligent.android.imessage.databinding.FragmentGroupChannelAdditionSendBinding;

public class GroupChannelAdditionPendingFragment extends Fragment {
    private FragmentGroupChannelAdditionPendingBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGroupChannelAdditionPendingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}