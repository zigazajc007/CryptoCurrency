package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Crypto {

    public String fullName;
    public String name;
    public String color;
    public String format;
    public double maximum;
    public double minimum;
    public double max_supply;
    public double price;
    public double supply = 0;
    public boolean rising = false;
    public int binanceArrayPosition = -1;

    public File fileWallet;
    public YamlConfiguration wallet = new YamlConfiguration();

    public File fileHistory;
    public YamlConfiguration history = new YamlConfiguration();

    public Crypto(String fullName, String name, String color, String format, double maximum, double minimum, double max_supply){
        this.fullName = fullName;
        this.name = name;
        this.color = color;
        this.format = format;
        this.maximum = maximum;
        this.minimum = minimum;
        this.max_supply = max_supply;

        initializeWallet();
    }

    public void initializeWallet(){
        new File(CryptoCurrency.getInstance().getDataFolder(), "Wallets").mkdir();
        new File(CryptoCurrency.getInstance().getDataFolder(), "History").mkdir();
        fileWallet = new File(CryptoCurrency.getInstance().getDataFolder(), "Wallets/" + this.name + ".yml");
        fileHistory = new File(CryptoCurrency.getInstance().getDataFolder(), "History/" + this.name + ".yml");

        try {
            fileWallet.createNewFile();
            fileHistory.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            this.wallet.load(this.fileWallet);
            this.history.load(this.fileHistory);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveWallet(){
        try {
            this.wallet.save(this.fileWallet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveHistory(){
        try {
            this.history.save(this.fileHistory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
