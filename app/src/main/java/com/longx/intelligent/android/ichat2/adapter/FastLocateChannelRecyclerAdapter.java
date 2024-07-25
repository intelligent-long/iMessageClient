package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChatMessageBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemFastLocateTextBinding;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

/**
 * Created by LONG on 2024/7/26 at 上午12:38.
 */
public class FastLocateChannelRecyclerAdapter extends WrappableRecyclerViewAdapter<FastLocateChannelRecyclerAdapter.ViewHolder, String>{
    private final Activity activity;
    private final String[] texts;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemFastLocateTextBinding binding;
        public ViewHolder(RecyclerItemFastLocateTextBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public FastLocateChannelRecyclerAdapter(Activity activity, String[] texts) {
        this.activity = activity;
        this.texts = texts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemFastLocateTextBinding binding = RecyclerItemFastLocateTextBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.textView.setText(texts[position]);
    }

    @Override
    public int getItemCount() {
        return texts.length;
    }
}
