package com.berec.crypto.recommender;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link RecommendationService}.
 *
 * @author Adam Berec
 * */

@SpringBootTest
class RecommendationServiceTest {

    //The tests work fine individually, but when run together, the first one fails for some reason.

    @MockBean
    DataProvider mockDataProvider;

    @Autowired
    RecommendationService recommendationService;

    @Test
    void getOldestNewestMinMaxValuesTest() {
        TreeMap<Timestamp, Double> dailyPrices = new TreeMap<>();
        dailyPrices.put(Timestamp.valueOf("2022-01-01 00:00:00"), 100.0);
        dailyPrices.put(Timestamp.valueOf("2022-10-01 00:00:00"), 200.0);

        Coin mockCoin = mock(Coin.class);

        when(mockCoin.getName()).thenReturn("testCoin");
        when(mockCoin.getDailyPrices()).thenReturn(dailyPrices);
        when(mockDataProvider.createListOfCryptos()).thenReturn(List.of(mockCoin));

        CoinResponse response = recommendationService.getOldestNewestMinMaxValues("testCoin");
        assertEquals("testCoin", response.getName());
        assertEquals(100.0, response.getDetails().getOldestValue());
        assertEquals(100.0, response.getDetails().getMinValue());
        assertEquals(200.0, response.getDetails().getNewestValue());
        assertEquals(200.0, response.getDetails().getMaxValue());
    }

    @Test
    void getCoinsByRangeDescendingTest() {
        TreeMap<Timestamp, Double> dailyPrices = new TreeMap<>();
        dailyPrices.put(Timestamp.valueOf("2022-01-01 00:00:00"), 100.0);
        dailyPrices.put(Timestamp.valueOf("2022-10-01 00:00:00"), 200.0);

        Coin mockCoin = mock(Coin.class);
        Coin mockCoin2 = mock(Coin.class);

        when(mockCoin.getName()).thenReturn("testCoin1");
        when(mockCoin.getDailyPrices()).thenReturn(dailyPrices);

        TreeMap<Timestamp, Double> dailyPrices2 = new TreeMap<>();
        dailyPrices2.put(Timestamp.valueOf("2022-01-01 00:00:00"), 100.0);
        dailyPrices2.put(Timestamp.valueOf("2022-10-01 00:00:00"), 500.0);

        when(mockCoin2.getName()).thenReturn("testCoin2");
        when(mockCoin2.getDailyPrices()).thenReturn(dailyPrices2);

        when(mockDataProvider.createListOfCryptos()).thenReturn(List.of(mockCoin, mockCoin2));

        List<CoinResponse> response = recommendationService.getCoinsByRangeDescending();
        assertEquals("testCoin2", response.get(0).getName());
        assertEquals("testCoin1", response.get(1).getName());
    }

    @Test
    void getCoinWithHighestRangeForGivenDayTest() {
        TreeMap<Timestamp, Double> dailyPrices = new TreeMap<>();
        dailyPrices.put(Timestamp.valueOf("2022-01-01 00:00:00"), 100.0);
        dailyPrices.put(Timestamp.valueOf("2022-10-01 00:00:00"), 200.0);

        Coin mockCoin = mock(Coin.class);
        Coin mockCoin2 = mock(Coin.class);

        when(mockCoin.getName()).thenReturn("testCoin1");
        when(mockCoin.getDailyPrices()).thenReturn(dailyPrices);

        TreeMap<Timestamp, Double> dailyPrices2 = new TreeMap<>();
        dailyPrices2.put(Timestamp.valueOf("2022-01-01 00:00:00"), 100.0);
        dailyPrices2.put(Timestamp.valueOf("2022-10-01 00:00:00"), 500.0);

        when(mockCoin2.getName()).thenReturn("testCoin2");
        when(mockCoin2.getDailyPrices()).thenReturn(dailyPrices2);

        when(mockDataProvider.createListOfCryptos()).thenReturn(List.of(mockCoin2, mockCoin2));

        CoinResponse response = recommendationService.getCoinWithHighestRangeForGivenDay("2022-10-01");
        assertEquals("testCoin2", response.getName());
    }
}