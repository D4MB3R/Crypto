package com.berec.crypto.recommender;

/**
 * This class holds special attributes of a cryptocurrency.
 *
 * @author Adam Berec
 * */
public class Details {
    private final Double oldestValue;
    private final Double newestValue;
    private final Double minValue;
    private final Double maxValue;

    public Details(Double oldestValue, Double newestValue, Double minValue, Double maxValue) {
        this.oldestValue = oldestValue;
        this.newestValue = newestValue;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    /**
     * @return the oldest value of the currency.
     * */
    public Double getOldestValue() {
        return oldestValue;
    }

    /**
     * @return the newest value of the currency.
     * */
    public Double getNewestValue() {
        return newestValue;
    }

    /**
     * @return the minimum value of the currency.
     * */
    public Double getMinValue() {
        return minValue;
    }

    /**
     * @return the maximum value of the currency.
     * */
    public Double getMaxValue() {
        return maxValue;
    }
}
