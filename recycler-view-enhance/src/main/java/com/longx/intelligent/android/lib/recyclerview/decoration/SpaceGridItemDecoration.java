package com.longx.intelligent.android.lib.recyclerview.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

/**
 * Created by LONG on 2024/1/6 at 4:10 PM.
 */
public class SpaceGridItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private int spanCount;
    private int spacing;
    private boolean includeEdge;
    private SpaceGridDecorationDimensionProvider spaceGridDecorationDimensionProvider;
    private int recyclerViewWidth = -1;

    public SpaceGridItemDecoration(Context context, int spanCount, int spacing, boolean includeEdge) {
        this.context = context;
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, androidx.recyclerview.widget.RecyclerView parent, RecyclerView.State state) {
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
