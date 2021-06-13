package com.rora.phase.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {

    public static String format(String data) {
        //SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);
        SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyyy");
        String result = "--";
        try {
            if (data == null)
                return result;
            Date datetime = format.parse(data);
            result = newFormat.format(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        //String dateTime = "N/A";
        //try {
        //    dateTime = dateFormat.parse(data).toString();
        //} catch (ParseException e) {
        //    e.printStackTrace();
        //}

        return result;
    }

    public static String formatIncludeTime(String data) {
        //SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm - MMM dd, yyyy");
        String result = "--";
        try {
            if (data == null)
                return result;
            Date datetime = format.parse(data);
            result = newFormat.format(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        //String dateTime = "N/A";
        //try {
        //    dateTime = dateFormat.parse(data).toString();
        //} catch (ParseException e) {
        //    e.printStackTrace();
        //}

        return result;
    }

}
