package wefit.com.wefit.utils.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gioacchino on 27/11/2017.
 */

public class CalendarFormatter {

    private static final Locale DATE_LOCALE = Locale.ENGLISH;
    private static final String MONTH_DATE_FORMAT = "MMM";

    public static String getMonthDay(long dateMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(dateMillis));
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = new SimpleDateFormat(MONTH_DATE_FORMAT, DATE_LOCALE).format(cal.getTime());
        return month.concat(" ").concat(day);
    }

    public static String getDate(long dateMillis) {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, DATE_LOCALE).format(new Date(dateMillis));
    }

    public static String getTime(long dateMillis) {
        return DateFormat.getTimeInstance(DateFormat.SHORT, DATE_LOCALE).format(new Date(dateMillis));
    }

    private CalendarFormatter() {
    }
}
