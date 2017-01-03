package ai.houzi.custom.widget.calender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;

public class CalendarView extends View {
    private static final String TAG = "CalendarView";
    private static final int WEEK_SIZE = 7;
    private int mWidth, mHeight;
    private int mPerWidth;
    private int mCurYear;
    private int mCurMonth;

    private TextPaint mTextPaint;
    private Paint mPaint;
    private boolean isPress;
    private boolean isSelect;
    private float[] pressPoint = new float[2];
    private float[] selectDay = new float[2];

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(getResources().getDisplayMetrics().density * 14 + 0.5f);

        Calendar calendar = Calendar.getInstance();
        mCurYear = calendar.get(Calendar.YEAR);
        mCurMonth = calendar.get(Calendar.MONTH);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int perHeight = sizeWidth / WEEK_SIZE;

        int sizeHeight = perHeight * CalendarUtils.getWeeksInMonth(mCurYear, mCurMonth);
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mPerWidth = mWidth / WEEK_SIZE;
    }

    SparseArray<RectF> rectFSparseArray = new SparseArray<>();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int daySize = CalendarUtils.getMonthDays(mCurYear, mCurMonth);
        int firstDayWeek = CalendarUtils.getFirstDayWeek(mCurYear, mCurMonth);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;

        for (int i = 0; i < daySize; i++) {
            int left = ((firstDayWeek + i) % WEEK_SIZE) * mPerWidth;
            int right = ((firstDayWeek + i) % WEEK_SIZE + 1) * mPerWidth;
            int top = ((i + firstDayWeek) / WEEK_SIZE) * mPerWidth;
            int bottom = ((i + firstDayWeek) / WEEK_SIZE + 1) * mPerWidth;
            RectF rectF = new RectF(left, top, right, bottom);
            rectFSparseArray.put(i, rectF);

            float mt = mTextPaint.measureText((i + 1) + "");
            if (isPress && !isSelect && rectF.contains(pressPoint[0], pressPoint[1])) {//只是按下没有抬起
                mPaint.setXfermode(null);
                mPaint.setAlpha(127);
                canvas.drawCircle(rectF.centerX(), rectF.centerY(), mPerWidth / 2 - 10, mPaint);
            } else if (isPress && isSelect) {//按下并抬起，视为点击选中
                if (selectDay[0] > 0 && selectDay[1] > 0) {
                    mPaint.setAlpha(255);
                    if (i == selectDay[0] - 1) {
                        RectF rectF1 = new RectF(rectF.centerX(), rectF.top, rectF.right, rectF.bottom);
                        canvas.drawRect(rectF1, mPaint);
                        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                        canvas.drawCircle(rectF.centerX(), rectF.centerY(), mPerWidth / 2 - 10, mPaint);
                    } else if (i == selectDay[1] - 1) {
                        RectF rectF1 = new RectF(rectF.left, rectF.top, rectF.centerX(), rectF.bottom);
                        canvas.drawRect(rectF1, mPaint);
                        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                        canvas.drawCircle(rectF.centerX(), rectF.centerY(), mPerWidth / 2 - 10, mPaint);
                    } else if (i > selectDay[0] - 1 && i < selectDay[1] - 1) {
                        mPaint.setXfermode(null);
                        canvas.drawRect(rectF, mPaint);
                    }
                }
            } else {//正常绘制
                mTextPaint.setColor(Color.BLACK);
            }
            mPaint.setXfermode(null);
            canvas.drawText(i + 1 + "", rectF.centerX() - mt / 2, rectF.centerY() + fontHeight / 3, mTextPaint);
        }
    }

    public void setYearMonth(int year, int month) {
        this.mCurYear = year;
        this.mCurMonth = month;
        requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.e(TAG, "onTouchEvent: " + event.getAction());
        isPress = false;
        isSelect = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPress = true;
                pressPoint[0] = event.getX();
                pressPoint[1] = event.getY();

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                isSelect = true;
                isPress = true;
                doClickDown();
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                isPress = false;
                isSelect = false;
                invalidate();
                break;
        }
        return true;
    }

    private void doClickDown() {
        performClick();
        int firstDayWeek = CalendarUtils.getFirstDayWeek(mCurYear, mCurMonth);
        int x = (int) (pressPoint[0] / mPerWidth);
        int y = (int) (pressPoint[1] / mPerWidth);
        int day = x + y * WEEK_SIZE - firstDayWeek + 1;
        Toast.makeText(getContext(), mCurYear + "年" + (mCurMonth + 1) + "月" + day + "日", Toast.LENGTH_SHORT).show();

        if (selectDay[0] != 0 && selectDay[1] == 0) {
            selectDay[1] = day;
            Arrays.sort(selectDay);
        } else {
            selectDay[0] = day;
        }
    }
}
