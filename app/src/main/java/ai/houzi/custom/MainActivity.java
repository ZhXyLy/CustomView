package ai.houzi.custom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import ai.houzi.custom.widget.calender.CalendarList;
import ai.houzi.custom.widget.calender.CalendarUtils;
import tyrantgit.explosionfield.ExplosionField;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int START_YEAR = 2014;//日历开始年
    TextView start_date;
    TextView end_date;
    private CalendarList calendarList;
    private ExplosionField explosionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_date = (TextView) findViewById(R.id.start_date);
        end_date = (TextView) findViewById(R.id.end_date);
        calendarList = (CalendarList) findViewById(R.id.calendarList);

        initCalendar();
        explosionField = ExplosionField.attach2Window(this);
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                explosionField.explode(start_date);
            }
        });
    }


    private void initCalendar() {
        calendarList.setSelectMode(CalendarList.RANGE);
        calendarList.setOnCalendarClickListener(new CalendarList.OnCalendarClickListener() {
            @Override
            public void onSingleCalendar(Calendar calendar) {
            // TODO: 2017/1/16 单选回调，其他时候不调
            setDateWeekText(calendar, null);
        }

            @Override
            public void onRangeCalendar(Calendar start, Calendar end) {
                // TODO: 2017/1/16 范围选择回调，其他时候不调
                setDateWeekText(start, end);
            }

            @Override
            public void onMultipleCalendar(Calendar calendar, List<Calendar> list) {
                // TODO: 2017/1/16 多选回调，其他时候不调 ，{list中包含calendar对象}
                setDateWeekText(calendar, null);
            }
        });
    }

    private void setDateWeekText(Calendar start, Calendar end) {
        if (start != null) {
            int month = start.get(Calendar.MONTH);
            int day = start.get(Calendar.DATE);
            int week = start.get(Calendar.DAY_OF_WEEK);
            start_date.setText((month + 1) + "月" + day + "日\n星期" + CalendarUtils.getWeekChinese(week));
        } else {
            start_date.setText("");
        }
        if (end != null) {
            int month = end.get(Calendar.MONTH);
            int day = end.get(Calendar.DATE);
            int week = end.get(Calendar.DAY_OF_WEEK);
            end_date.setText((month + 1) + "月" + day + "日\n星期" + CalendarUtils.getWeekChinese(week));
        } else {
            end_date.setText("");
        }
    }
}
