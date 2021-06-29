package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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

    public static void Help(CommandSender player, String currency){
        String color = Settings.cryptos.get(currency).color;

        player.sendMessage(Message.chat("&7Listing commands:"));
        player.sendMessage(Message.chat(""));
        player.sendMessage(Message.chat(color + "/" + currency + " balance &7- Check " + currency + " balance"));
        player.sendMessage(Message.chat(color + "/" + currency + " price &7- Show " + currency + " price"));
        player.sendMessage(Message.chat(color + "/" + currency + " send <player> <amount> &7- Send " + currency + " to player"));
        player.sendMessage(Message.chat(color + "/" + currency + " buy <amount> &7- buy " + currency));
        player.sendMessage(Message.chat(color + "/" + currency + " sell <amount> &7- sell " + currency));
        if(player.hasPermission("cryptocurrency.give"))
            player.sendMessage(Message.chat(color + "/" + currency + " give <player> <amount> &7- Give " + currency + " to player"));
        if(player.hasPermission("cryptocurrency.take"))
            player.sendMessage(Message.chat(color + "/" + currency + " take <player> <amount> &7- Take " + currency + " from player"));
        player.sendMessage(Message.chat(""));
    }
}
