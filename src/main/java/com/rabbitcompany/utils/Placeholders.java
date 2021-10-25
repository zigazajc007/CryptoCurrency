package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class Placeholders extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){ return true; }

    @Override
    public @NotNull String getIdentifier() {
        return "cryptocurrency";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Black1_TV";
    }

    @Override
    public @NotNull String getVersion() {
        return "3.1.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {

        String[] identi = identifier.split("_");
        if(Settings.cryptos.get(identi[0]) == null) return "";

        if(identi.length == 3 && identi[1].equals("price") && Number.isNumeric(identi[2])) return API.getCryptoPriceFormatted(identi[0], Double.parseDouble(identi[2]));
        if(identi.length == 3 && identi[1].equals("balance")) return API.getBalanceFormatted(identi[2], identi[0]);
        if(identi[1].equals("supply")) return API.getFormatter(identi[0]).format(API.getCryptoSupply(identi[0]));
        if(identi[1].equals("maxSupply")) return API.getFormatter(identi[0]).format(API.getCryptoMaxSupply(identi[0]));
        if(identi[1].equals("marketCap")) return API.moneyFormatter.format(API.getCryptoMarketCap(identi[0]));
        if(identi[1].equals("price")) return API.getCryptoPriceFormatted(identi[0]);

        if(identi.length == 2 && identi[1].equals("change")) return (Settings.cryptos.get(identi[0]).rising) ? CryptoCurrency.getInstance().getConf().getString("price_rising", "&a▲") : CryptoCurrency.getInstance().getConf().getString("price_falling", "&c▼");

        if(identi.length == 3 && identi[1].equals("change") && identi[2].equals("fiat")){
            double yesterdayCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return API.moneyFormatter.format(cryptoPrice - yesterdayCryptoPrice);
        }

        if(identi.length == 3 && identi[1].equals("change") && identi[2].equals("percent")) {
            double yesterdayCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return "" + Number.roundDouble(Math.abs(yesterdayCryptoPrice - cryptoPrice) / ((yesterdayCryptoPrice + cryptoPrice) / 2.0) * 100, 2);
        }

        if(identi.length == 3 && identi[1].equals("change") && identi[2].equals("high")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".max", 0));
        }

        if(identi.length == 3 && identi[1].equals("change") && identi[2].equals("low")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".min", 0));
        }

        if(identi.length == 3 && identi[1].equals("change") && identi[2].equals("open")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".open", 0));
        }

        if(identi.length == 3 && identi[1].equals("change") && identi[2].equals("close")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".close", 0));
        }

        if (offlinePlayer == null) return "";
        if (offlinePlayer.getPlayer() == null) return "";

        final Player player = offlinePlayer.getPlayer();

        if(identi[1].equals("balance")) return API.getBalanceFormatted(player.getName(), identi[0]);

        if(identi.length == 4 && identi[1].equals("change") && identi[2].equals("fiat") && identi[3].equals("player")){
            double balance = API.getBalance(player.getName(), identi[0]);
            double yesterdayCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double yesterdayPrice = yesterdayCryptoPrice * balance;
            double price = API.getCryptoPrice(identi[0], balance);
            return API.moneyFormatter.format(price - yesterdayPrice);
        }
        if(identi.length == 4 && identi[1].equals("change") && identi[2].equals("percent") && identi[3].equals("player")) {
            double balance = API.getBalance(player.getName(), identi[0]);
            double yesterdayCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double yesterdayPrice = yesterdayCryptoPrice * balance;
            double price = API.getCryptoPrice(identi[0], balance);
            return "" + Number.roundDouble(Math.abs(yesterdayPrice - price) / ((yesterdayCryptoPrice + price) / 2.0) * 100, 2);
        }
        return "";
    }
}
