package ai.houzi.custom.widget.calender;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ai.houzi.custom.R;

public class CalendarList extends ListView {
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

        calendarAdapter = new CalendarAdapter(datas);
        calendarAdapter.setOnCalendarClickListener(new CalendarAdapter.OnCalendarClickListener() {
            @Override
            public void onCalendarClick(Calendar start, Calendar end) {
                //点击选中后，回调传回选中的首尾日历
                if (onCalendarClickListener != null) {
                    onCalendarClickListener.onCalendarClick(start, end);
                }
            }
        });
    }

    /**
     * 填充数据
     *
     * @param datas    填充的数据
     * @param isBottom 是否滚动到最底部
     */
    public void setDatas(List<Cale> datas, boolean isBottom) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        this.datas.clear();
        this.datas.addAll(datas);
        setAdapter(calendarAdapter);
        if (isBottom) {
            setSelection(calendarAdapter.getCount() - 1);
        }
    }

    public List<Calendar> getDates() {
        return calendarAdapter.getDate();
    }

    public void clear() {
        calendarAdapter.clear();
    }

    public void updateDates(List<Calendar> calendars) {
        calendarAdapter.updateDates(calendars);
    }

    public static class Cale {
        int year;
        int month;

        public Cale(int year, int month) {
            this.year = year;
            this.month = month;
        }
    }

    private static class CalendarAdapter extends BaseAdapter {
        private List<Cale> datas;
        private List<Calendar> list = new ArrayList<>();

        CalendarAdapter(List<Cale> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);
                holder = new ViewHolder();
                holder.tvYearMonth = (TextView) convertView.findViewById(R.id.tv_year_month);
                holder.calendarView = (CalendarView) convertView.findViewById(R.id.calendarView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Cale cale = (Cale) getItem(position);
            //title
            holder.tvYearMonth.setText(cale.year + "年" + (cale.month + 1) + "月");
            //设置当前日历的年月
            holder.calendarView.setYearMonth(cale.year, cale.month);
            //点击选中后的点（Calendar）集合（最多两个），通知CalendarView重绘
            if (list.size() == 2) {
                holder.calendarView.setSelectScope(list.get(0), list.get(1));
            } else if (list.size() == 1) {
                holder.calendarView.setSelectScope(list.get(0), null);
            } else {
                holder.calendarView.setSelectScope(null, null);
            }
            //所有的点击处理
            holder.calendarView.setOnCalendarClickListener(new CalendarView.OnCalendarClickListener() {
                @Override
                public void onCalendarClick(Calendar calendar) {
                    if (list.size() == 2) {//存了两个值说明是点第三下，要清空前边两个点，重置为第一个点
                        list.clear();
                        list.add(0, calendar);
                    } else if (list.size() == 1) {//存了一个点说明是点第二下，排序存入此范围
                        Calendar old = list.get(0);
                        //比较old小于calendar，返回小于0，反之。
                        //返回0，相等
                        int compareTo = old.compareTo(calendar);

                        if (compareTo < 0) {
                            list.add(1, calendar);
                        } else if (compareTo > 0) {
                            list.clear();
                            list.add(0, calendar);
                            list.add(1, old);
                        }//相等时说明点的是同一个点，不变
                    } else {//未存入点，存入此点作为第一个点
                        list.clear();
                        list.add(0, calendar);
                    }

                    notifyDataSetChanged();//重置后刷新一下页面
                    if (onCalendarClickListener != null) {//点击后list里至少有一个点，第二个可能有可能没有
                        onCalendarClickListener.onCalendarClick(list.get(0), list.size() == 2 ? list.get(1) : null);
                    }
                }
            });
            return convertView;
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }

        public List<Calendar> getDate() {
            ArrayList<Calendar> calendars = new ArrayList<>();
            calendars.addAll(list);
            return calendars;
        }

        public void updateDates(List<Calendar> calendars) {
            if (calendars == null) {
                return;
            }
            list.clear();
            list.addAll(calendars);
            notifyDataSetChanged();
        }

        class ViewHolder {
            TextView tvYearMonth;
            CalendarView calendarView;
        }

        interface OnCalendarClickListener {
            void onCalendarClick(Calendar start, Calendar end);
        }

        void setOnCalendarClickListener(OnCalendarClickListener listener) {
            this.onCalendarClickListener = listener;
        }

        private OnCalendarClickListener onCalendarClickListener;
    }

    public interface OnCalendarClickListener {
        void onCalendarClick(Calendar start, Calendar end);
    }

    public void setOnCalendarClickListener(OnCalendarClickListener listener) {
        this.onCalendarClickListener = listener;
    }

    private OnCalendarClickListener onCalendarClickListener;
}
