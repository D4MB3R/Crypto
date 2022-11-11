package com.berec.crypto.recommender;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Service
public class DataProvider {

    /**
     * This field gets its value from resources/application.properties
     * */
    @Value("${csv.path}")
    private String pathToDataSource;

    /**
     * This method reads the list of csv files inside the provided folder and creates {@link Coin} objects from them
     * with the help of {@link this#createCryptoFromCSV(CSVReader)}.
     *
     * @return a list of {@link Coin} objects.
     * */
    public List<Coin> createListOfCryptos() {
        List<Coin> cryptoList = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(pathToDataSource).listFiles()))
        {
            if (file.getName().contains(".csv"))
            {
                try
                {
                    CSVReader reader = new CSVReader(new FileReader(file.getAbsolutePath()));
                    cryptoList.add(createCryptoFromCSV(reader));
                } catch (IOException | CsvException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return cryptoList;
    }

    /**
     * This method reads a csv file and creates a {@link Coin} object from it.
     *
     * @param reader the csv file reader.
     *
     * @return a {@link Coin} object
     * */
    private Coin createCryptoFromCSV(CSVReader reader) throws IOException, CsvException {
        List<String[]> rows = reader.readAll();
        reader.close();
        String[] headerRow = rows.get(0);
        Map<String, Integer> header = new HashMap<>();
        for (int i = 0; i < headerRow.length; i++)
        {
            header.put(headerRow[i], i);
        }
        int symbol = header.get("symbol");
        int timestamp = header.get("timestamp");
        int price = header.get("price");
        String name = rows.get(1)[symbol];
        TreeMap<Timestamp, Double> dailyPrices = new TreeMap<>();
        dailyPrices.put(new Timestamp(Long.parseLong(rows.get(1)[timestamp])), Double.parseDouble(rows.get(1)[price]));
        if (rows.size() > 2) {
            for (String[] row : rows.subList(2, rows.size() - 1))
            {
                dailyPrices.put(new Timestamp(Long.parseLong(row[timestamp])), Double.parseDouble(row[price]));
            }
        }
        return new Coin(name, dailyPrices);
    }
}
