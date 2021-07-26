package com.gowtham.personal.services;

import com.gowtham.personal.model.Ticker;
import com.gowtham.personal.httpclient.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static com.gowtham.personal.constants.TickerConstants.SERVICE_URL;

@Service
public class TickerService {

    private  final HttpClient httpClient;

    @Autowired
    public TickerService(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Ticker getTickerStats(final String symbol, final Long startPeriod, final Long endPeriod) {
        try {
            String[] headers = {"accept", "application/json"};
            return this.httpClient.get(String.format(SERVICE_URL, symbol, startPeriod, endPeriod), headers, Ticker.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Ticker getTickerStats(final String symbol){
        try {
            final Date today = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.YEAR, -1);

            // Use the date formatter to produce a formatted date string
            final Date previousYearDate = calendar.getTime();

            String[] headers = {"accept", "application/json"};
            return this.httpClient.get(String.format(SERVICE_URL, symbol, previousYearDate.getTime()/1000, today.getTime()/1000),  headers, Ticker.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
