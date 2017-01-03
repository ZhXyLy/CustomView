package ai.houzi.custom.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import ai.houzi.custom.R;

/**
 * 用于翻转的ImageView
 */

public class Rotate3dView extends ImageView {
    public Rotate3dView(Context context) {
        this(context, null);
    }

    public Rotate3dView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Rotate3dView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void switchFront() {

    }

    public void switchBack() {
        ObjectAnimator rotationY = ObjectAnimator.ofFloat(this, "rotationY", 0, 90);
        rotationY.setDuration(1000);
        rotationY.start();
        rotationY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                toBack();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void toBack() {
        setImageResource(R.drawable.tabbar_work);
        ObjectAnimator rotationY = ObjectAnimator.ofFloat(this, "rotationY", 90, 180);
        rotationY.setDuration(1000);
        rotationY.start();
    }
}
