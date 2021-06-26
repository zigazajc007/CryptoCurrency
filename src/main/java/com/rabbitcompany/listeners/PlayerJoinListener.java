package com.rabbitcompany.listeners;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.MySql;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
            MySql.createPlayerWallet(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName(), "btc");
            MySql.createPlayerWallet(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName(), "eth");
        }
    }

}
