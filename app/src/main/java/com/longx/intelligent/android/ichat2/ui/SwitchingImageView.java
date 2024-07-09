package com.longx.intelligent.android.ichat2.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.longx.intelligent.android.ichat2.R;
import java.util.Timer;
import java.util.TimerTask;

public class SwitchingImageView extends AppCompatImageView {
    private int[] imageResources;
    private int currentIndex = 0;
    private int interval = 1000;
    private Timer timer;
    private TimerTask timerTask;
    private int transitionDuration = 500;
    private boolean isTransitionEnabled = true;

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
            transitionDuration = a.getInt(R.styleable.SwitchingImageView_transition_duration, transitionDuration);
            isTransitionEnabled = a.getBoolean(R.styleable.SwitchingImageView_transition_enabled, isTransitionEnabled);
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
                        int nextIndex = (currentIndex + 1) % imageResources.length;
                        if (isTransitionEnabled) {
                            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(SwitchingImageView.this, "alpha", 1f, 0f);
                            fadeOut.setDuration(transitionDuration / 2);
                            fadeOut.setInterpolator(new AccelerateDecelerateInterpolator());
                            fadeOut.start();

                            fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(android.animation.Animator animation) {
                                    setImageResource(imageResources[nextIndex]);
                                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(SwitchingImageView.this, "alpha", 0f, 1f);
                                    fadeIn.setDuration(transitionDuration / 2);
                                    fadeIn.setInterpolator(new LinearOutSlowInInterpolator());
                                    fadeIn.start();
                                }
                            });
                        } else {
                            setImageResource(imageResources[nextIndex]);
                        }
                        currentIndex = nextIndex;
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

    public void setTransitionDuration(int transitionDuration) {
        this.transitionDuration = transitionDuration;
    }

    public void setTransitionEnabled(boolean isTransitionEnabled) {
        this.isTransitionEnabled = isTransitionEnabled;
    }
}
