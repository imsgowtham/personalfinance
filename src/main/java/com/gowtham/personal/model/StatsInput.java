package com.gowtham.personal.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatsInput {
    public ArrayList<TickerInput> symbols;

    public String startPeriod;

    public String endPeriod;

    public String getStartPeriod() {
        return startPeriod;
    }

    public String getEndPeriod() {
        return endPeriod;
    }

    public static Long getEpoch(String strDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            Date date = df.parse(strDate);
            return date.getTime()/1000;
        }
        catch (ParseException exp){
            //log error
        }
        Date date = new Date();
        return date.getTime();
    }

    public static Long getEpoch(String strDate, String format){
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date date = df.parse(strDate);
            return date.getTime()/1000;
        }
        catch (ParseException exp){
            //log error
        }
        Date date = new Date();
        return date.getTime();
    }

    public ArrayList<TickerInput> getSymbols() {
        return symbols;
    }
}
