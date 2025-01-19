package com.longx.intelligent.android.imessage.yier;

import android.view.View;
import android.widget.CompoundButton;

/**
 * Created by LONG on 2024/1/30 at 6:42 AM.
 */
public class RecyclerItemYiers {
    public interface OnRecyclerItemActionYier {
        void onRecyclerItemAction(int position, Object... o);
    }

    public interface OnRecyclerItemClickYier {
        void onRecyclerItemClick(int position, View view);
    }

    public interface OnRecyclerItemCheckBoxCheckedChangeYier {
        void onRecyclerItemCheckBoxCheckedChange(int position, CompoundButton compoundButton, boolean checked);
    }
}
