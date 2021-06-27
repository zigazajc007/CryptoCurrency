package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
        return "2.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {

        if (offlinePlayer == null) return "";
        if (!offlinePlayer.isOnline()) return "";
        if (offlinePlayer.getPlayer() == null) return "";

        final Player player = offlinePlayer.getPlayer();

        NumberFormat btc_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("btc_format", "0.0000"));
        NumberFormat bch_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("bch_format", "0.0000"));
        NumberFormat eth_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("eth_format", "0.000"));
        NumberFormat etc_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("etc_format", "0.000"));
        NumberFormat doge_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("doge_format", "0.000"));
        NumberFormat ltc_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("ltc_format", "0.000"));
        NumberFormat usdt_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("usdt_format", "0.00"));
        NumberFormat money_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("money_format", "###,###.00"));

        switch (identifier) {
            case "btc_price":
                return money_formatter.format(API.btc_price);
            case "bch_price":
                return money_formatter.format(API.bch_price);
            case "eth_price":
                return money_formatter.format(API.eth_price);
            case "etc_price":
                return money_formatter.format(API.etc_price);
            case "doge_price":
                return money_formatter.format(API.doge_price);
            case "ltc_price":
                return money_formatter.format(API.ltc_price);
            case "usdt_price":
                return money_formatter.format(API.usdt_price);
            case "btc_balance":
                double btc_balance = (CryptoCurrency.conn != null) ? MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "btc") : CryptoCurrency.getInstance().getBtcw().getDouble(player.getUniqueId().toString());
                return btc_formatter.format(btc_balance);
            case "bch_balance":
                double bch_balance = (CryptoCurrency.conn != null) ? MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "bch") : CryptoCurrency.getInstance().getBchw().getDouble(player.getUniqueId().toString());
                return bch_formatter.format(bch_balance);
            case "eth_balance":
                double eth_balance = (CryptoCurrency.conn != null) ? MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "eth") : CryptoCurrency.getInstance().getEthw().getDouble(player.getUniqueId().toString());
                return eth_formatter.format(eth_balance);
            case "etc_balance":
                double etc_balance = (CryptoCurrency.conn != null) ? MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "etc") : CryptoCurrency.getInstance().getEtcw().getDouble(player.getUniqueId().toString());
                return etc_formatter.format(etc_balance);
            case "doge_balance":
                double doge_balance = (CryptoCurrency.conn != null) ? MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "doge") : CryptoCurrency.getInstance().getDogew().getDouble(player.getUniqueId().toString());
                return doge_formatter.format(doge_balance);
            case "ltc_balance":
                double ltc_balance = (CryptoCurrency.conn != null) ? MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "ltc") : CryptoCurrency.getInstance().getLtcw().getDouble(player.getUniqueId().toString());
                return ltc_formatter.format(ltc_balance);
            case "usdt_balance":
                double usdt_balance = (CryptoCurrency.conn != null) ? MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "usdt") : CryptoCurrency.getInstance().getUsdtw().getDouble(player.getUniqueId().toString());
                return usdt_formatter.format(usdt_balance);
            default:
                return null;
        }
    }
}
