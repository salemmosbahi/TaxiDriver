package it.mahd.taxidriver.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by salem on 2/16/16.
 */
public class Calculator {
    private static DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
    private static DateFormat timeFormat = new SimpleDateFormat("K:mma");

    public static Calendar getCurrentTime() {
        /*Date today = Calendar.getInstance().getTime();
        return timeFormat.format(today);*/
        return Calendar.getInstance();
    }

    public static Date getCurrentDate() {
        /*Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);*/
        return Calendar.getInstance().getTime();
    }

    public long[] getDifference2Dates(Date startDate, Date endDate){
        long different = endDate.getTime() - startDate.getTime();
        long diffReturn = different;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        long[] tab = new long[5];
        tab[0] = diffReturn;
        tab[1] = elapsedDays;
        tab[2] = elapsedHours;
        tab[3] = elapsedMinutes;
        tab[4] = elapsedSeconds;
        return tab;
    }

    public int[] getAge(String date) {
        String[] strTemp = date.split("-");
        int birthdayYear = Integer.parseInt(strTemp[0].toString());
        int birthdayMonth = Integer.parseInt(strTemp[1].toString());
        int birthdayDay = Integer.parseInt(strTemp[2].toString());
        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH) + 1;
        int currentDay = today.get(Calendar.DAY_OF_MONTH);
        int year, month, day;
        year = currentYear - birthdayYear;
        month = currentMonth - birthdayMonth;
        day = currentDay - birthdayDay;
        if (month < 0) {
            year--;
            month = 12 - birthdayMonth + currentMonth;
            if (day < 0) month--;
        } else if (month == 0 && day < 0) {
            year--;
            month = 11;
        }
        if (day < 0) {
            today.add(Calendar.MONTH, -1);
            day = today.getActualMaximum(Calendar.DAY_OF_MONTH) - birthdayDay + currentDay;
        } else {
            day = 0;
            if (month == 12) {
                year++;
                month = 0;
            }
        }

        int[] tab = new int[3];
        tab[0] = year;
        tab[1] = month;
        tab[2] = day;
        return tab;
    }
}
