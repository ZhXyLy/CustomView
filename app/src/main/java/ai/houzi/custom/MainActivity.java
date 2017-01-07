package ai.houzi.custom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import ai.houzi.custom.widget.calender.CalendarList;
import ai.houzi.custom.widget.calender.CalendarUtils;
import tyrantgit.explosionfield.ExplosionField;

import static ai.houzi.custom.R.id.calendarList;
import static ai.houzi.custom.R.id.end_date;
import static ai.houzi.custom.R.id.start_date;


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
        ArrayList<CalendarList.Cale> cales = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        //当前之前年
        for (int i = START_YEAR; i < year; i++) {
            for (int j = 0; j < 12; j++) {
                cales.add(new CalendarList.Cale(i, j));
            }
        }
        //到当前年当前月
        for (int j = 0; j < month + 1; j++) {
            cales.add(new CalendarList.Cale(year, j));
        }
        calendarList.setDatas(cales, true);
        calendarList.setOnCalendarClickListener(new CalendarList.OnCalendarClickListener() {
            @Override
            public void onCalendarClick(Calendar start, Calendar end) {
                setDateWeekText(start, end);
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
