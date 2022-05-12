package com.kerrli.BanksystemJava_4sem.util;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public static String formatDate(Date date) {
        String string = "";
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            string = dateFormat.format(date);
        }
        return string;
    }

    public static Date addTime(Date date, String time) {
        Date dateTime = null;
        try {
            String dateString = formatDate(date) + " " + time;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            dateTime = formatter.parse(dateString);
        }
        catch (Exception e) {}
        return dateTime;
    }

    public static double roundSum(double sum) {
        return Math.round(sum * 100.0) / 100.0;
    }

    public static String formatSum(double sum) {
        return String.format("%.2f", sum);
    }

    public static void setAttribute(HttpSession httpSession, String name, Object value) {
        Map<String, Object> report = (Map<String, Object>) httpSession.getAttribute("report");
        if (report == null) {
            report = new HashMap<>();
            httpSession.setAttribute("report", report);
        }
        report.put(name, value);
    }

    public static void moveAttributeToModel(HttpSession httpSession, Model model) {
        Map<String, Object> report = (Map<String, Object>) httpSession.getAttribute("report");
        if (report == null) {
            return;
        }
        Set<String> keys = report.keySet();
        for (String key : keys) {
            model.addAttribute(key, report.get(key));
        }
        httpSession.removeAttribute("report");
    }
}
