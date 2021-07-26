package com.gowtham.personal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticker {
    public ArrayList<Double> close = new ArrayList<>();
    public ArrayList<Long> timestamp = new ArrayList<>();
    public ArrayList<Double> dailyReturn = new ArrayList<>();
    public Double avgDailyReturn;
    //dispersion will be used to calculate covariance;
    public ArrayList<Double> dispersion = new ArrayList<>();
    public Double variance;
    public Double std;
    public Double priceStd;
    public Map<Long, Pair> timeStampReturnPair = new LinkedHashMap<>();
    public Ticker(){
    }

    @JsonProperty("chart")
    private void unpackNameFromNestedObject(JsonNode chart) {
        AtomicInteger index = new AtomicInteger();
        JsonNode close = chart.get("result").get(0).get("indicators").get("adjclose").get(0).get("adjclose");
        JsonNode timestamp = chart.get("result").get(0).get("timestamp");
        close.iterator().forEachRemaining((x) -> {
            if (!x.isNull()) {
                this.close.add(x.asDouble());
                this.timestamp.add(timestamp.get(index.get()).asLong());
            }
            index.getAndIncrement();
        });
        Collections.reverse(this.close);
        Collections.reverse(this.timestamp);
    }


    public ArrayList<Double> getDailyReturn() {
//        ArrayList<Double> test = new ArrayList<>();
//        test.add(570.1);
//        test.add(580.5);
//        test.add(595.15);
//        test.add(580.75);
//        test.add(578.6);
//        test.add(577.95);
//        test.add(579.15);
        this.dailyReturn.clear();

        for (int i = 0; i < this.close.size(); i++) {
            /*Ignore daily return of the last element as the next element does not exist*/
            /*For the last index add zero as there is no next element to calculate*/
            Double dReturn = 0.0;
            if(i+1 != this.close.size()){
                Double current = this.close.get(i);
                Double oldValue = this.close.get(i + 1);
                dReturn = (current/oldValue - 1);
            }
            this.dailyReturn.add(dReturn);
        }
        return this.dailyReturn;
    }

    public Double getVariance(){
        return this.getVariance(this.dailyReturn);
    }

    public Double getPriceVariance(){
        return this.getVariance(this.close);
    }

    public Double getStd(){
        return Math.sqrt(this.getVariance());
    }

    public Double getPriceStd(){
        return Math.sqrt(this.getPriceVariance());
    }

    public ArrayList<Double> getDispersion() {
        return this.getDispersion(this.dailyReturn, this.avgDailyReturn);
    }

    public Double getAvgDailyReturn() {
        this.avgDailyReturn = this.getAverage(this.dailyReturn);
        return this.avgDailyReturn;
    }

    public ArrayList<Long> getTimestamp() {
        return timestamp;
    }

    public Map<Long, Pair> getTimeStampReturnPair() {

        if(this.dailyReturn.isEmpty())
            this.getDailyReturn();

        for (int i = 0; i < this.timestamp.size(); i++) {
            this.timeStampReturnPair.put(this.timestamp.get(i), new Pair<>(this.close.get(i), this.dailyReturn.get(i)));
        }
        return timeStampReturnPair;
    }

    public static <K, V> Map<K, V> listToMap(List<K> keys, List<V> values) {
        Iterator<K> keyIter = keys.iterator();
        Iterator<V> valIter = values.iterator();
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap( i -> keyIter.next(), i -> valIter.next()));
    }

    /*
                Variance = (dispersionSquared sum)/n
                dispersionSquared = dispersion^2
                dispersion: overallseries(X)% - AverageSeries(µ)%
                n: number of samples
            */
    private Double getVariance(ArrayList<Double> series){
        ArrayList<Double> dispersion = this.getDispersion(series, this.getAverage(series));
        Double dispersionSum = this.getDispersionSquared(dispersion).stream().mapToDouble(x -> x).sum();
        return dispersionSum / series.size();
    }


    private Double getAverage(final ArrayList<Double> series){
        return series.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    private ArrayList<Double> getDispersion(ArrayList<Double> series, Double avgSeries){
        ArrayList<Double> dispersionList = new ArrayList<>();
        for(Double item : series){
            Double dispersion = item - avgSeries;
            dispersionList.add(dispersion);
        }
        return dispersionList;
    }

    private ArrayList<Double> getDispersionSquared(ArrayList<Double> dispersionList){
        ArrayList<Double> dispersionSquaredList = new ArrayList<>();
        for(Double dispersion : dispersionList){
            dispersionSquaredList.add(Math.pow(dispersion, 2));
        }
        return dispersionSquaredList;
    }

    private Double getStd(final Double variance) {
        return Math.sqrt(variance);
    }

    @Override
    public String toString() {
        return "Ticker{" +
                "close=" + close +
                ", timestamp=" + timestamp +
                ", dailyReturn=" + dailyReturn +
                ", dispersion=" + dispersion +
                ", avgDailyReturn=" + avgDailyReturn +
                ", variance=" + variance +
                ", std=" + std +
                ", priceStd=" + priceStd +
                '}';
    }
}
