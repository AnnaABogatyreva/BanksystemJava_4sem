package com.kerrli.BanksystemJava_4sem.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Lib {
    public static Date parseDate(String string) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            date = formatter.parse(string);
        }
        catch (ParseException e) {}
        return date;
    }
}