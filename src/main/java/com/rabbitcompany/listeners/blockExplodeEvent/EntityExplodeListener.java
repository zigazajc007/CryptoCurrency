package com.rabbitcompany.listeners.blockExplodeEvent;

import com.rabbitcompany.CryptoCurrency;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener extends AbstracExplodeListener implements Listener {

    public EntityExplodeListener(CryptoCurrency plugin) {
        super(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        onExplode(event.blockList());
        event.setCancelled(true);
    }

}
