package com.dirror.music.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class RippleCircleView extends View {
    private RippleAnimationView mRippleAnimationView;

    public RippleCircleView(RippleAnimationView rippleAnimationView) {
        super(rippleAnimationView.getContext());
        mRippleAnimationView = rippleAnimationView;
    }

    public RippleCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float radius = Math.min(getWidth(), getHeight()) / 2;
        canvas.drawCircle(radius, radius, radius - RippleAnimationView.STROKE_WIDTH, mRippleAnimationView.getPaint());
    }
}
