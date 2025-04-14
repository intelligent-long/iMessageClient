package com.longx.intelligent.android.imessage.fragment.groupchanneladdition;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.databinding.FragmentGroupChannelAdditionSendBinding;

public class GroupChannelAdditionSendFragment extends Fragment {
    private FragmentGroupChannelAdditionSendBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGroupChannelAdditionSendBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}