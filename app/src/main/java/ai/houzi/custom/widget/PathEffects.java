package ai.houzi.custom.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class PathEffects extends View {
    private Paint mPaint;
    private Path mPath;
    private PathEffect[] mEffects;
    private float mPhase;

    public PathEffects(Context context) {
        this(context, null);
    }

    public PathEffects(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathEffects(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setFocusable(true);
        setFocusableInTouchMode(true);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        mPaint.setColor(Color.RED);

        mPath = new Path();
        mPath.moveTo(10, 10);
        mPath.lineTo(80, 90);
        mPath.lineTo(150, 10);
        mPath.lineTo(260, 170);
        mPath.lineTo(400, 50);

        mEffects = new PathEffect[6];

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        RectF bounds = new RectF();
        mPath.computeBounds(bounds, false);
        canvas.translate(10 - bounds.left, 10 - bounds.top);

        makeEffects(mEffects, mPhase);
        mPhase += 1;
        invalidate();

        for (PathEffect mEffect : mEffects) {
            mPaint.setPathEffect(mEffect);
            canvas.drawPath(mPath, mPaint);
            canvas.translate(0, 28);
        }
    }

    private void makeEffects(PathEffect[] mEffects, float mPhase) {
        mEffects[0] = null;
        mEffects[1] = new CornerPathEffect(10);
        mEffects[2] = new DashPathEffect(new float[]{10, 5, 5, 5}, mPhase);
        mEffects[3] = new PathDashPathEffect(makePathDash(), 12, mPhase, PathDashPathEffect.Style.ROTATE);
        mEffects[4] = new ComposePathEffect(mEffects[2], mEffects[1]);
        mEffects[5] = new ComposePathEffect(mEffects[3], mEffects[1]);
    }

    private static Path makePathDash() {
        Path p = new Path();
        p.moveTo(0, 0);
        p.lineTo(4, -4);
        p.lineTo(8, -4);
        p.lineTo(4, 0);
        p.lineTo(8, 4);
        p.lineTo(4, 4);
        return p;
    }
}
