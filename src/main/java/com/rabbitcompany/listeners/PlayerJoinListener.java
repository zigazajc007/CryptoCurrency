package com.rabbitcompany.listeners;

import com.rabbitcompany.CryptoCurrency;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoinListener implements Listener {

    private CryptoCurrency cryptoCurrency;

    public PlayerJoinListener(CryptoCurrency plugin){
        cryptoCurrency = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        //SQL
        if(CryptoCurrency.conn != null){
            try {
                CryptoCurrency.mySQL.update("INSERT INTO cryptocurrency_btc VALUES ('" + event.getPlayer().getUniqueId() + "', '" + event.getPlayer().getName() + "', 0);");
            } catch (SQLException ignored) { }
        }
    }

}
