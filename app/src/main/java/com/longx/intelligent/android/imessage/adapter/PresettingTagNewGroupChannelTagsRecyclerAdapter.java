package com.longx.intelligent.android.imessage.adapter;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemSettingTagNewGroupChannelTagBinding;
import com.longx.intelligent.android.imessage.yier.RecyclerItemYiers;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2025/4/20 at 上午2:56.
 */
public class PresettingTagNewGroupChannelTagsRecyclerAdapter extends WrappableRecyclerViewAdapter<PresettingTagNewGroupChannelTagsRecyclerAdapter.ViewHolder, List<GroupChannelTag>> {
    private final AppCompatActivity activity;
    private final ArrayList<String> newTagNames;
    private RecyclerItemYiers.OnRecyclerItemClickYier onDeleteClickYier;

    public PresettingTagNewGroupChannelTagsRecyclerAdapter(AppCompatActivity activity, ArrayList<String> newGroupChannelTagNames) {
        this.activity = activity;
        this.newTagNames = newGroupChannelTagNames;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemSettingTagNewGroupChannelTagBinding binding;
        public ViewHolder(RecyclerItemSettingTagNewGroupChannelTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemSettingTagNewGroupChannelTagBinding binding = RecyclerItemSettingTagNewGroupChannelTagBinding.inflate(activity.getLayoutInflater(), parent, false);
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
