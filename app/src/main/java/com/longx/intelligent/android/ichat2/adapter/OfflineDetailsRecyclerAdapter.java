package com.longx.intelligent.android.ichat2.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.data.OfflineDetail;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemOfflineDetailBinding;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2024/5/24 at 12:11 AM.
 */
public class OfflineDetailsRecyclerAdapter extends WrappableRecyclerViewAdapter<OfflineDetailsRecyclerAdapter.ViewHolder, OfflineDetailsRecyclerAdapter.ItemData> {
    private final AppCompatActivity activity;
    private final List<OfflineDetailsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();

    public OfflineDetailsRecyclerAdapter(AppCompatActivity activity, List<OfflineDetail> offlineDetails) {
        this.activity = activity;
        offlineDetails.sort((o1, o2) -> -o1.getTime().compareTo(o2.getTime()));
        offlineDetails.forEach(offlineDetail -> {
            itemDataList.add(new ItemData(offlineDetail));
        });
    }

    public static class ItemData{
        private final OfflineDetail offlineDetail;

        public ItemData(OfflineDetail offlineDetail) {
            this.offlineDetail = offlineDetail;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemOfflineDetailBinding binding;

        public ViewHolder(RecyclerItemOfflineDetailBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemOfflineDetailBinding binding = RecyclerItemOfflineDetailBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        holder.binding.time.setText(TimeUtil.formatRelativeTime(itemData.offlineDetail.getTime()));
        holder.binding.ip.setText(itemData.offlineDetail.getIp());
        holder.binding.desc.setText(itemData.offlineDetail.getDesc());
    }
}
