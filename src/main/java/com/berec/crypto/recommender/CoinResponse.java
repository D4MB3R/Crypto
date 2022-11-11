package com.berec.crypto.recommender;

/**
 * This class wraps together the name of a cryptocurrency and it's details.
 *
 * @author Adam Berec
 * */
public class CoinResponse {

    private final String name;
    private final Details details;

    public CoinResponse(String name, Details details) {
        this.name = name;
        this.details = details;
    }

    /**
     * @return name of the wrapped currency.
     * */
    public String getName() {
        return name;
    }

    /**
     * @return details of the wrapped currency.
     * */
    public Details getDetails() {
        return details;
    }
}
