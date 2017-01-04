package ai.houzi.custom.widget.calender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ai.houzi.custom.util.Unit;

public class CalendarView extends View {
    private static final String TAG = "CalendarView";
    private static final int WEEK_SIZE = 7;//一周七天
    private int PADDING = 2;//行间距
    private int mWidth;
    private int mPerWidth;//每日的宽高
    private int mCurYear;//当前年
    private int mCurMonth;//当前月

    private TextPaint mTextPaint;
    private Paint mPaint;
    private float[] pressPoint = new float[2];//通过按下的位置计算day
    private List<Calendar> selectDate = new ArrayList<>();//存储选中的首末日期Calendar

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        PADDING = Unit.dp2px(context, 2);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(Unit.dp2px(context, 14));

        //当前日期
        Calendar calendar = Calendar.getInstance();
        mCurYear = calendar.get(Calendar.YEAR);
        mCurMonth = calendar.get(Calendar.MONTH);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int perHeight = sizeWidth / WEEK_SIZE;//计算每日的宽高（正方形）

        //按月计算当前有几周，算出总高度
        int sizeHeight = perHeight * CalendarUtils.getWeeksInMonth(mCurYear, mCurMonth);
        //由于计算时用的int，所以宽度占不满，设置宽度为7份和
        setMeasuredDimension(perHeight * WEEK_SIZE, sizeHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mPerWidth = mWidth / WEEK_SIZE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (selectDate.size() == 2) {//两头都选中，绘制一个选中范围
            drawScope(canvas);
        } else if (selectDate.size() == 1) {//只选中了一个点，绘制一个点
            drawAPoint(canvas);
        } else {//正常绘制
            drawCalendarNormal(canvas);
        }
    }

    private void drawCalendarNormal(Canvas canvas) {
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

            //默认背景，文字黑色
            mTextPaint.setColor(Color.BLACK);
            float mt = mTextPaint.measureText((i + 1) + "");
            canvas.drawText(i + 1 + "", rectF.centerX() - mt / 2, rectF.centerY() + fontHeight / 3, mTextPaint);
        }
    }

    private void drawAPoint(Canvas canvas) {
        Calendar start = selectDate.get(0);
        int startYear = start.get(Calendar.YEAR);
        int startMonth = start.get(Calendar.MONTH);
        int startDay = start.get(Calendar.DATE);
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

            if (startYear == mCurYear && startMonth == mCurMonth && startDay - 1 == i) {//选中的日子(年月日对应上)
                //文字白色，背景红色圆形
                mTextPaint.setColor(Color.WHITE);
                canvas.drawCircle(rectF.centerX(), rectF.centerY(), mPerWidth / 2 - PADDING, mPaint);
            } else {//正常日
                //默认背景，文字黑色
                mTextPaint.setColor(Color.BLACK);
            }

            float mt = mTextPaint.measureText((i + 1) + "");
            canvas.drawText(i + 1 + "", rectF.centerX() - mt / 2, rectF.centerY() + fontHeight / 3, mTextPaint);
        }
    }

    private void drawScope(Canvas canvas) {
        Calendar start = selectDate.get(0);
        Calendar end = selectDate.get(1);
        Calendar instance = Calendar.getInstance();

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

            instance.set(mCurYear, mCurMonth, i + 1);
            if (CalendarUtils.equals(start, instance)) {//开头日
                //文字白色，背景红色左半圆矩形
                mTextPaint.setColor(Color.WHITE);
                RectF rectF1 = new RectF(rectF.centerX(), rectF.top + PADDING, rectF.right, rectF.bottom - PADDING);
                canvas.drawRect(rectF1, mPaint);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                canvas.drawCircle(rectF.centerX(), rectF.centerY(), mPerWidth / 2 - PADDING, mPaint);
            } else if (CalendarUtils.equals(end, instance)) {//结尾日
                //文字白色，背景红色右半圆矩形
                mTextPaint.setColor(Color.WHITE);
                RectF rectF1 = new RectF(rectF.left, rectF.top + PADDING, rectF.centerX(), rectF.bottom - PADDING);
                canvas.drawRect(rectF1, mPaint);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                canvas.drawCircle(rectF.centerX(), rectF.centerY(), mPerWidth / 2 - PADDING, mPaint);
            } else if (CalendarUtils.inBetween(instance, start, end)) {//中间日
                //文字白色，背景红色矩形
                mTextPaint.setColor(Color.WHITE);
                mPaint.setXfermode(null);
                RectF rectF1 = new RectF(rectF.left, rectF.top + PADDING, rectF.right, rectF.bottom - PADDING);
                canvas.drawRect(rectF1, mPaint);
            } else {//正常日
                //默认背景，文字黑色
                mTextPaint.setColor(Color.BLACK);
            }

            float mt = mTextPaint.measureText((i + 1) + "");
            canvas.drawText(i + 1 + "", rectF.centerX() - mt / 2, rectF.centerY() + fontHeight / 3, mTextPaint);
        }

