package com.longx.intelligent.android.ichat2.adapter;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.MediaActivity;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemSendBroadcastMediasBinding;
import com.longx.intelligent.android.ichat2.media.data.Media;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Created by LONG on 2024/8/5 at 下午2:45.
 */
public class SendBroadcastMediasRecyclerAdapter extends WrappableRecyclerViewAdapter<SendBroadcastMediasRecyclerAdapter.ViewHolder, Uri> {
    private final Activity activity;
    private final ActivityResultLauncher<Intent> returnFromPreviewToSendMediaResultLauncher;
    private final ArrayList<Media> mediaList;

    public SendBroadcastMediasRecyclerAdapter(Activity activity, ActivityResultLauncher<Intent> returnFromPreviewToSendMediaResultLauncher, ArrayList<Media> mediaList) {
        this.activity = activity;
        this.returnFromPreviewToSendMediaResultLauncher = returnFromPreviewToSendMediaResultLauncher;
        this.mediaList = mediaList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemSendBroadcastMediasBinding binding;
        public ViewHolder(RecyclerItemSendBroadcastMediasBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemSendBroadcastMediasBinding binding = RecyclerItemSendBroadcastMediasBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = mediaList.get(position).getUri();
        GlideApp
                .with(activity.getApplicationContext())
                .load(uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.image);
        setupYiers(holder, position);
    }

    private void setupYiers(ViewHolder holder, int position) {
        holder.binding.image.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MediaActivity.class);
            intent.putParcelableArrayListExtra(ExtraKeys.MEDIAS, mediaList);
            intent.putExtra(ExtraKeys.POSITION, position);
            intent.putExtra(ExtraKeys.BUTTON_TEXT, "移除");
            MediaActivity.setActionButtonYier(v1 -> {
                int currentItem = MediaActivity.getInstance().getCurrentItemIndex();
                ArrayList<Media> mediaList1 = MediaActivity.getInstance().getMediaList();
                mediaList1.remove(currentItem);
                if(mediaList1.isEmpty()) MediaActivity.getInstance().finish();
                MediaActivity.getInstance().getBinding().toolbar.setTitle((currentItem == mediaList1.size() ? currentItem : currentItem + 1) + " / " + mediaList1.size());
                MediaActivity.getInstance().getAdapter().removeItem(currentItem);
                Intent intent1 = new Intent();
                intent1.putParcelableArrayListExtra(ExtraKeys.MEDIAS, mediaList1);
                MediaActivity.getInstance().setResult(RESULT_OK, intent1);
            });
            returnFromPreviewToSendMediaResultLauncher.launch(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeAllDataAndShow(ArrayList<Media> mediaList){
        this.mediaList.clear();
        this.mediaList.addAll(mediaList);
        notifyDataSetChanged();
    }
}
