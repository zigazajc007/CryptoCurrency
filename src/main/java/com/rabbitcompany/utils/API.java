package com.rabbitcompany.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitcompany.CryptoCurrency;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class API {

    public static NumberFormat moneyFormatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("money_format", "###,##0.00"));

    public static boolean isCryptoEnabled(String crypto){
        return Settings.cryptos.get(crypto) != null;
    }

    public static List<String> getEnabledCryptos(){
        return new ArrayList<>(Settings.cryptos.keySet());
    }

    public static String getAPICurrency(){
        return CryptoCurrency.getInstance().getConf().getString("api_currency", "USD");
    }

    public static NumberFormat getFormatter(String crypto){
        if(!isCryptoEnabled(crypto)) return new DecimalFormat("#0.0000");
        return new DecimalFormat("#" + Settings.cryptos.get(crypto).format);
    }

    public static boolean reloadCrypto(String crypto){
        if(!isCryptoEnabled(crypto)) return false;
        Settings.cryptos.get(crypto).initializeWallet();
        return true;
    }

    public static void reloadConfigFiles(){
        CryptoCurrency.getInstance().loadYamls();
    }

    public static String getName(String UUID){
        for(String name : CryptoCurrency.getInstance().getPlayers().getValues(false).keySet()){
            if(CryptoCurrency.getInstance().getPlayers().getString(name, "").equals(UUID)) return name;
        }
        return null;
    }

    public static String getUUID(String player){
        return CryptoCurrency.getInstance().getPlayers().getString(player, null);
    }

    public static boolean hasWallet(String player){
        return CryptoCurrency.getInstance().getPlayers().getString(player, null) != null;
    }

    public static double getBalance(String player, String crypto){
        if(!isCryptoEnabled(crypto)) return 0;
        if(!hasWallet(player)) return 0;
        String UUID = getUUID(player);
        if(CryptoCurrency.conn != null) return MySql.getPlayerBalance(UUID, player, crypto);
        return Settings.cryptos.get(crypto).wallet.getDouble(UUID, 0);
    }

    public static String getBalanceFormatted(String player, String crypto){
        return getFormatter(crypto).format(getBalance(player, crypto));
    }

    public static double getCryptoPrice(String crypto){
        if(!isCryptoEnabled(crypto)) return 0;
        return Settings.cryptos.get(crypto).price;
    }

    public static double getCryptoPrice(String crypto, double amount){
        return getCryptoPrice(crypto) * amount;
    }

    public static String getCryptoPriceFormatted(String crypto){
        return moneyFormatter.format(getCryptoPrice(crypto));
    }

    public static String getCryptoPriceFormatted(String crypto, double amount){
        return moneyFormatter.format(getCryptoPrice(crypto, amount));
    }

    public static void calculateCryptoSupply(String crypto){
        if(!isCryptoEnabled(crypto)) return;

        if(CryptoCurrency.conn != null){
            Settings.cryptos.get(crypto).supply = MySql.getCryptoSupply(crypto);
        }else{
            double supply = 0;
            for(String player : Settings.cryptos.get(crypto).wallet.getKeys(false)){
                supply += Settings.cryptos.get(crypto).wallet.getDouble(player, 0);
            }
            Settings.cryptos.get(crypto).supply = supply;
        }
    }

    public static double getCryptoSupply(String crypto){
        if(!isCryptoEnabled(crypto)) return 0;
        return Settings.cryptos.get(crypto).supply;
    }

    public static double getCryptoMaxSupply(String crypto){
        if(!isCryptoEnabled(crypto)) return 0;
        return Settings.cryptos.get(crypto).max_supply;
    }

    public static double getCryptoMarketCap(String crypto){
        if(!isCryptoEnabled(crypto)) return 0;
        return getCryptoSupply(crypto) * getCryptoPrice(crypto);
    }

    public static int giveCrypto(String toPlayer, String crypto, double amount){
        if(!isCryptoEnabled(crypto)) return 1;
        if(amount < Settings.cryptos.get(crypto).minimum) return 2;
        if(amount > Settings.cryptos.get(crypto).maximum) return 3;
        if(amount > (getCryptoMaxSupply(crypto) - getCryptoSupply(crypto))) return 12;
        if(!hasWallet(toPlayer)) return 4;
        String UUID = getUUID(toPlayer);
        double balance = getBalance(toPlayer, crypto);
        if(CryptoCurrency.conn != null){
            MySql.setPlayerBalance(UUID, toPlayer, getFormatter(crypto).format(balance + amount), crypto);
            Settings.cryptos.get(crypto).supply += amount;
            return 10;
        }
        Settings.cryptos.get(crypto).wallet.set(UUID, balance + amount);
        Settings.cryptos.get(crypto).saveWallet();
        Settings.cryptos.get(crypto).supply += amount;
        return 10;
    }

    public static int buyCrypto(String player, String crypto, double amount){
        if(!CryptoCurrency.vault) return 9;
        if(!isCryptoEnabled(crypto)) return 1;
        if(amount < Settings.cryptos.get(crypto).minimum) return 2;
        if(amount > Settings.cryptos.get(crypto).maximum) return 3;
        if(amount > (getCryptoMaxSupply(crypto) - getCryptoSupply(crypto))) return 12;
        if(!hasWallet(player)) return 4;
        String UUID = getUUID(player);
        double balance = getBalance(player, crypto);
        double player_balance = CryptoCurrency.getEconomy().getBalance(player);
        double money_price = getCryptoPrice(crypto, amount);
        if(player_balance < money_price) return 11;
        if(CryptoCurrency.conn != null){
            MySql.setPlayerBalance(UUID, player, API.getFormatter(crypto).format(balance + amount), crypto);
            CryptoCurrency.getEconomy().withdrawPlayer(player, money_price);
            Settings.cryptos.get(crypto).supply += amount;
            return 10;
        }
        Settings.cryptos.get(crypto).wallet.set(UUID, balance + amount);
        Settings.cryptos.get(crypto).saveWallet();
        CryptoCurrency.getEconomy().withdrawPlayer(player, money_price);
        Settings.cryptos.get(crypto).supply += amount;
        return 10;
    }

    public static int takeCrypto(String fromPlayer, String crypto, double amount){
        if(!isCryptoEnabled(crypto)) return 1;
        if(amount < Settings.cryptos.get(crypto).minimum) return 2;
        if(amount > Settings.cryptos.get(crypto).maximum) return 3;
        if(!hasWallet(fromPlayer)) return 4;
        String UUID = getUUID(fromPlayer);
        double balance = getBalance(fromPlayer, crypto);
        if(balance - amount < 0) amount = balance;
        if(CryptoCurrency.conn != null){
            MySql.setPlayerBalance(UUID, fromPlayer, getFormatter(crypto).format(balance - amount), crypto);
            Settings.cryptos.get(crypto).supply -= amount;
            return 10;
        }
        Settings.cryptos.get(crypto).wallet.set(UUID, balance - amount);
        Settings.cryptos.get(crypto).saveWallet();
        Settings.cryptos.get(crypto).supply -= amount;
        return 10;
    }

    public static int sellCrypto(String player, String crypto, double amount){
        if(!CryptoCurrency.vault) return 9;
        if(!isCryptoEnabled(crypto)) return 1;
        if(amount < Settings.cryptos.get(crypto).minimum) return 2;
        if(amount > Settings.cryptos.get(crypto).maximum) return 3;
        if(!hasWallet(player)) return 4;
        String UUID = getUUID(player);
        double balance = getBalance(player, crypto);
        double money_price = getCryptoPrice(crypto, amount);
        if(balance < amount) return 11;
        if(CryptoCurrency.conn != null){
            MySql.setPlayerBalance(UUID, player, API.getFormatter(crypto).format(balance - amount), crypto);
            CryptoCurrency.getEconomy().depositPlayer(player, money_price);
            Settings.cryptos.get(crypto).supply -= amount;
            return 10;
        }
        Settings.cryptos.get(crypto).wallet.set(UUID, balance - amount);
        Settings.cryptos.get(crypto).saveWallet();
        CryptoCurrency.getEconomy().depositPlayer(player, money_price);
        Settings.cryptos.get(crypto).supply -= amount;
        return 10;
    }

    public static int sendCrypto(String fromPlayer, String toPlayer, String crypto, double amount){
        if(!isCryptoEnabled(crypto)) return 1;
        if(amount < Settings.cryptos.get(crypto).minimum) return 2;
        if(amount > Settings.cryptos.get(crypto).maximum) return 3;
        if(!hasWallet(fromPlayer)) return 4;
        if(!hasWallet(toPlayer)) return 5;
        double fromBalance = getBalance(fromPlayer, crypto);

        if(fromBalance < amount) return 6;
        if(takeCrypto(fromPlayer, crypto, amount) != 10) return 7;
        if(giveCrypto(toPlayer, crypto, amount) != 10) return 8;
        return 10;
    }

    public static int sendCrypto(String fromPlayer, String crypto, double amount){
        if(!isCryptoEnabled(crypto)) return 1;
        if(amount < Settings.cryptos.get(crypto).minimum) return 2;
        if(amount > Settings.cryptos.get(crypto).maximum) return 3;
        if(!hasWallet(fromPlayer)) return 4;
        double fromBalance = getBalance(fromPlayer, crypto);

        if(fromBalance < amount) return 6;
        if(takeCrypto(fromPlayer, crypto, amount) != 10) return 7;
        return 10;
    }

    public static void startPriceFetcher(String currency){
        Bukkit.getScheduler().runTaskTimerAsynchronously(CryptoCurrency.getInstance(), () -> {
            try {
                String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/exchange-rates?currency=" + currency).openStream(), "UTF-8").useDelimiter("\\A").next();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);

                for(String cur : Settings.cryptos.keySet()){
                    String value = jsonObject.getAsJsonObject("data").getAsJsonObject("rates").get(cur.toUpperCase()).getAsString();
                    double price = 1 / Double.parseDouble(value);
                    Settings.cryptos.get(cur).rising = price >= Settings.cryptos.get(cur).price;
                    Settings.cryptos.get(cur).price = price;

                    if(!CryptoCurrency.getInstance().getConf().getBoolean("monitor_history", true)) continue;
                    double max = Settings.cryptos.get(cur).history.getDouble(LocalDate.now() + ".max", Double.MIN_VALUE);
                    double min = Settings.cryptos.get(cur).history.getDouble(LocalDate.now() + ".min", Double.MAX_VALUE);
                    if(price > max) Settings.cryptos.get(cur).history.set(LocalDate.now() + ".max", Number.roundDouble(price, 2));
                    if(price < min) Settings.cryptos.get(cur).history.set(LocalDate.now() + ".min", Number.roundDouble(price, 2));
                    Settings.cryptos.get(cur).history.set(LocalDate.now() + ".avg", Number.roundDouble((max + min) / 2.0, 2));
                    Settings.cryptos.get(cur).saveHistory();
                }

            } catch (IOException ignored) {}
        }, 0L, 20L * CryptoCurrency.getInstance().getConf().getInt("price_fetch", 20));
    }

    public static void startSupplyCalculator(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(CryptoCurrency.getInstance(), () -> {
            for(String cur : Settings.cryptos.keySet()) calculateCryptoSupply(cur);
        }, 0L, 20L * CryptoCurrency.getInstance().getConf().getInt("supply_calculator", 60));
    }
}
