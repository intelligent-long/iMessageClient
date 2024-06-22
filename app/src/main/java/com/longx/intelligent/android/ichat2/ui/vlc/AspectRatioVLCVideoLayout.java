package com.longx.intelligent.android.ichat2.ui.vlc;

import android.content.Context;
import android.util.AttributeSet;

import org.videolan.libvlc.util.VLCVideoLayout;

/**
 * Created by LONG on 2024/6/22 at 3:51 PM.
 */
public class AspectRatioVLCVideoLayout extends VLCVideoLayout {

    private int videoWidth = 0;
    private int videoHeight = 0;

    public AspectRatioVLCVideoLayout(Context context) {
        super(context);
    }

    public AspectRatioVLCVideoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioVLCVideoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setVideoSize(int width, int height) {
        videoWidth = width;
        videoHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);

        if (videoWidth > 0 && videoHeight > 0) {
            if (videoWidth * height > width * videoHeight) {
                height = width * videoHeight / videoWidth;
            } else if (videoWidth * height < width * videoHeight) {
                width = height * videoWidth / videoHeight;
            }
        }

        setMeasuredDimension(width, height);
    }
}