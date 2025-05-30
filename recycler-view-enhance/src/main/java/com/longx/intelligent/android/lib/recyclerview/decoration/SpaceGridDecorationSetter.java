package com.longx.intelligent.android.lib.recyclerview.decoration;

import android.content.Context;

import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

/**
 * Created by LONG on 2024/1/7 at 2:27 AM.
 */
public class SpaceGridDecorationSetter {
    private RecyclerView.ItemDecoration itemDecoration;

    public void setSpace(Context context, RecyclerView recyclerView, int columnCount, double spaceDp, boolean includeEdge, SpaceGridDimensionProvider spaceGridDimensionProvider){
        setSpace(context, recyclerView, columnCount, spaceDp, includeEdge, spaceGridDimensionProvider, false);
    }

    public void setSpace(Context context, RecyclerView recyclerView, int columnCount, double spaceDp, boolean includeEdge, SpaceGridDimensionProvider spaceGridDimensionProvider, boolean maxHeight){
        recyclerView.removeItemDecoration(itemDecoration);
        if(!maxHeight) {
            itemDecoration = new SpaceGridItemDecoration(columnCount, dpToPx(context, spaceDp), includeEdge);
            ((SpaceGridItemDecoration) itemDecoration).setSpaceGridDecorationDimensionProvider(spaceGridDimensionProvider);
        }else {
            itemDecoration = new MaxHeightSpaceGridItemDecoration(context, columnCount, dpToPx(context, spaceDp), includeEdge);
            ((MaxHeightSpaceGridItemDecoration) itemDecoration).setSpaceGridDecorationDimensionProvider(spaceGridDimensionProvider);
        }
        recyclerView.addItemDecoration(itemDecoration);
    }

    private static int dpToPx(Context context, double dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) (dp * density));
    }

    public void remove(RecyclerView recyclerView){
        recyclerView.removeItemDecoration(itemDecoration);
    }

}
