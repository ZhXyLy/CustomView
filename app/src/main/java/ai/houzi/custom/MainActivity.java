package ai.houzi.custom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import ai.houzi.custom.widget.calender.CalendarList;
import ai.houzi.custom.widget.calender.CalendarUtils;

import static ai.houzi.custom.R.id.calendarList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//         搜索
//        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchView.startSearch();
//                searchView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        searchView.stopSearch();
//                    }
//                }, 6000);
//            }
//        });
//
//        final RoundedImageView view = (RoundedImageView) findViewById(R.id.imageView);
//        view.getDrawable();
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (view.isFront()) {
//                    view.switchBack();
//                } else {
//                    view.switchFront();
//                }
//            }
//        });

        final TextView start_date = (TextView) findViewById(R.id.start_date);
        final TextView end_date = (TextView) findViewById(R.id.end_date);
        CalendarList calendarList = (CalendarList) findViewById(R.id.calendarList);

        ArrayList<CalendarList.Cale> cales = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 12; j++) {
                cales.add(new CalendarList.Cale(2015 + i, j));
            }
        }
        calendarList.setDatas(cales, true);
        calendarList.setOnCalendarClickListener(new CalendarList.OnCalendarClickListener() {
            @Override
            public void onCalendarClick(Calendar start, Calendar end) {
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
        });
    }
}
