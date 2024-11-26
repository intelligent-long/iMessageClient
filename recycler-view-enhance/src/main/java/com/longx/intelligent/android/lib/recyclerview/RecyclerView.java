package com.longx.intelligent.android.lib.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.lib.recyclerviewenhance.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LONG on 2024/1/7 at 1:56 AM.
 */
public class RecyclerView extends androidx.recyclerview.widget.RecyclerView {
    private RecyclerViewAdapterWrapper recyclerViewAdapterWrapper;
    private boolean bottomInit;
    private final int bottomInitBehavior;
    private final List<ApproachEdgeYierTrigger> approachEdgeYierTriggers = new ArrayList<>();
    private final Set<OnScrollUpDownYier> onScrollUpDownYiers = new HashSet<>();
    private final Set<OnThresholdScrollUpDownYier> onThresholdScrollUpDownYiers = new HashSet<>();

    public RecyclerView(@NonNull Context context) {
        this(context, null);
        init();
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RecyclerView,
                0, 0);
        try {
            bottomInit = a.getBoolean(R.styleable.RecyclerView_bottom_init, false);
            bottomInitBehavior = a.getInt(R.styleable.RecyclerView_bottom_init_behavior, 0);
        } finally {
            a.recycle();
        }
        init();
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RecyclerView,
                0, 0);
        try {
            bottomInit = a.getBoolean(R.styleable.RecyclerView_bottom_init, false);
            bottomInitBehavior = a.getInt(R.styleable.RecyclerView_bottom_init_behavior, 0);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init(){
        forceHasOverlappingRendering(false);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    onThresholdScrollUpDownYiers.forEach(onThresholdScrollUpDownYier -> {
                        if(Math.abs(dy) > onThresholdScrollUpDownYier.threshold){
                            onThresholdScrollUpDownYier.onScrollUp();
                        }
                    });
                    onScrollUpDownYiers.forEach(OnScrollUpDownYier::onScrollUp);
                } else if (dy < 0) {
                    onThresholdScrollUpDownYiers.forEach(onThresholdScrollUpDownYier -> {
                        if(Math.abs(dy) > onThresholdScrollUpDownYier.threshold){
                            onThresholdScrollUpDownYier.onScrollDown();
                        }
                    });
                    onScrollUpDownYiers.forEach(OnScrollUpDownYier::onScrollDown);
                }
            }
        });
    }

    public void setBottomInit(boolean bottomInit){
        this.bottomInit = bottomInit;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof WrappableRecyclerViewAdapter)) {
            throw new RuntimeException("Adapter must be a WrappableRecyclerViewAdapter");
        }
        recyclerViewAdapterWrapper = new RecyclerViewAdapterWrapper((WrappableRecyclerViewAdapter) adapter);
        super.setAdapter(recyclerViewAdapterWrapper);
        if (bottomInit) {
            scrollToEnd(false);
            if (bottomInitBehavior == 1) {
                bottomInit = false;
            }
        }
    }

    @Nullable
    @Override
    public WrappableRecyclerViewAdapter getAdapter() {
        RecyclerViewAdapterWrapper headerAndFooterWrapAdapter = getWrapAdapter();
        if(headerAndFooterWrapAdapter == null) return null;
        return headerAndFooterWrapAdapter.getWrappedAdapter();
    }

    @Nullable
    private RecyclerViewAdapterWrapper getWrapAdapter() {
        return (RecyclerViewAdapterWrapper) super.getAdapter();
    }

    public void setHeaderView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to set must not be null!");
        } else if(recyclerViewAdapterWrapper == null) {
            throw new IllegalStateException("You must set a adapter before!");
        } else {
            recyclerViewAdapterWrapper.setHeaderView(view);
        }
    }

    public void setFooterView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to set must not be null!");
        } else if(recyclerViewAdapterWrapper == null) {
            throw new IllegalStateException("You must set a adapter before!");
        } else {
            recyclerViewAdapterWrapper.setFooterView(view);
        }
    }

    public void removeHeaderView() {
        if(recyclerViewAdapterWrapper == null) {
            throw new IllegalStateException("You must set a adapter before!");
        } else {
            recyclerViewAdapterWrapper.removeHeaderView();
        }
    }

    public void removeFooterView() {
        if(recyclerViewAdapterWrapper == null) {
            throw new IllegalStateException("You must set a adapter before!");
        } else {
            recyclerViewAdapterWrapper.removeFooterView();
        }
    }

    public boolean hasHeader() {
        if(recyclerViewAdapterWrapper == null) {
            throw new IllegalStateException("You must set an adapter before!");
        } else {
            return recyclerViewAdapterWrapper.hasHeader();
        }
    }

    public boolean hasFooter() {
        if(recyclerViewAdapterWrapper == null) {
            throw new IllegalStateException("You must set an adapter before!");
        } else {
            return recyclerViewAdapterWrapper.hasFooter();
        }
    }

    public boolean isPositionHeader(int position){
        if(recyclerViewAdapterWrapper == null) {
            throw new IllegalStateException("You must set an adapter before!");
        } else {
            return recyclerViewAdapterWrapper.isPositionHeader(position);
        }
    }

    public boolean isPositionFooter(int position){
        if(recyclerViewAdapterWrapper == null) {
            throw new IllegalStateException("You must set an adapter before!");
        } else {
            return recyclerViewAdapterWrapper.isPositionFooter(position);
        }
    }

    public void scrollToEnd(boolean smooth){
        Adapter<?> adapter = getWrapAdapter();
        if(adapter == null){
            throw new RuntimeException("You must set an adapter before!");
        }
        try {
            if (smooth) {
                int itemCount = adapter.getItemCount();
                LayoutManager layoutManager = getLayoutManager();
                int firstVisibleItemPosition = -1;
                if(layoutManager instanceof LinearLayoutManager) {
                    firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                }
                if(firstVisibleItemPosition == -1 || itemCount - firstVisibleItemPosition > 21) {
                    int position = itemCount - 21;
                    if(position <= 0){
                        position = itemCount;
                    }
                    scrollToPosition(position);
                }
                smoothScrollToPosition(itemCount - 1);
            } else {
                scrollToPosition(adapter.getItemCount() - 1);
            }
        }catch (IllegalArgumentException ignore){};
    }

    public void scrollToStart(boolean smooth){
        Adapter<?> adapter = getWrapAdapter();
        if(adapter == null){
            throw new RuntimeException("You must set an adapter before!");
        }
        try {
            if (smooth) {
                LayoutManager layoutManager = getLayoutManager();
                int lastVisibleItemPosition = -1;
                if(layoutManager instanceof LinearLayoutManager) {
                    lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                }
                if(lastVisibleItemPosition == -1 || lastVisibleItemPosition > 21) {
                    scrollToPosition(21);
                }
                smoothScrollToPosition(0);
            } else {
                scrollToPosition(0);
            }
        }catch (IllegalArgumentException ignore){};
    }

    public boolean isAtStart() {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) return false;
        if (layoutManager instanceof LinearLayoutManager) {
            int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            if(hasHeader()){
                return firstVisibleItemPosition == 1;
            }else {
                return firstVisibleItemPosition == 0;
            }
        }
        return false;
    }

    public boolean isAtEnd() {
        LayoutManager layoutManager = getLayoutManager();
        WrappableRecyclerViewAdapter adapter = getAdapter();
        if (layoutManager == null || adapter == null) return false;
        if (layoutManager instanceof LinearLayoutManager) {
            int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            int itemCount = adapter.getItemCount();
            if(hasHeader() && hasFooter()){
                return lastVisibleItemPosition - 1 == itemCount;
            }else if(hasHeader()) {
                return lastVisibleItemPosition == itemCount;
            }else if(hasFooter()){
                return lastVisibleItemPosition - 1 == itemCount - 1;
            } else {
                return lastVisibleItemPosition == itemCount - 1;
            }
        }
        return false;
    }

    public boolean isApproachStart(int approach) {
        if(approach > 15 || approach < 0) throw new IllegalArgumentException("approach is illegal");
        LayoutManager layoutManager = getLayoutManager();
        WrappableRecyclerViewAdapter adapter = getAdapter();
        if (layoutManager == null || adapter == null) return false;
        if (layoutManager instanceof LinearLayoutManager) {
            int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            if(hasHeader()){
                return firstVisibleItemPosition <= 1 + approach;
            }else {
                return firstVisibleItemPosition <= approach;
            }
        }
        return false;
    }

    public boolean isApproachEnd(int approach) {
        if(approach > 15 || approach < 0) throw new IllegalArgumentException("approach is illegal");
        LayoutManager layoutManager = getLayoutManager();
        WrappableRecyclerViewAdapter adapter = getAdapter();
        if (layoutManager == null || adapter == null) return false;
        if (layoutManager instanceof LinearLayoutManager) {
            int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            int itemCount = adapter.getItemCount();
            if(hasHeader() && hasFooter()){
                return lastVisibleItemPosition - 1 >= itemCount - approach;
            }else if(hasHeader()) {
                return lastVisibleItemPosition >= itemCount - approach;
            }else if(hasFooter()){
                return lastVisibleItemPosition - 1 >= itemCount - 1 - approach;
            } else {
                return lastVisibleItemPosition >= itemCount - 1 - approach;
            }
        }
        return false;
    }

    public interface OnApproachEdgeYier{
        void onApproachStart();
        void onApproachEnd();
    }

    private class ApproachEdgeYierTrigger extends OnScrollListener{
        private final OnApproachEdgeYier onApproachEdgeYier;
        private final int approach;

        public ApproachEdgeYierTrigger(OnApproachEdgeYier onApproachEdgeYier, int approach) {
            this.onApproachEdgeYier = onApproachEdgeYier;
            this.approach = approach;
        }

        @Override
        public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            LayoutManager layoutManager = getLayoutManager();
            if(!(layoutManager instanceof LinearLayoutManager)){
                return;
            }
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
            if (pastVisibleItems + visibleItemCount >= totalItemCount - approach) {
                onApproachEdgeYier.onApproachEnd();
            }
            if (pastVisibleItems <= approach) {
                onApproachEdgeYier.onApproachStart();
            }
        }
    }

    public void addOnApproachEdgeYier(int approach, OnApproachEdgeYier onApproachEdgeYier){
        ApproachEdgeYierTrigger approachEdgeYierTrigger = new ApproachEdgeYierTrigger(onApproachEdgeYier, approach);
        approachEdgeYierTriggers.add(approachEdgeYierTrigger);
        addOnScrollListener(approachEdgeYierTrigger);
    }

    public void removeOnApproachEdgeYier(OnApproachEdgeYier onApproachEdgeYier){
        for (ApproachEdgeYierTrigger approachEdgeYierTrigger : approachEdgeYierTriggers) {
            if(approachEdgeYierTrigger.onApproachEdgeYier.equals(onApproachEdgeYier)){
                removeOnScrollListener(approachEdgeYierTrigger);
                return;
            }
        }
    }

    public interface OnScrollUpDownYier {
        void onScrollUp();
        void onScrollDown();
    }

    public void addOnScrollUpDownYier(OnScrollUpDownYier onScrollUpDownYier){
        onScrollUpDownYiers.add(onScrollUpDownYier);
    }

    public void removeOnScrollUpDownYier(OnScrollUpDownYier onScrollUpDownYier){
        onScrollUpDownYiers.remove(onScrollUpDownYier);
    }


    public static abstract class OnThresholdScrollUpDownYier implements OnScrollUpDownYier{
        private final int threshold;

        public OnThresholdScrollUpDownYier(int threshold) {
            this.threshold = threshold;
        }

        public int getThreshold() {
            return threshold;
        }
    }

    public void addOnThresholdScrollUpDownYier(OnThresholdScrollUpDownYier onScrollUpDownYier){
        onThresholdScrollUpDownYiers.add(onScrollUpDownYier);
    }

    public void removeOnThresholdScrollUpDownYier(OnThresholdScrollUpDownYier onScrollUpDownYier){
        onThresholdScrollUpDownYiers.remove(onScrollUpDownYier);
    }
}