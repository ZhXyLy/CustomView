package ai.houzi.custom.util;

import android.content.Context;


public class Unit {
    public static int dp2px(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}
