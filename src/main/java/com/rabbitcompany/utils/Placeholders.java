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

        String[] identi = identifier.split("_");

        if(Settings.cryptos.get(identi[0]) == null) return "";

        if(identi[1].equals("price")){
            NumberFormat money_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("money_format", "###,###.00"));
            return money_formatter.format(Settings.cryptos.get(identi[0]).price);
        }

        if(identi[1].equals("balance")){
            NumberFormat formatter = new DecimalFormat("#" + Settings.cryptos.get(identi[0]).format);
            double balance = (CryptoCurrency.conn != null) ? MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), identi[0]) : Settings.cryptos.get(identi[0]).wallet.getDouble(player.getUniqueId().toString());
            return formatter.format(balance);
        }

        return "";
    }
}
