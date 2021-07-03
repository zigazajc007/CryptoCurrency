package com.rabbitcompany.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitcompany.CryptoCurrency;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Crypto {

    public String fullName;
    public String name;
    public String color;
    public String format;
    public double maximum;
    public double minimum;
    public double price;
    public double marketCap;
    public BukkitTask priceFetcher;

    public File file;
    public YamlConfiguration wallet = new YamlConfiguration();

    public Crypto(String fullName, String name, String color, String format, double maximum, double minimum){
        this.fullName = fullName;
        this.name = name;
        this.color = color;
        this.format = format;
        this.maximum = maximum;
        this.minimum = minimum;

        initializeWallet();
        startPriceFetcher(API.getAPICurrency());
    }

    public void initializeWallet(){
        new File(CryptoCurrency.getInstance().getDataFolder(), "Wallets").mkdir();
        file = new File(CryptoCurrency.getInstance().getDataFolder(), "Wallets/" + this.name + ".yml");

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            this.wallet.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void startPriceFetcher(String currency){
        priceFetcher = Bukkit.getScheduler().runTaskTimerAsynchronously(CryptoCurrency.getInstance(), () -> {
            try {
                String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/prices/" + this.name.toUpperCase() + "-" + currency + "/spot").openStream(), "UTF-8").useDelimiter("\\A").next();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
                String data = jsonObject.getAsJsonObject("data").get("amount").getAsString();

                if(Double.parseDouble(data) != 0) this.price = Double.parseDouble(data);

            } catch (IOException ignored) { }
        }, 0L, 20L * 60L * CryptoCurrency.getInstance().getConf().getInt("price_fetch", 1));
    }

    public void saveWallet(){
        try {
            this.wallet.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
