package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.ichat2.databinding.RecyclerItemFastLocateTextBinding;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.Arrays;

/**
 * Created by LONG on 2024/7/26 at 上午12:38.
 */
public class FastLocateChannelRecyclerAdapter extends WrappableRecyclerViewAdapter<FastLocateChannelRecyclerAdapter.ViewHolder, String>{
    private final Activity activity;
    private final String[] locateTexts;
    private final String[] existTexts;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemFastLocateTextBinding binding;
        public ViewHolder(RecyclerItemFastLocateTextBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public FastLocateChannelRecyclerAdapter(Activity activity, String[] locateTexts, String[] existTexts) {
        this.activity = activity;
        this.locateTexts = locateTexts;
        this.existTexts = existTexts;
    }

    @Override
    public int getItemCount() {
        return locateTexts.length;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemFastLocateTextBinding binding = RecyclerItemFastLocateTextBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.textView.setText(locateTexts[position]);
        if(Arrays.asList(existTexts).contains(locateTexts[position])){
            setupYiers(holder, position);
        }else {
            holder.binding.textView.setAlpha(0.26F);
            holder.binding.textView.setClickable(false);
        }
    }

    private void setupYiers(ViewHolder holder, int position) {
        holder.binding.textView.setOnClickListener(v -> {
            OnItemClickYier<String> onItemClickYier = getOnItemClickYier();
            if(onItemClickYier != null) onItemClickYier.onItemClick(position, locateTexts[position]);
        });
    }

}
