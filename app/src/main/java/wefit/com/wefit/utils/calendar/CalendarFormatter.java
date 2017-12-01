package wefit.com.wefit.utils.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gioacchino on 27/11/2017.
 * This helper class formats Calendar instances
 */

public class CalendarFormatter {
    /**
     * Locale
     */
    private static final Locale DATE_LOCALE = Locale.ENGLISH;
    private static final String MONTH_DATE_FORMAT = "MMM";

    /**
     * It formats a Date in order to retrieve a String containing the month and the day
     * @param dateMillis the date to format
     */
    public static String getMonthDay(long dateMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(dateMillis));
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = new SimpleDateFormat(MONTH_DATE_FORMAT, DATE_LOCALE).format(cal.getTime());
        return month.concat(" ").concat(day);
    }
    /**
     * It formats a Date in order to retrieve a String containing the date in English format
     * @param dateMillis the date to format
     */
    public static String getDate(long dateMillis) {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, DATE_LOCALE).format(new Date(dateMillis));
    }
    /**
     * It formats a Date in order to retrieve a String containing the time expressed in hours and minutes
     * @param dateMillis the date to format
     */
    public static String getTime(long dateMillis) {
        return DateFormat.getTimeInstance(DateFormat.SHORT, DATE_LOCALE).format(new Date(dateMillis));
    }

    private CalendarFormatter() {
    }
}
