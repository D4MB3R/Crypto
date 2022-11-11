package com.berec.crypto.recommender;

import java.sql.Timestamp;
import java.util.TreeMap;

/**
 * This class represents a cryptocurrency.
 *
 * @author Adam Berec
 * */
public class Coin
{
    private final String name;
    private final TreeMap<Timestamp, Double> dailyPrices;

    public Coin(String name, TreeMap<Timestamp, Double> dailyPrices)
    {
        this.name = name;
        this.dailyPrices = dailyPrices;
    }

    /**
     * @return the name of this currency.
     * */
    public String getName()
    {
        return name;
    }

    /**
     * @return the different dates and corresponding prices of this currency in a {@link TreeMap}.
     * */
    public TreeMap<Timestamp, Double> getDailyPrices()
    {
        return dailyPrices;
    }

    /**
     * @param time a date.
     * @param price the price on the given date.
     * */
    public void addDailyPrice(Timestamp time, Double price)
    {
        this.dailyPrices.put(time, price);
    }
}
