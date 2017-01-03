package ai.houzi.custom.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.*;

import ai.houzi.custom.R;


public class AnimateDrawables extends View {

    private TranslateAnimation mAnimation;
    private Drawable mDr;

    public AnimateDrawables(Context context) {
        this(context, null);
    }

    public AnimateDrawables(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimateDrawables(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);
        setFocusableInTouchMode(true);

        mDr = ContextCompat.getDrawable(context, R.drawable.beach);
        mDr.setBounds(0, 0, mDr.getIntrinsicWidth(), mDr.getIntrinsicHeight());

        mAnimation = new TranslateAnimation(0, 100, 0, 200);
        mAnimation.setDuration(2000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.initialize(10, 10, 10, 10);

        mAnimation.startNow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        drawDraw(canvas);
        invalidate();
    }

    private Transformation mTransformation = new Transformation();

    public void drawDraw(Canvas canvas) {

        if (mDr != null) {
            int sc = canvas.save();
            Animation anim = mAnimation;
            if (anim != null) {
                anim.getTransformation(
                        AnimationUtils.currentAnimationTimeMillis(),
                        mTransformation);
                canvas.concat(mTransformation.getMatrix());
            }
            mDr.draw(canvas);
            canvas.restoreToCount(sc);
        }
    }
}
