package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
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
        List<String> help = CryptoCurrency.getInstance().getEngl().getStringList("help");

        for(String message : help){
            player.sendMessage(Message.chat(message.replace("{color}", color).replace("{crypto}", currency)));
        }
    }
}
