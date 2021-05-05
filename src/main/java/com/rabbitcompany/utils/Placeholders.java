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
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {

        if (offlinePlayer == null) return "";
        if (!offlinePlayer.isOnline()) return "";

        final Player player = offlinePlayer.getPlayer();

        NumberFormat btc_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("btc_format", "0.0000"));
        NumberFormat eth_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("eth_format", "0.000"));
        NumberFormat money_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("money_format", "###,###.00"));

        switch (identifier) {
            case "btc_price":
                return money_formatter.format(API.btc_price);
            case "eth_price":
                return money_formatter.format(API.eth_price);
            case "btc_balance":
                double btc_balance;
                if(CryptoCurrency.conn != null){
                    btc_balance = MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "btc");
                }else{
                    btc_balance = CryptoCurrency.getInstance().getBtcw().getDouble(player.getUniqueId().toString());
                }
                return btc_formatter.format(btc_balance);
            case "eth_balance":
                double balance;
                if(CryptoCurrency.conn != null){
                    balance = MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "eth");
                }else{
                    balance = CryptoCurrency.getInstance().getEthw().getDouble(player.getUniqueId().toString());
                }
                return eth_formatter.format(balance);
            default:
                return null;
        }
    }
}
