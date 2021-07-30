package com.damon.messenger.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {

    public  String getTimeAgo(long duration,Date date){
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date now = new Date();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime()-duration);
        long minutes =  TimeUnit.MILLISECONDS.toMinutes(now.getTime()-duration);
        long hours =  TimeUnit.MILLISECONDS.toHours(now.getTime()-duration);
        long days =  TimeUnit.MILLISECONDS.toDays(now.getTime()-duration);

        if (seconds < 60){
            return " justo ahora";
        }else if (minutes == 1){
            return " hoy";
        }else if (minutes > 1 && minutes < 60){
            return " hoy";
        }else if (hours ==1){
            return " hoy";
        }else if (hours > 1 && hours < 24){
            return  "hace " + hours+ " horas";
        }else if (days ==1){
            return  " ayer";
        }else {

            return simpleDateFormat.format(date);
//            return days + " dias atras";
        }
    }
}
