package com.longx.intelligent.android.lib.recyclerview.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by LONG on 2024/1/6 at 4:10 PM.
 */
class SpaceGridItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private int spanCount;
    private int spacing;
    private boolean includeEdge;
    private SpaceGridDecorationDimensionProvider spaceGridDecorationDimensionProvider;

    public SpaceGridItemDecoration(Context context, int spanCount, int spacing, boolean includeEdge) {
        this.context = context;
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        setItemSpace(outRect, view, (com.longx.intelligent.android.lib.recyclerview.RecyclerView) parent);
        setItemSize(view, (com.longx.intelligent.android.lib.recyclerview.RecyclerView) parent);
    }

    private void setItemSpace(Rect outRect, View view, com.longx.intelligent.android.lib.recyclerview.RecyclerView parent) {
        int position = parent.getChildAdapterPosition(view); // item position
        if(parent.isPositionHeader(position) || parent.isPositionFooter(position)){
            return;
        }
        if(parent.hasHeader()) {
            position -= 1;
        }

        int column = position % spanCount; // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing; // item top
            }
        }
    }

    private void setItemSize(View view, com.longx.intelligent.android.lib.recyclerview.RecyclerView parent){
        int position = parent.getChildAdapterPosition(view); // item position
        if(parent.isPositionHeader(position) || parent.isPositionFooter(position)){
            return;
        }
        int itemPosition = position;
        if(parent.hasHeader()){
            itemPosition = position - 1;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int itemWidth = calculateItemWidth(context);
        if(spaceGridDecorationDimensionProvider == null) {
            layoutParams.width = layoutParams.height = itemWidth;
        }else {
            int width = spaceGridDecorationDimensionProvider.getWidth(itemPosition);
            int height = spaceGridDecorationDimensionProvider.getHeight(itemPosition);
            layoutParams.width = itemWidth;
            layoutParams.height = (int) (height / (width / (double)itemWidth));
        }
    }

    private int calculateItemWidth(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        return (screenWidth - (spanCount - 1) * spacing) / spanCount;
    }

    public void setSpaceGridDecorationDimensionProvider(SpaceGridDecorationDimensionProvider spaceGridDecorationDimensionProvider) {
        this.spaceGridDecorationDimensionProvider = spaceGridDecorationDimensionProvider;
    }
}
