package abs.sf.beach.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DateUtils {
    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        System.out.println( sdf.format(cal.getTime()));

        return sdf.format(cal.getTime());

    }


}
