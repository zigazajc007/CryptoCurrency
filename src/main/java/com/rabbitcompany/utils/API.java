package com.rabbitcompany.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitcompany.CryptoCurrency;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class API {

    public static void startPriceFetcher(String currency){
        for(String crypto : Settings.cryptos.keySet()){
            Bukkit.getScheduler().runTaskTimerAsynchronously(CryptoCurrency.getInstance(), () -> {
                try {
                    String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/prices/" + crypto.toUpperCase() + "-" + currency + "/spot").openStream(), "UTF-8").useDelimiter("\\A").next();

                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
                    String data = jsonObject.getAsJsonObject("data").get("amount").getAsString();

                    if(Double.parseDouble(data) != 0) Settings.cryptos.get(crypto).price = Double.parseDouble(data);

                } catch (IOException ignored) { }
            }, 0L, 20L * 60L * CryptoCurrency.getInstance().getConf().getInt("price_fetch", 1));
        }
    }
}
