package com.longx.intelligent.android.ichat2.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.ichat2.adapter.FastLocateChannelRecyclerAdapter;
import com.longx.intelligent.android.ichat2.databinding.DialogFastLocateBinding;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/7/25 at 下午12:28.
 */
public class FastLocateDialog extends AbstractDialog{
    public static String[] LOCATE_CHANNEL = {
            ".", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
    };

    private static final int COLUMN_COUNT = 5;
    private final String[] locateTexts;

    public FastLocateDialog(Activity activity, String[] locateTexts) {
        super(activity);
        this.locateTexts = locateTexts;
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        return builder.create();
    }

    @Override
    protected View createView(LayoutInflater layoutInflater) {
        DialogFastLocateBinding binding = DialogFastLocateBinding.inflate(layoutInflater);
        showContent(binding);
        return binding.getRoot();
    }

    private void showContent(DialogFastLocateBinding binding) {
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), COLUMN_COUNT));
        FastLocateChannelRecyclerAdapter adapter = new FastLocateChannelRecyclerAdapter(getActivity(), locateTexts);
        binding.recyclerView.setAdapter(adapter);
    }
}
