package com.longx.intelligent.android.ichat2.fragment.channeladdition;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.databinding.FragmentSendBinding;

public class SendFragment extends Fragment {
    private FragmentSendBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSendBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}