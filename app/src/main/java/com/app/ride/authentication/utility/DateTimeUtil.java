package com.app.ride.authentication.utility;

import android.content.Context;

import com.app.ride.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {
    //get current Utc timestamp
    public Long getCurrentUTCTimeStampForChat() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(Constant.DATE_FORMAT_yyyy_MM_dd_hh_mm_ss_a);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = df.format(c);
        Date value = new Date();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(Constant.DATE_FORMAT_yyyy_MM_dd_hh_mm_ss_a);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            value = formatter.parse(formattedDate);
        } catch (Exception e) {
        }
        return value.getTime();
    }

    //convert utc timestamp to local time
    public String convertUtcTimeToLocalTimeFormatForChat(Long timestamp) {
        try {
            Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal1.setTimeInMillis(timestamp);
            Date date = new Date(cal1.getTimeInMillis());
            SimpleDateFormat format = new SimpleDateFormat(Constant.DATE_FORMAT_hh_MM_aa);
            String formatted = format.format(date);
            format.setTimeZone(TimeZone.getDefault());
            Date dateNew = format.parse(formatted);
            formatted = format.format(dateNew);
            return formatted;
        } catch (Exception e) {
        }
        return "";

    }

    //convert utc time stamp to day/date format
    public String convertUTCTToLocalDate(Long timestamp, Context context, Boolean isTimeReturn) {
        String data = "";

        try {
            Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal1.setTimeInMillis(timestamp);
            Date date = new Date(cal1.getTimeInMillis());

            SimpleDateFormat format = new SimpleDateFormat(Constant.DATE_FORMAT_dd_MM_yyyy);
            String formatted = format.format(date);

            format.setTimeZone(TimeZone.getDefault());
            Date dateNew = format.parse(formatted);

            Calendar msgTime = Calendar.getInstance();
            msgTime.setTimeInMillis(dateNew.getTime());

            Calendar now = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.ENGLISH);
            SimpleDateFormat lastYearFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);

            Boolean sameDay = now.get(Calendar.DATE) == msgTime.get(Calendar.DATE);
            Boolean lastDay = now.get(Calendar.DAY_OF_YEAR) - msgTime.get(Calendar.DAY_OF_YEAR) == 1;
            Boolean sameYear = now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR);

            if (sameDay && sameYear) {
                if (isTimeReturn) {
                    SimpleDateFormat isTimeFormat = new SimpleDateFormat(Constant.DATE_FORMAT_hh_MM_aa);
                    String isTimeFormatted = isTimeFormat.format(date);
                    isTimeFormat.setTimeZone(TimeZone.getDefault());
                    Date timeDateNew = isTimeFormat.parse(isTimeFormatted);
                    isTimeFormatted = isTimeFormat.format(timeDateNew);
                    data =  isTimeFormatted;
                } else {
                    data =  context.getString(R.string.today);
                }
            } else if (lastDay && sameYear) {
                data = context.getString(R.string.yesterday);
            } else if (sameYear) {
                data = dateFormat.format(dateNew);
            } else {
                data = lastYearFormat.format(dateNew);
            }
        } catch (Exception e) {
        }

        return data;
    }

}
