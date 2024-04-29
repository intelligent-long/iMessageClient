package com.longx.intelligent.android.lib.recyclerview;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by LONG on 2024/2/16 at 10:42 PM.
 */
public abstract class WrappableRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, DATA> extends RecyclerView.Adapter<VH>{
    private RecyclerViewAdapterWrapper recyclerViewAdapterWrapper;
    private OnItemClickYier<DATA> onItemClickYier;

    public RecyclerViewAdapterWrapper getWrapper() {
        return recyclerViewAdapterWrapper;
    }

    void setRecyclerViewAdapterWrapper(RecyclerViewAdapterWrapper recyclerViewAdapterWrapper) {
        this.recyclerViewAdapterWrapper = recyclerViewAdapterWrapper;
    }

    public interface OnItemClickYier<T>{
        void onItemClick(int position, T data);
    }

    public void setOnItemClickYier(OnItemClickYier<DATA> onItemClickYier) {
        this.onItemClickYier = onItemClickYier;
    }

    public OnItemClickYier<DATA> getOnItemClickYier() {
        return onItemClickYier;
    }
}
