package com.rabbitcompany;

import com.rabbitcompany.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Updater {

    public static void sendPlayer(Player player){
        try {
            if (CryptoCurrency.new_version != null) {
                player.sendMessage(Message.chat("&7[&cAdmin GUI&7] &aAn update was found!"));
                player.sendMessage(Message.chat("&7[&cAdmin GUI&7] &aNew version: &b" + CryptoCurrency.new_version));
                player.sendMessage(Message.chat("&7[&cAdmin GUI&7] &aDownload: &bhttps://www.spigotmc.org/resources/admin-gui.71157/"));
            }
        } catch (Exception e) {
            player.sendMessage(Message.chat("&7[&cAdmin GUI&7] &cCould not check for updates!"));
        }
    }

    public static void sendConsole(){
        try {
            if (CryptoCurrency.new_version != null) {
                Bukkit.getConsoleSender().sendMessage(Message.chat("&7[&cAdmin GUI&7] &aAn update was found!"));
                Bukkit.getConsoleSender().sendMessage(Message.chat("&7[&cAdmin GUI&7] &aNew version: &b" + CryptoCurrency.new_version));
                Bukkit.getConsoleSender().sendMessage(Message.chat("&7[&cAdmin GUI&7] &aDownload: &bhttps://www.spigotmc.org/resources/admin-gui.71157/"));
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Message.chat("&7[&cAdmin GUI&7] &cCould not check for updates!"));
        }
    }

}
