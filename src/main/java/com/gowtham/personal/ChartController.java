package com.gowtham.personal;

import com.gowtham.personal.model.Pair;
import com.gowtham.personal.model.StatsInput;
import com.gowtham.personal.model.Ticker;
import com.gowtham.personal.services.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Stream;

@Controller
public class ChartController {
    private final TickerService tickerService;

    @Autowired
    public ChartController(TickerService tickerService) {
        this.tickerService = tickerService;
    }

    @RequestMapping(value="/chart", method = RequestMethod.GET)
    @ResponseBody
    public Ticker GetTicker(@RequestParam String symbol) throws IOException, InterruptedException {
        return this.tickerService.getTickerStats(symbol);
    }

    @RequestMapping(value="/chart", method=RequestMethod.POST)
    @ResponseBody
    public Stream<Ticker> GetStats(@RequestBody StatsInput statsInput){
        Stream<Ticker> symbolStats =  statsInput.getSymbols().parallelStream().map((symbol) -> this.tickerService.getTickerStats(symbol.getName()));
        return symbolStats;
    }

    @RequestMapping(value="/equitycurve", method=RequestMethod.POST)
    @ResponseBody
    public Map<Long, ArrayList<Double>> getEquityCurve(@RequestBody StatsInput statsInput) {
        Map<Long, ArrayList<Double>> dailyPortfolioValue = new LinkedHashMap<>();
        final long startEpoch =  StatsInput.getEpoch(statsInput.getStartPeriod());
        final long endEpoch = StatsInput.getEpoch(statsInput.getEndPeriod());

        statsInput.getSymbols().stream().forEach((symbol) -> {
            Ticker ticker = this.tickerService.getTickerStats(symbol.getName(),startEpoch, endEpoch );
            ArrayList<Long> timestampList = ticker.getTimestamp();
            Map<Long, Pair> timestampAndClosePriceReturnPercentpair = ticker.getTimeStampReturnPair();
            Double dayValue = symbol.getWeightage(); //starts with weightage

            // start the iteration from second last element as last element return is already zero
            for (int currentIndex = timestampList.size() - 2; currentIndex >= 0; currentIndex--) {
                var timeStamp = timestampList.get(currentIndex);
                var closePriceReturnPercentPair = timestampAndClosePriceReturnPercentpair.get(timeStamp);
                /* Pair.first = closeprice; Pair.second = returnpercent */
                dayValue = dayValue + dayValue * (Double) closePriceReturnPercentPair.getSecond();


                var dailyPortfolioPrices = dailyPortfolioValue.get(timeStamp);

                /* data already in map */
                if( dailyPortfolioPrices != null){
                    dailyPortfolioPrices.add(dayValue);
                }
                else{
                    ArrayList<Double> dailyPricesOfAllSymbol = new ArrayList<>();
                    dailyPricesOfAllSymbol.add(dayValue);
                    dailyPortfolioValue.put(timeStamp,dailyPricesOfAllSymbol);
                }
            }
        });
        return dailyPortfolioValue;
    }

}
