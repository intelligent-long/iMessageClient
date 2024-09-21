package com.longx.intelligent.android.ichat2.fragment.broadcastinteraction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.databinding.FragmentBroadcastCommentsInteractionBinding;

public class BroadcastCommentsInteractionFragment extends Fragment {
    private FragmentBroadcastCommentsInteractionBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBroadcastCommentsInteractionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}