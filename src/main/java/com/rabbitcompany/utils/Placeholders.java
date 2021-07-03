package com.rabbitcompany.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        return "3.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {

        String[] identi = identifier.split("_");
        if(Settings.cryptos.get(identi[0]) == null) return "";

        if(identi.length == 3 && identi[1].equals("price") && Number.isNumeric(identi[2])) return API.getCryptoPriceFormatted(identi[0], Double.parseDouble(identi[2]));
        if(identi.length == 3 && identi[1].equals("balance")) return API.getBalanceFormatted(identi[2], identi[0]);
        if(identi[1].equals("price")) return API.moneyFormatter.format(Settings.cryptos.get(identi[0]).price);

        if (offlinePlayer == null) return "";
        if (offlinePlayer.getPlayer() == null) return "";

        final Player player = offlinePlayer.getPlayer();

        if(identi[1].equals("balance")) return API.getBalanceFormatted(player.getName(), identi[0]);

        return "";
    }
}
