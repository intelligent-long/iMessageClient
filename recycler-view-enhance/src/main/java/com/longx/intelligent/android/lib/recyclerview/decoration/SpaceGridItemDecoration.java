package com.longx.intelligent.android.lib.recyclerview.decoration;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

/**
 * Created by LONG on 2024/1/6 at 4:10 PM.
 */
public class SpaceGridItemDecoration extends RecyclerView.ItemDecoration {
    private final int spanCount;
    private final int spacing;
    private final boolean includeEdge;
    private SpaceGridDimensionProvider spaceGridDimensionProvider;
    private int recyclerViewWidth = -1;

    public SpaceGridItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull androidx.recyclerview.widget.RecyclerView parent, @NonNull RecyclerView.State state) {
        if (recyclerViewWidth == -1 && parent.getWidth() > 0) {
            recyclerViewWidth = parent.getWidth();
        }
        setItemSpace(outRect, view, (RecyclerView) parent);
        setItemSize(view, (RecyclerView) parent);
    }

    private void setItemSpace(Rect outRect, View view, RecyclerView parent) {
        int position = parent.getChildAdapterPosition(view);
        if(parent.isPositionHeader(position) || parent.isPositionFooter(position)){
            return;
        }
        if(parent.hasHeader()) {
            position -= 1;
        }

        int column = position % spanCount;

        if (includeEdge) {
            outRect.left = (int) Math.ceil(spacing - column * (float)spacing / spanCount);
            outRect.right = (int) Math.ceil((column + 1) * (float)spacing / spanCount);

            if (position < spanCount) {
                outRect.top = spacing;
            }
            outRect.bottom = spacing;
        } else {
            outRect.left = (int) Math.ceil(column * (float)spacing / spanCount);
            outRect.right = (int) Math.ceil(spacing - (column + 1) * (float)spacing / spanCount);
            if (position >= spanCount) {
                outRect.top = spacing;
            }
        }
    }

    private void setItemSize(View view, RecyclerView parent) {
        int position = parent.getChildAdapterPosition(view);
        if(parent.isPositionHeader(position) || parent.isPositionFooter(position)){
            return;
        }

        int itemPosition = position;
        if(parent.hasHeader()){
            itemPosition = position - 1;
        }

        if (recyclerViewWidth == -1) {
            recyclerViewWidth = parent.getWidth();
        }

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int itemWidth = calculateItemWidth();
        if (spaceGridDimensionProvider == null) {
            layoutParams.width = layoutParams.height = itemWidth;
        } else {
            int width = spaceGridDimensionProvider.getWidth(itemPosition);
            int height = spaceGridDimensionProvider.getHeight(itemPosition);
            layoutParams.width = itemWidth;
            layoutParams.height = (int) Math.ceil(height / (width / (float) itemWidth));
        }
    }

    private int calculateItemWidth() {
        return (int) Math.ceil((recyclerViewWidth - (spanCount - 1) * spacing) / (float) spanCount);
    }

    public void setSpaceGridDecorationDimensionProvider(SpaceGridDimensionProvider spaceGridDimensionProvider) {
        this.spaceGridDimensionProvider = spaceGridDimensionProvider;
    }
}
