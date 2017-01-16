package ai.houzi.custom.widget.calender;

import java.util.Calendar;

public class CalendarUtils {
    /**
     * 是否在开始和结束之间，不包括
     *
     * @param cal      当前
     * @param calStart 开始
     * @param calEnd   结束
     * @return 之间返回true
     */
    public static boolean inBetween(Calendar cal, Calendar calStart, Calendar calEnd) {

        return calStart.compareTo(cal) < 0 && calEnd.compareTo(cal) > 0;
    }

    /**
     * 是否同一天
     *
     * @param cal1
     * @param cal2
     * @return
     */
    public static boolean equals(Calendar cal1, Calendar cal2) {
        int year1 = cal1.get(Calendar.YEAR);
        int dayOfYear1 = cal1.get(Calendar.DAY_OF_YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        int dayOfYear2 = cal2.get(Calendar.DAY_OF_YEAR);
        return year1 == year2 && dayOfYear1 == dayOfYear2;
    }

    /**
     * 比较大小
     *
     * @param cal1
     * @param cal2
     * @return 0：相等，即同一天，-1：cal1比cal2小，1：cal1比cal2大
     */
    public static int compareTo(Calendar cal1, Calendar cal2) {
        if (equals(cal1, cal2)) {
            return 0;
        } else {
            return cal1.compareTo(cal2);
        }
    }

    /**
     * 返回周对应的中文
     *
     * @param week
     */
    public static String getWeekChinese(int week) {
        String s = "日";
        switch (week) {
            case 1:
                s = "日";
                break;
            case 2:
                s = "一";
                break;
            case 3:
                s = "二";
                break;
            case 4:
                s = "三";
                break;
            case 5:
                s = "四";
                break;
            case 6:
                s = "五";
                break;
            case 7:
                s = "六";
                break;
        }
        return s;
    }

    public static int getWeeksInMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, getMonthDays(year, month));
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 获取本月1号对应周几
     *
     * @param year  年份
     * @param month 月份，传入系统获取的，不需要正常的
     * @return 日：0  	一：1	二：2	三：3	四：4	五：5	六：6
     */
    public static int getFirstDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int getLastDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, getMonthDays(year, month));
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 通过年份和月份 得到当月的天数
     */
    public static int getMonthDays(int year, int month) {
        month++;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }
}
