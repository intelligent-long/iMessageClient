package com.longx.intelligent.android.ichat2.fragment.broadcastinteraction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.databinding.FragmentBroadcastLikeBinding;

public class BroadcastLikeFragment extends Fragment {
    private FragmentBroadcastLikeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBroadcastLikeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}