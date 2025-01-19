package com.longx.intelligent.android.imessage.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by LONG on 2024/2/5 at 1:35 AM.
 */
public class NoPaddingTextView extends AppCompatTextView {

    private final Paint paint = new Paint();
    private final Rect bounds = new Rect();

    public NoPaddingTextView(Context context) {
        super(context);
        init();
    }

    public NoPaddingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoPaddingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        paint.setColor(getCurrentTextColor());
        paint.setTextSize(getTextSize());
        paint.setTypeface(getTypeface());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String text = setBounds();
        int left = bounds.left;
        int bottom = bounds.bottom;
        bounds.offset(-left, -bounds.top);
        canvas.drawText(text, -left, bounds.bottom - bottom, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setBounds();
        setMeasuredDimension(bounds.width() + getPaddingLeft() + getPaddingRight(),
                bounds.height() + getPaddingTop() + getPaddingBottom());
    }

    @NonNull
    private String setBounds() {
        String text = getText().toString();
        int textLength = text.length();
        paint.getTextBounds(text, 0, textLength, bounds);
        if (textLength == 0) {
            bounds.right = bounds.left;
        }
        return text;
    }
}
