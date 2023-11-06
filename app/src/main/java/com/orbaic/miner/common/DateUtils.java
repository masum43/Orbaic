package com.orbaic.miner.common;

import java.util.Calendar;

public class DateUtils {
    public static String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Format the key as "yyyyMMdd"
        return String.format("%04d%02d%02d", year, month, day);
    }
}
