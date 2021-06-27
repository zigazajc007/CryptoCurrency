package com.rabbitcompany.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitcompany.CryptoCurrency;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class API {

    public static double btc_price = 0;
    public static double eth_price = 0;
    public static double usdt_price = 0;

    public static void startPriceFetcher(String currency){
        Bukkit.getScheduler().runTaskTimerAsynchronously(CryptoCurrency.getInstance(), () -> {
            try {
                String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/prices/BTC-" + currency + "/spot").openStream(), "UTF-8").useDelimiter("\\A").next();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
                String btc = jsonObject.getAsJsonObject("data").get("amount").getAsString();

                if(Double.parseDouble(btc) != 0) btc_price = Double.parseDouble(btc);

            } catch (IOException ignored) { }

            try {
                String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/prices/ETH-" + currency + "/spot").openStream(), "UTF-8").useDelimiter("\\A").next();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
                String eth = jsonObject.getAsJsonObject("data").get("amount").getAsString();

                if(Double.parseDouble(eth) != 0) eth_price = Double.parseDouble(eth);

            } catch (IOException ignored) { }

            try {
                String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/prices/USDT-" + currency + "/spot").openStream(), "UTF-8").useDelimiter("\\A").next();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
                String usdt = jsonObject.getAsJsonObject("data").get("amount").getAsString();

                if(Double.parseDouble(usdt) != 0) usdt_price = Double.parseDouble(usdt);

            } catch (IOException ignored) { }
        }, 0L, 20L * 60L * CryptoCurrency.getInstance().getConf().getInt("price_fetch", 1));
    }
}
