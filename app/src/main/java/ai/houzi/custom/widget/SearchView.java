package ai.houzi.custom.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


public class SearchView extends View {
    private static final String TAG = "SearchView";

    private Paint mPaint;
    // View 宽高
    private int mViewWidth;
    private int mViewHeight;

    private Path mCirclePath;
    private Path mSearchPath;
    private PathMeasure mPathMeasure;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    // 默认的动效周期 2s
    private static final int defaultDuration = 2000;
    private ValueAnimator mStartingAnimator;
    private ValueAnimator mSearchingAnimator;
    private ValueAnimator mEndingAnimator;

    // 当前的状态(非常重要)
    private State mCurrentState = State.NONE;
    // 动画数值(用于控制动画状态,因为同一时间内只允许有一种状态出现,具体数值处理取决于当前状态)
    private float mAnimatorValue = 0;
    // 判断是否已经搜索结束
    private boolean isOver = false;

    // 这个视图拥有的状态
    public enum State {
        NONE,
        STARTING,
        SEARCHING,
        ENDING
    }

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAll();
    }

    private void initAll() {
        initPaint();

        initPath();

        initListener();

        initHandler();

        initAnimator();
    }

    private void initAnimator() {
        mStartingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        mSearchingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        mEndingAnimator = ValueAnimator.ofFloat(1, 0).setDuration(defaultDuration);

        mStartingAnimator.addUpdateListener(mAnimatorUpdateListener);
        mSearchingAnimator.addUpdateListener(mAnimatorUpdateListener);
        mEndingAnimator.addUpdateListener(mAnimatorUpdateListener);

        mStartingAnimator.addListener(mAnimatorListener);
        mSearchingAnimator.addListener(mAnimatorListener);
        mEndingAnimator.addListener(mAnimatorListener);

        mSearchingAnimator.setInterpolator(new DecelerateInterpolator());
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initPath() {
        mCirclePath = new Path();
        mSearchPath = new Path();
        //PathMeasure测量路径的长度，和找到它的位置和切线，要调用setPath
        mPathMeasure = new PathMeasure();

        // 注意,不要到360度,否则内部会自动优化,测量不能取到需要的数值
        RectF rectF1 = new RectF(-50, -50, 50, 50);          //放大镜圆环
        mSearchPath.addArc(rectF1, 45, 359.9f);

        RectF rectF2 = new RectF(-100, -100, 100, 100);      //外部圆环
        mCirclePath.addArc(rectF2, 45, 359.9f);

        float[] pos = new float[2];

        mPathMeasure.setPath(mSearchPath, false);            // 放大镜把手的位置
        mPathMeasure.getPosTan(0, pos, null);

        mSearchPath.rLineTo(pos[0], pos[1]);                  //放大镜把手位置

        Log.e("TAG", "pos=" + pos[0] + ":" + pos[1]);
    }

    private void initListener() {
        mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //onAnimationEnd中有重新开始动画时不可靠，需要延迟postDelayed或者用handler
                mAnimatorHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSearch(canvas);
    }

    private void drawSearch(Canvas canvas) {
        //平移坐标原点到中心
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        //绘蓝色底
        canvas.drawColor(Color.parseColor("#0082D7"));

        switch (mCurrentState) {
            case NONE:
                canvas.drawPath(mSearchPath, mPaint);
                break;
            case STARTING:
                mPathMeasure.setPath(mSearchPath, false);
                Path dst = new Path();
                mPathMeasure.getSegment(mPathMeasure.getLength() * mAnimatorValue, mPathMeasure.getLength(), dst, true);
                canvas.drawPath(dst, mPaint);
                break;
            case SEARCHING:
                mPathMeasure.setPath(mCirclePath, false);
                Path dst2 = new Path();
                float stop = mPathMeasure.getLength() * mAnimatorValue;
                float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * mPathMeasure.getLength()));
                Log.e(TAG, "drawSearch: " + start);
                mPathMeasure.getSegment(start, stop, dst2, true);
                canvas.drawPath(dst2, mPaint);
                break;
            case ENDING:
                mPathMeasure.setPath(mSearchPath, false);
                Path dst3 = new Path();
                mPathMeasure.getSegment(mPathMeasure.getLength() * mAnimatorValue, mPathMeasure.getLength(), dst3, true);
                canvas.drawPath(dst3, mPaint);
                break;
        }
    }

    /**
     * 开始搜索
     */
    public void startSearch() {
        // 进入开始动画
        mCurrentState = State.STARTING;
        mStartingAnimator.start();
    }

    /**
     * 搜索完成
     */
    public void stopSearch() {
        isOver = true;
    }

    // 用于控制动画状态转换
    private Handler mAnimatorHandler;

    private void initHandler() {
        mAnimatorHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (mCurrentState) {
                    case STARTING:
                        // 从开始动画转换好搜索动画
                        isOver = false;
                        mCurrentState = State.SEARCHING;
                        mSearchingAnimator.start();
                        break;
                    case SEARCHING:
                        if (!isOver) {  // 如果搜索未结束 则继续执行搜索动画
                            mSearchingAnimator.start();
                            Log.e("Update", "RESTART");
                        } else {        // 如果搜索已经结束 则进入结束动画
                            mCurrentState = State.ENDING;
                            mEndingAnimator.start();
                        }
                        break;
                    case ENDING:
                        // 从结束动画转变为无状态
                        mCurrentState = State.NONE;
                        break;
                }
            }
        };
    }
}
