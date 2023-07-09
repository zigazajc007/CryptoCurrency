package com.rabbitcompany.listeners.blockExplodeEvent;

import com.rabbitcompany.CryptoCurrency;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

public class BlockExplodeListener extends AbstracExplodeListener implements Listener {

	public BlockExplodeListener(CryptoCurrency plugin) {
		super(plugin);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		onExplode(event.blockList());
		event.setCancelled(true);
	}

}
