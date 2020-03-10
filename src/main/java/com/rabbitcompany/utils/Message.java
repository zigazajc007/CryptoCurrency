package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Message {

    public static String chat(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String getMessage(UUID uuid, String config){
        String mess;
        String lang = Settings.language.getOrDefault(uuid, CryptoCurrency.getInstance().getConf().getString("default_language"));
        switch (lang){
            default:
                mess = CryptoCurrency.getInstance().getEngl().getString(config);
                break;
        }
        if(mess != null){
            return chat(mess);
        }else{
            return chat("&cValue: &6" + config + "&c is missing in " + lang + ".yml file! Please add it or delete " + lang + ".yml file.");
        }
    }

    public static void Help(Player player, String currency){
        player.sendMessage(Message.chat("&7Listing commands:"));
        player.sendMessage(Message.chat(""));
        player.sendMessage(Message.chat("&b/" + currency + " balance &7- Check btc balance"));
        player.sendMessage(Message.chat("&b/" + currency + " price &7- Show btc price"));
        player.sendMessage(Message.chat("&b/" + currency + " send <player> <amount> &7- Send btc to player"));
        player.sendMessage(Message.chat("&b/" + currency + " give <player> <amount> &7- Give btc to player"));
        player.sendMessage(Message.chat("&b/" + currency + " take <player> <amount> &7- Take btc from player"));
        player.sendMessage(Message.chat(""));
    }
}
