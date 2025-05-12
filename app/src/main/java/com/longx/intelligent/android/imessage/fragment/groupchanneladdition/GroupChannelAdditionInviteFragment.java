package com.longx.intelligent.android.imessage.fragment.groupchanneladdition;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.databinding.FragmentGroupChannelAdditionInviteBinding;
import com.longx.intelligent.android.imessage.fragment.helper.BaseFragment;

public class GroupChannelAdditionInviteFragment extends BaseFragment {
    private FragmentGroupChannelAdditionInviteBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGroupChannelAdditionInviteBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        showContent();
        return binding.getRoot();
    }

    private void showContent() {

    }
}