package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ChatImageActivity;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChatImageBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChatMessageBinding;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.RecyclerItemYiers;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LONG on 2024/5/28 at 9:24 PM.
 */
public class ChatImagePagerAdapter extends RecyclerView.Adapter<ChatImagePagerAdapter.ViewHolder> {
    private Activity activity;
    private List<String> imageFilePaths;
    private RecyclerItemYiers.OnRecyclerItemActionYier onRecyclerItemActionYier;
    private RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier;

    public ChatImagePagerAdapter(Activity activity, List<String> imageFilePaths) {
        this.activity = activity;
        this.imageFilePaths = imageFilePaths;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerItemChatImageBinding binding;

        public ViewHolder(RecyclerItemChatImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChatImageBinding binding = RecyclerItemChatImageBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return imageFilePaths.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imageFilePaths.get(position);
        holder.binding.photoView.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener(){
            @Override
            public void onImageLoadError(Exception e) {
                super.onImageLoadError(e);
                holder.binding.photoView.setVisibility(View.GONE);
                holder.binding.loadFailedView.setVisibility(View.VISIBLE);
                Glide.with(activity.getApplicationContext())
                        .load(new File(imagePath))
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                holder.binding.spareImageView.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        });
        holder.binding.photoView.setImage(ImageSource.uri(Uri.fromFile(new File(imagePath))));
        setupPhotoView(holder.binding, position);
    }

    private void setupPhotoView(RecyclerItemChatImageBinding binding, int position) {
        binding.photoView.setOnClickListener(v -> {
            if(onRecyclerItemClickYier != null) onRecyclerItemClickYier.onRecyclerItemClick(position, binding.photoView);
        });
        binding.photoView.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
            @Override
            public void onScaleChanged(float newScale, int origin) {
                if(onRecyclerItemActionYier != null) onRecyclerItemActionYier.onRecyclerItemAction(position, newScale != binding.photoView.getMinScale());
            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {

            }
        });
    }

    public void setOnRecyclerItemActionYier(RecyclerItemYiers.OnRecyclerItemActionYier onRecyclerItemActionYier) {
        this.onRecyclerItemActionYier = onRecyclerItemActionYier;
    }

    public void setOnRecyclerItemClickYier(RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier) {
        this.onRecyclerItemClickYier = onRecyclerItemClickYier;
    }
}
