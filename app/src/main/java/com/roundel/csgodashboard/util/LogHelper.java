package com.roundel.csgodashboard.util;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Krzysiek on 2017-02-03.
 */
public class LogHelper
{
    private static final String TAG = LogHelper.class.getSimpleName();
    private static final int TYPE_DEBUG = 0;
    private static final int TYPE_ERROR = 1;
    private static final int TYPE_INFO = 2;
    private static List<Log> logs = new ArrayList<>();

    private static DateFormat logFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());

    private static LogListener listener;

    private static boolean logToLogcat = true;

    private static void log(Date date, int type, String tag, String message)
    {
        Log log = new Log(date, type, tag, message);
        if(listener != null)
            listener.onLogAdded(log);
        logs.add(log);
    }

    private static void log(int type, String tag, String message)
    {
        log(new Date(), type, tag, message);
    }

    public static void d(String tag, String message)
    {
        if(logToLogcat)
            android.util.Log.d(tag, message);
        log(TYPE_DEBUG, tag, message);
    }

    public static void e(String tag, String message)
    {
        if(logToLogcat)
            android.util.Log.e(tag, message);
        log(TYPE_ERROR, tag, message);
    }

    public static void i(String tag, String message)
    {
        if(logToLogcat)
            android.util.Log.i(tag, message);
        log(TYPE_INFO, tag, message);
    }

    public static void setLogToLogcat(boolean logToLogcat)
    {
        LogHelper.logToLogcat = logToLogcat;
    }

    private static String getLetter(int type)
    {
        switch(type)
        {
            case TYPE_DEBUG:
                return "D";
            case TYPE_ERROR:
                return "E";
            case TYPE_INFO:
                return "I";
            default:
                return "V";
        }
    }

    public static String getLogs()
    {
        StringBuilder builder = new StringBuilder();

        for(Log log : logs)
        {
            String line = String.format(
                    "%s %s/%s: %s",
                    logFormat.format(log.date),
                    getLetter(log.type),
                    log.tag,
                    log.content
            );
            builder.append(line + "\n");
        }
        return builder.toString();
    }

    public static void setLogListener(LogListener listener)
    {
        LogHelper.listener = listener;
    }

    public interface LogListener
    {
        void onLogAdded(Log log);
    }

    public static class Log
    {
        protected Date date;
        protected int type;
        protected String tag;
        protected String content;

        public Log(Date date, int type, String tag, String content)
        {
            this.date = date;
            this.type = type;
            this.tag = tag;
            this.content = content;
        }
    }
}
