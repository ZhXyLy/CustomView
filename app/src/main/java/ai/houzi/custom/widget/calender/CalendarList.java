package ai.houzi.custom.widget.calender;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CalendarList extends ListView {

    private int pageSize = 12;//每次增加的月份

    private int mSelectMode = 1;//默认单选
    private int mCurrentYear;

    @IntDef({SINGLE, RANGE, MULTIPLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SelectMode {
    }

    public static final int SINGLE = 1;//单选
    public static final int RANGE = 2;//范围
    public static final int MULTIPLE = 3;//多选

    /**
     * 设置选择模式
     *
     * @param selectMode One of {@link #SINGLE}, {@link #RANGE}, or {@link #MULTIPLE}.
     *                   {@link SelectMode}
     */
    public void setSelectMode(int selectMode) {
        this.mSelectMode = selectMode;
        calendarAdapter.setSelectMode(selectMode);
    }

    private List<Cale> datas;//传入的年月集合
    private CalendarAdapter calendarAdapter;//所有月份日历adapter

    public CalendarList(Context context) {
        this(context, null);
    }

    public CalendarList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        datas = new ArrayList<>();
        setOverScrollMode(OVER_SCROLL_NEVER);
        if (isInEditMode()) {
            Calendar calendar = Calendar.getInstance();
            for (int i = 0; i < 3; i++) {
                datas.add(new Cale(calendar.get(Calendar.YEAR), i));
            }
            calendarAdapter = new CalendarAdapter(datas);
            setAdapter(calendarAdapter);
            return;
        }
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.e(TAG, "onScroll: " + "=firstVisibleItem==" + firstVisibleItem + "=visibleItemCount=" + visibleItemCount + "=totalItemCount==" + totalItemCount);
                if (isCanAddNextYear && totalItemCount > 3 && firstVisibleItem + visibleItemCount == totalItemCount) {
                    isCanAddNextYear = false;
                    addNextYear();
                } else if (isCanAddLastYear && totalItemCount > 3 && firstVisibleItem == 0) {
                    isCanAddLastYear = false;
                    addLastYear();
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        mCurrentYear = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        mMaxYear = mCurrentYear + 50;//限制一下，最大不超过50年后

        setDatas(mCurrentYear, month);
    }

    /**
     * 第一次填充数据，初始化
     */
    private void setDatas(int year, int month) {
        //添加一年
        for (int i = 0; i < pageSize; i++) {
            datas.add(new Cale(year, i));
        }
        calendarAdapter = new CalendarAdapter(datas);
        setAdapter(calendarAdapter);
        setSelection(month);
    }

    private int next = 1;
    private int last = 1;
    private boolean isCanAddNextYear = true;
    private boolean isCanAddLastYear = true;

    /**
     * 添加下一年
     */
    private void addNextYear() {
        int year = mCurrentYear + next;
        if (mMaxYear > mCurrentYear && year > mMaxYear) {
            return;
        }
        next++;
        //添加一年
        for (int i = 0; i < pageSize; i++) {
            datas.add(new Cale(year, i));
        }
        calendarAdapter.notifyDataSetChanged();
        isCanAddNextYear = true;
    }

    /**
     * 添加上一年
     */
    private void addLastYear() {
        int year = mCurrentYear - last;
        if (year < mMinYear) {
            return;
        }
        last++;
        //添加一年
        for (int i = 0; i < pageSize; i++) {
            datas.add(i, new Cale(year, i));
        }
        calendarAdapter.notifyDataSetChanged();
        setSelection(12);
        isCanAddLastYear = true;
    }

    /**
     * @return 返回选中的日子集合（范围选择是首和尾，双闭区间[start,end]）
     */
    public List<Calendar> getDates() {
        return calendarAdapter.getDate();
    }

    /**
     * 清空选择的日子
     */
    public void clear() {
        calendarAdapter.clear();
    }

    public static class Cale {
        int year;
        int month;

        Cale(int year, int month) {
            this.year = year;
            this.month = month;
        }
    }

    private int mMinYear = 1970;

    /**
     * 设置最小年(不能小于1970年)
     */
    public void setMinYear(int minYear) {
        mMinYear = Math.max(minYear, mMinYear);
    }

    private int mMaxYear;

    /**
     * 设置最大年
     */
    public void setMaxYear(int maxYear) {
        mMaxYear = Math.min(maxYear, mMaxYear);
    }

    public interface OnCalendarClickListener {
        /**
         * 单选
         */
        void onSingleCalendar(Calendar calendar);

        /**
         * 范围选择
         */
        void onRangeCalendar(Calendar start, Calendar end);

        /**
         * 多选
         *
         * @param calendar 当前点击的日子
         * @param list     所有选中的日子
         */
        void onMultipleCalendar(Calendar calendar, List<Calendar> list);
    }

    public void setOnCalendarClickListener(OnCalendarClickListener listener) {
        this.onCalendarClickListener = listener;
        calendarAdapter.setOnCalendarClickListener(new CalendarAdapter.OnCalendarClickListener() {
            @Override
            public void onSingleCalendar(Calendar calendar) {
                if (mSelectMode == SINGLE && onCalendarClickListener != null) {
                    onCalendarClickListener.onSingleCalendar(calendar);
                }
            }

            @Override
            public void onRangeCalendar(Calendar start, Calendar end) {
                if (mSelectMode == RANGE && onCalendarClickListener != null) {
                    onCalendarClickListener.onRangeCalendar(start, end);
                }
            }

            @Override
            public void onMultipleCalendar(Calendar calendar, List<Calendar> list) {
                if (mSelectMode == MULTIPLE && onCalendarClickListener != null) {
                    onCalendarClickListener.onMultipleCalendar(calendar, list);
                }
            }
        });
    }

    private OnCalendarClickListener onCalendarClickListener;
}
