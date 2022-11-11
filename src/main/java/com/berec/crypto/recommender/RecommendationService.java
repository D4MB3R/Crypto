package com.berec.crypto.recommender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Service for recommendation of cryptocurrencies.
 *
 * @author Adam Berec
 * */
@Service
public class RecommendationService {
    private static List<Coin> cryptos;

    @Autowired
    DataProvider dataProvider;

    /**
     * This method returns details about the selected cryptocurrency.
     *
     * @param crypto the name of the selected cryptocurrency.
     *
     * @return a {@link CoinResponse} object containing the oldest, newest, maximum, minimum price of the selected
     * currency.
     * */
    public CoinResponse getOldestNewestMinMaxValues(String crypto) {
        ensureCryptosExist();
        Coin selectedCoin = cryptos.stream()
            .filter(coin -> coin.getName().equalsIgnoreCase(crypto)).findFirst().orElse(null);
        if (selectedCoin == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "This crypto is not supported yet.");
        List<Double> sortedPrices = sortPrices(selectedCoin);
        return new CoinResponse(selectedCoin.getName(), new Details(findOldestPrice(selectedCoin),
            findNewestPrice(selectedCoin), findMinPrice(sortedPrices), findMaxPrice(sortedPrices)));
    }

    /**
     * This method returns the provided currencies in descending order, ordered by their normalized range.
     *
     * @return a list of {@link CoinResponse} objects which contains the currencies and their details.
     * */
    public List<CoinResponse> getCoinsByRangeDescending() {
        ensureCryptosExist();
        return orderCoinsByRangeDescending(cryptos);
    }

    /**
     * This method returns the currency that has the maximum normalized range for the given day.
     *
     * @param date the date of the day in yyyy-MM-dd format.
     *
     * @return a {@link CoinResponse} object containing the details of the found currency.
     * */
    public CoinResponse getCoinWithHighestRangeForGivenDay(String date) {
        ensureCryptosExist();
        return findCoinWithHighestRangeForGivenDay(cryptos, date);
    }

    /**
     * This method makes sure that our list of cryptocurrencies exists and is not empty.
     * */
    private void ensureCryptosExist() {
        if (cryptos == null || cryptos.isEmpty())
        {
            cryptos = dataProvider.createListOfCryptos();
        }
    }

    /**
     * This method returns the oldest known price of the given cryptocurrency.
     *
     * @param coin the selected cryptocurrency.
     *
     * @return a {@link Double} object containing the oldest price.
     * */
    private Double findOldestPrice(Coin coin) {
        return coin.getDailyPrices().firstEntry().getValue();
    }

    /**
     * This method returns the most recent price of the given cryptocurrency.
     *
     * @param coin the selected cryptocurrency.
     *
     * @return a {@link Double} object containing the oldest price.
     * */
    private Double findNewestPrice(Coin coin) {
        return coin.getDailyPrices().lastEntry().getValue();
    }

    /**
     * This method sorts the prices of the given cryptocurrency.
     *
     * @param coin the selected cryptocurrency.
     *
     * @return a list of {@link Double} objects containing the prices in ascending order.
     * */
    private List<Double> sortPrices(Coin coin) {
        return coin.getDailyPrices().values().stream().sorted().collect(Collectors.toList());
    }

    /**
     * This method returns the lowest price of the given cryptocurrency.
     *
     * @param sortedPrices the prices of the cryptocurrency in ascending order.
     *
     * @return a {@link Double} object with the value of the lowest price.
     * */
    private Double findMinPrice(List<Double> sortedPrices) {
        return sortedPrices.get(0);
    }

    /**
     * This method returns the highest price of the given cryptocurrency.
     *
     * @param sortedPrices the prices of the cryptocurrency in ascending order.
     *
     * @return a {@link Double} object with the value of the highest price.
     * */
    private Double findMaxPrice(List<Double> sortedPrices) {
        return sortedPrices.get(sortedPrices.size() - 1);
    }

    /**
     * This method calculates the normalized range from the given list of prices.
     *
     * @param sortedPrices the prices of the cryptocurrency in ascending order.
     *
     * @return a {@link Double} object with the value of the normalized range.
     * */
    private Double getNormalizedRange(List<Double> sortedPrices) {
        Double min = findMinPrice(sortedPrices);
        Double max = findMaxPrice(sortedPrices);
        return (max - min) / min;
    }

    /**
     * This method calculates the normalized range for a given day.
     *
     * @param coin the selected currency.
     * @param date the date of the day in yyyy-MM-dd format.
     *
     * @return a {@link Double} object with the provided currency's normalized range for the given day.
     * */
    private Double getNormalizedRangeForGivenDay(Coin coin, String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Double> pricesOfGivenDate = coin.getDailyPrices().entrySet().stream().filter(t -> {
            try
            {
                Timestamp time = t.getKey();
                Date start = dateFormat.parse(date.concat(" 00:00:00"));
                Date end = dateFormat.parse(date.concat(" 23:59:59"));
                return (time.after(start) || time.equals(new Timestamp(start.getTime()))) &&
                    (time.before(end) || time.equals(new Timestamp(end.getTime())));
            } catch (ParseException e)
            {
                throw new RuntimeException(e);
            }
        }).map(Map.Entry::getValue).sorted().collect(Collectors.toList());
        return getNormalizedRange(pricesOfGivenDate);
    }

    /**
     * This method returns the provided currencies in descending order, ordered by their normalized range.
     *
     * @param coins the selected currencies.
     *
     * @return a list of {@link CoinResponse} objects which contains the currencies and their details.
     * */
    private List<CoinResponse> orderCoinsByRangeDescending(List<Coin> coins) {
        TreeMap<Double, CoinResponse> normRangeAndCoinName = new TreeMap<>(Collections.reverseOrder());
        for (Coin coin : coins)
        {
            normRangeAndCoinName.put(getNormalizedRange(sortPrices(coin)), getOldestNewestMinMaxValues(coin.getName()));
        }
        return new ArrayList<>(normRangeAndCoinName.values());
    }

    /**
     * This method returns the currency that has the maximum normalized range for the given day.
     *
     * @param coins the list of selected currencies.
     * @param date the date of the day in yyyy-MM-dd format.
     *
     * @return a {@link CoinResponse} object containing the details of the found currency.
     * */
    private CoinResponse findCoinWithHighestRangeForGivenDay(List<Coin> coins, String date) {
        TreeMap<Double, CoinResponse> normRangeAndCoinName = new TreeMap<>();
        for (Coin coin : coins)
        {
            normRangeAndCoinName.put(getNormalizedRangeForGivenDay(coin, date),
                getOldestNewestMinMaxValues(coin.getName()));
        }
        return normRangeAndCoinName.lastEntry().getValue();
    }
}
