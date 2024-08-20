package com.longx.intelligent.android.lib.recyclerview.decoration;

import android.content.Context;

import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

/**
 * Created by LONG on 2024/1/7 at 2:27 AM.
 */
public class SpaceGridDecorationSetter {
    private RecyclerView.ItemDecoration itemDecoration;

    public void setSpace(Context context, RecyclerView recyclerView, int columnCount, double spaceDp, boolean includeEdge, SpaceGridDecorationDimensionProvider spaceGridDecorationDimensionProvider){
        setSpace(context, recyclerView, columnCount, spaceDp, includeEdge, spaceGridDecorationDimensionProvider, false);
    }

    public void setSpace(Context context, RecyclerView recyclerView, int columnCount, double spaceDp, boolean includeEdge, SpaceGridDecorationDimensionProvider spaceGridDecorationDimensionProvider, boolean maxHeight){
        recyclerView.removeItemDecoration(itemDecoration);
        if(!maxHeight) {
            itemDecoration = new SpaceGridItemDecoration(context, columnCount, dpToPx(context, spaceDp), includeEdge);
            ((SpaceGridItemDecoration) itemDecoration).setSpaceGridDecorationDimensionProvider(spaceGridDecorationDimensionProvider);
        }else {
            itemDecoration = new MaxHeightSpaceGridItemDecoration(context, columnCount, dpToPx(context, spaceDp), includeEdge);
            ((MaxHeightSpaceGridItemDecoration) itemDecoration).setSpaceGridDecorationDimensionProvider(spaceGridDecorationDimensionProvider);
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
