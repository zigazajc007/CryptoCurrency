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
    public double price;
    public double marketCap;

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

    public void saveWallet(){
        try {
            this.wallet.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
