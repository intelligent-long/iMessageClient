package com.longx.intelligent.android.imessage.adapter;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemSettingTagNewChannelTagBinding;
import com.longx.intelligent.android.imessage.yier.RecyclerItemYiers;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/6/3 at 8:47 PM.
 */
public class SettingTagNewChannelTagsRecyclerAdapter extends WrappableRecyclerViewAdapter<SettingTagNewChannelTagsRecyclerAdapter.ViewHolder, List<ChannelTag>> {
    private final AppCompatActivity activity;
    private final ArrayList<String> newTagNames = new ArrayList<>();
    private RecyclerItemYiers.OnRecyclerItemClickYier onDeleteClickYier;

    public SettingTagNewChannelTagsRecyclerAdapter(AppCompatActivity activity) {
        this.activity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemSettingTagNewChannelTagBinding binding;
        public ViewHolder(RecyclerItemSettingTagNewChannelTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemSettingTagNewChannelTagBinding binding = RecyclerItemSettingTagNewChannelTagBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return newTagNames.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String newTagName = newTagNames.get(position);
        holder.binding.tagName.setText(newTagName);
        setUpYiers(holder, position);
    }

    private void setUpYiers(ViewHolder holder, int position) {
        holder.binding.clickViewDelete.setOnClickListener(v -> {
            if(onDeleteClickYier != null) onDeleteClickYier.onRecyclerItemClick(position, holder.binding.clickViewDelete);
        });
    }

    public void setOnDeleteClickYier(RecyclerItemYiers.OnRecyclerItemClickYier onDeleteClickYier) {
        this.onDeleteClickYier = onDeleteClickYier;
    }

    public void addAndShow(String newTagName){
        newTagNames.add(newTagName);
        notifyItemInserted(newTagNames.size());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAndShow(int position){
        newTagNames.remove(position);
        notifyDataSetChanged();
    }

    public ArrayList<String> getNewTagNames() {
        return newTagNames;
    }
}
