package com.berec.crypto.recommender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for recommending cryptocurrencies.
 *
 * @author Adam Berec
 * */
@RestController
public class RecommendationController
{
    @Autowired
    RecommendationService recommendationService;

    /**
     * This method returns the available currencies in descending order, ordered by their normalized range.
     *
     * @return a list of {@link CoinResponse} objects which contains the currencies and their details.
     * */
    @GetMapping("/cryptos-desc")
    public List<CoinResponse> cryptosDescending() {
        return recommendationService.getCoinsByRangeDescending();
    }

    /**
     * This method returns details about the selected cryptocurrency.
     *
     * @param name the name of the selected cryptocurrency.
     *
     * @return a {@link CoinResponse} object containing the oldest, newest, maximum, minimum price of the selected
     * currency.
     * */
    @GetMapping("/cryptos/{name}")
    public CoinResponse getDetailsOfCrypto(@PathVariable(value = "name") String name) {
        return recommendationService.getOldestNewestMinMaxValues(name);
    }

    /**
     * This method returns the currency that has the maximum normalized range for the given day.
     *
     * @param date the date of the day in yyyy-MM-dd format.
     *
     * @return a {@link CoinResponse} object containing the details of the found currency.
     * */
    @GetMapping("/cryptos/day")
    public CoinResponse getCryptoWithHighestRangeOnGivenDay(@RequestParam String date) {
        return recommendationService.getCoinWithHighestRangeForGivenDay(date);
    }
}
