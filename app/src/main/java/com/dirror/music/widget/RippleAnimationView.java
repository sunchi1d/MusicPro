package com.dirror.music.widget;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.dirror.music.R;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RippleAnimationView extends RelativeLayout {

    private Paint mPaint;
    int radius;
    public static final int STROKE_WIDTH = 5;
    int rippleColor;
    List<View> views = new ArrayList<>();
    private boolean animationRunning = false;

    public RippleAnimationView(Context context) {
        super(context);
    }

    public RippleAnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//抗锯齿
        radius = 54;
        rippleColor = ContextCompat.getColor(context, R.color.rippleColor);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(rippleColor);

        //        延迟时间
        int rippleDuration = 3500;
        int singleDelay = rippleDuration / 4;//间隔时间 （上一个波纹  和下一个波纹的）
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(radius + STROKE_WIDTH, radius + STROKE_WIDTH);
        params.addRule(CENTER_IN_PARENT);
        for (int i = 0; i < 4; i++) {
            RippleCircleView rippleCircleView = new RippleCircleView(this);
            addView(rippleCircleView, params);
            views.add(rippleCircleView);

            PropertyValuesHolder aplhaHolder = PropertyValuesHolder.ofFloat("Alpha", 1, 0);
            PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", 10);
            PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", 10);
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(rippleCircleView, aplhaHolder, scaleXHolder, scaleYHolder);
            animator.setDuration(rippleDuration);
            animator.setStartDelay(i * singleDelay);
            animator.setRepeatMode(ObjectAnimator.RESTART);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            rippleCircleView.setTag(animator);
        }
    }

    //启动动画   //停止动画

    public void startRippleAnimation() {
        if (!animationRunning) {
            for (View rippleView : views) {
                rippleView.setVisibility(VISIBLE);
                ((ObjectAnimator) rippleView.getTag()).start();
            }
            animationRunning = true;
        }

    }

    public void stopRippleAnimation() {
        if (animationRunning) {
            Collections.reverse(views);
            for (View rippleView : views) {
                rippleView.setVisibility(INVISIBLE);
                ((ObjectAnimator) rippleView.getTag()).end();
                ((ObjectAnimator) rippleView.getTag()).cancel();
            }
            animationRunning = false;
        }

    }


    public boolean isAnimationRunning() {
        return animationRunning;
    }

    public Paint getPaint() {
        return mPaint;
    }
}
