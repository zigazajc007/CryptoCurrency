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
        String lang = CryptoCurrency.getInstance().getConf().getString("default_language", "English");
        switch (lang){
            case "Russian":
                mess = CryptoCurrency.getInstance().getRussi().getString(config);
                break;
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
        String lang = CryptoCurrency.getInstance().getConf().getString("default_language", "English");
        List<String> help = CryptoCurrency.getInstance().getEngl().getStringList("help");
        switch (lang){
            case "Russian":
                help = CryptoCurrency.getInstance().getRussi().getStringList("help");
                break;
        }

        for(String message : help){
            player.sendMessage(Message.chat(message.replace("{color}", color).replace("{crypto}", currency)));
        }
    }
}
