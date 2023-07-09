package com.rabbitcompany.listeners.playerJoinEvent;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.MySql;
import com.rabbitcompany.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CreateWalletListener implements Listener {

	private final CryptoCurrency cryptoCurrency;

	public CreateWalletListener(CryptoCurrency plugin) {
		cryptoCurrency = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		//Save player to Database
		if (CryptoCurrency.conn != null) {
			for (String crypto : Settings.cryptos.keySet())
				MySql.createPlayerWallet(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName(), crypto);
		}
	}

}