//      -------------------------------------月头月尾-----------------------------------------------
//        //跳月的话，头和尾绘制红色背景
//        //头
        instance.set(mCurYear, mCurMonth, 0);
        if (CalendarUtils.inBetween(instance, start, end)) {
            int left = 0;
            int right = firstDayWeek * mPerWidth;
            int top = PADDING;
            int bottom = mPerWidth - PADDING;
            RectF rectF = new RectF(left, top, right, bottom);
            canvas.drawRect(rectF, mPaint);
        }
//        //尾
        instance.set(mCurYear, mCurMonth, CalendarUtils.getMonthDays(mCurYear, mCurMonth));
        int week = instance.get(Calendar.WEEK_OF_MONTH);
        int lastDayWeek = CalendarUtils.getLastDayWeek(mCurYear, mCurMonth);
        if (CalendarUtils.inBetween(instance, start, end)) {
            int left = (lastDayWeek + 1) * mPerWidth;
            int right = mWidth;
            int top = (week - 1) * mPerWidth + PADDING;
            int bottom = (week) * mPerWidth - PADDING;
            RectF rectF = new RectF(left, top, right, bottom);
            canvas.drawRect(rectF, mPaint);
        }
//      -----------------------------------------------------------------------------------
    }

    public void setYearMonth(int year, int month) {
        this.mCurYear = year;
        this.mCurMonth = month;
        requestLayout();
    }

    public void setSelectScope(Calendar startDate, Calendar endDate) {
        selectDate.clear();
        if (startDate != null) {
            selectDate.add(0, startDate);
        }
        if (endDate != null) {
            selectDate.add(1, endDate);
        }
        requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressPoint[0] = event.getX();
                pressPoint[1] = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                doClickDown();
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private void doClickDown() {
//        performClick();
        int firstDayWeek = CalendarUtils.getFirstDayWeek(mCurYear, mCurMonth);
        int weeksInMonth = CalendarUtils.getWeeksInMonth(mCurYear, mCurMonth);
        int lastDayWeek = CalendarUtils.getLastDayWeek(mCurYear, mCurMonth);
        int x = (int) (pressPoint[0] / mPerWidth);
        int y = (int) (pressPoint[1] / mPerWidth);
        int day = x + y * WEEK_SIZE - firstDayWeek + 1;
        //算点击的三种情况：1.在中间行，2.第一行，在第一日之后，3.最后一周的，小于最后一日
        if ((y > 0 && y < weeksInMonth - 1)
                || (y == 0 && x > firstDayWeek - 1)
                || (y == weeksInMonth - 1 && x < lastDayWeek + 1)) {

            if (onCalendarClickListener != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(mCurYear, mCurMonth, day);
                onCalendarClickListener.onCalendarClick(calendar);
            }
        }
    }

    //日历点击后回调，在列表中处理
    interface OnCalendarClickListener {
        void onCalendarClick(Calendar calendar);

    }

    public void setOnCalendarClickListener(OnCalendarClickListener listener) {
        this.onCalendarClickListener = listener;
    }

    private OnCalendarClickListener onCalendarClickListener;

}
