package com.longx.intelligent.android.imessage.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

/**
 * Created by LONG on 2024/2/12 at 7:32 PM.
 */
public class SubsamplingScalePhotoView extends SubsamplingScaleImageView {
    public SubsamplingScalePhotoView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public SubsamplingScalePhotoView(Context context) {
        super(context);
        init();
    }

    private void init(){
        setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        setDoubleTapZoomDpi(370);
        setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        setBitmapDecoderFactory(new Argb8888DecoderFactory());
    }
}
