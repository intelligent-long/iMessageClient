package com.longx.intelligent.android.ichat2.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import com.longx.intelligent.android.ichat2.R;
import java.util.Timer;
import java.util.TimerTask;

public class SwitchingImageView extends AppCompatImageView {
    private int[] imageResources;
    private int currentIndex = 0;
    private int interval = 1000;
    private Timer timer;
    private TimerTask timerTask;

    public SwitchingImageView(Context context) {
        super(context);
        init(null);
    }

    public SwitchingImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SwitchingImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SwitchingImageView);
            interval = a.getInt(R.styleable.SwitchingImageView_interval, interval);
            int imageArrayResId = a.getResourceId(R.styleable.SwitchingImageView_images, 0);
            if (imageArrayResId != 0) {
                setImageResources(getResources().obtainTypedArray(imageArrayResId));
            }
            a.recycle();
        }
    }

    public void setImageResources(int[] imageResources) {
        this.imageResources = imageResources;
        currentIndex = 0;
    }

    public void setImageResources(TypedArray typedArray) {
        int length = typedArray.length();
        imageResources = new int[length];
        for (int i = 0; i < length; i++) {
            imageResources[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        currentIndex = 0;
    }

    public void startAnimating() {
        if (timer != null) {
            stopAnimating();
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                post(() -> {
                    if (imageResources != null && imageResources.length > 0) {
                        setImageResource(imageResources[currentIndex]);
                        currentIndex = (currentIndex + 1) % imageResources.length;
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, interval);
    }

    public void stopAnimating() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setInterval(int interval) {
        this.interval = interval;
        if (timer != null) {
            startAnimating();
        }
    }
}
