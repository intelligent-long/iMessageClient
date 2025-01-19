package com.longx.intelligent.android.imessage.ui;

import android.graphics.Bitmap;

import com.davemorrissey.labs.subscaleview.decoder.DecoderFactory;
import com.davemorrissey.labs.subscaleview.decoder.SkiaImageDecoder;

/**
 * Created by LONG on 2024/2/11 at 8:54 AM.
 */
public class Argb8888DecoderFactory implements DecoderFactory<SkiaImageDecoder> {
    @Override
    public SkiaImageDecoder make() {
        return new SkiaImageDecoder(Bitmap.Config.ARGB_8888);
    }
}