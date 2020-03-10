package com.rabbitcompany.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class API {

    public static double getBTCFromPrice(String currency, String value){
        try {
            String btc = new Scanner(new URL("https://blockchain.info/tobtc?currency=" + currency + "&value=" + value).openStream(), "UTF-8").useDelimiter("\\A").next();
            return Double.parseDouble(btc);
        } catch (IOException ignored) {
            return 0;
        }
    }

}
