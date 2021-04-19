package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class API {

    public static double btc_price = 0;
    public static double eth_price = 0;

    public static void startBTCPriceFetcher(String currency){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CryptoCurrency.getInstance(), () -> {
            try {
                String btc = new Scanner(new URL("https://blockchain.info/tobtc?currency=" + currency + "&value=100000").openStream(), "UTF-8").useDelimiter("\\A").next();
                if(Double.parseDouble(btc) != 0) btc_price = 100000 / Double.parseDouble(btc);
            } catch (IOException ignored) { }
        }, 0L, 20L * 300);
    }

    public static void startETHPriceFetcher(String currency){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CryptoCurrency.getInstance(), () -> {
            try {
                String eth = new Scanner(new URL("https://blockchain.info/tobtc?currency=" + currency + "&value=100000").openStream(), "UTF-8").useDelimiter("\\A").next();
                if(Double.parseDouble(eth) != 0) eth_price = 100000 / Double.parseDouble(eth);
            } catch (IOException ignored) { }
        }, 0L, 20L * 300);
    }

}
