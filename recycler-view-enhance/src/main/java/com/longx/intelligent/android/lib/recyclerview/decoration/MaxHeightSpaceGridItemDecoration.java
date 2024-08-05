package com.longx.intelligent.android.lib.recyclerview.decoration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

/**
 * Created by LONG on 2024/8/5 at 下午4:28.
 */
public class MaxHeightSpaceGridItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private int spanCount;
    private int spacing;
    private boolean includeEdge;
    private SpaceGridDecorationDimensionProvider spaceGridDecorationDimensionProvider;
    private int recyclerViewWidth = -1;
    private boolean widthInitialized = false;

    public MaxHeightSpaceGridItemDecoration(Context context, int spanCount, int spacing, boolean includeEdge) {
        this.context = context;
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, androidx.recyclerview.widget.RecyclerView parent, RecyclerView.State state) {
        if (!widthInitialized) {
            parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 移除监听器以避免多次调用
                    parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    recyclerViewWidth = parent.getWidth();
                    widthInitialized = true;
                    calculateAndSetRecyclerViewHeight((RecyclerView) parent);
                }
            });
        } else {
            setItemSpace(outRect, view, (RecyclerView) parent);
            setItemSize(view, (RecyclerView) parent);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void calculateAndSetRecyclerViewHeight(RecyclerView recyclerView) {
        int totalHeight = 0;
        int itemWidth = calculateItemWidth();
        int childCount = recyclerView.getAdapter() != null ? recyclerView.getAdapter().getItemCount() : 0;
        int rows = (int) Math.ceil((double) childCount / spanCount);

        for (int i = 0; i < rows; i++) {
            totalHeight += itemWidth;
            if(i != rows - 1) totalHeight += spacing;
        }

        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        layoutParams.height = totalHeight;
        recyclerView.setLayoutParams(layoutParams);
        if(recyclerView.getAdapter() != null) recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void setItemSpace(Rect outRect, View view, RecyclerView parent) {
        int position = parent.getChildAdapterPosition(view);
        if (parent.isPositionHeader(position) || parent.isPositionFooter(position)) {
            return;
        }
        if (parent.hasHeader()) {
            position -= 1;
        }

        int column = position % spanCount;

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            if (position < spanCount) {
                outRect.top = spacing;
            }
            outRect.bottom = spacing;
        } else {
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (position >= spanCount) {
                outRect.top = spacing;
            }
        }
    }

    private void setItemSize(View view, RecyclerView parent) {
        int position = parent.getChildAdapterPosition(view);
        if (parent.isPositionHeader(position) || parent.isPositionFooter(position)) {
            return;
        }

        int itemPosition = position;
        if (parent.hasHeader()) {
            itemPosition = position - 1;
        }

        if (recyclerViewWidth == -1) {
            recyclerViewWidth = parent.getWidth();
        }

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int itemWidth = calculateItemWidth();
        if (spaceGridDecorationDimensionProvider == null) {
            layoutParams.width = layoutParams.height = itemWidth;
        } else {
            int width = spaceGridDecorationDimensionProvider.getWidth(itemPosition);
            int height = spaceGridDecorationDimensionProvider.getHeight(itemPosition);
            layoutParams.width = itemWidth;
            layoutParams.height = (int) (height / (width / (double) itemWidth));
        }
    }

    private int calculateItemWidth() {
        return (recyclerViewWidth - (spanCount - 1) * spacing) / spanCount;
    }

    public void setSpaceGridDecorationDimensionProvider(SpaceGridDecorationDimensionProvider spaceGridDecorationDimensionProvider) {
        this.spaceGridDecorationDimensionProvider = spaceGridDecorationDimensionProvider;
    }
}
