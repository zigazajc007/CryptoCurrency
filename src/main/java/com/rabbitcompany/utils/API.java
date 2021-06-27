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
    public static double bch_price = 0;
    public static double eth_price = 0;
    public static double etc_price = 0;
    public static double doge_price = 0;
    public static double ltc_price = 0;
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
                String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/prices/BCH-" + currency + "/spot").openStream(), "UTF-8").useDelimiter("\\A").next();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
                String bch = jsonObject.getAsJsonObject("data").get("amount").getAsString();

                if(Double.parseDouble(bch) != 0) bch_price = Double.parseDouble(bch);

            } catch (IOException ignored) { }

            try {
                String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/prices/ETH-" + currency + "/spot").openStream(), "UTF-8").useDelimiter("\\A").next();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
                String eth = jsonObject.getAsJsonObject("data").get("amount").getAsString();

                if(Double.parseDouble(eth) != 0) eth_price = Double.parseDouble(eth);

            } catch (IOException ignored) { }

            try {
                String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/prices/ETC-" + currency + "/spot").openStream(), "UTF-8").useDelimiter("\\A").next();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
                String etc = jsonObject.getAsJsonObject("data").get("amount").getAsString();

                if(Double.parseDouble(etc) != 0) etc_price = Double.parseDouble(etc);

            } catch (IOException ignored) { }

            try {
                String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/prices/DOGE-" + currency + "/spot").openStream(), "UTF-8").useDelimiter("\\A").next();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
                String doge = jsonObject.getAsJsonObject("data").get("amount").getAsString();

                if(Double.parseDouble(doge) != 0) doge_price = Double.parseDouble(doge);

            } catch (IOException ignored) { }

            try {
                String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/prices/LTC-" + currency + "/spot").openStream(), "UTF-8").useDelimiter("\\A").next();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
                String ltc = jsonObject.getAsJsonObject("data").get("amount").getAsString();

                if(Double.parseDouble(ltc) != 0) ltc_price = Double.parseDouble(ltc);

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
