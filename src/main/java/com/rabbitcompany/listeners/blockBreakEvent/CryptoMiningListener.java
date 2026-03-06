package com.rabbitcompany.listeners.blockBreakEvent;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.API;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class CryptoMiningListener implements Listener {

	public CryptoMiningListener(CryptoCurrency plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMining(BlockBreakEvent event) {
		Player player = event.getPlayer();
		String material = event.getBlock().getType().toString();

		if(!player.hasPermission("cryptocurrency.mining")) return;

		ItemStack tool;
		if (!Bukkit.getVersion().contains("1.8")) {
			tool = event.getPlayer().getInventory().getItemInMainHand();
		} else {
			tool = event.getPlayer().getInventory().getItemInHand();
		}

		if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) return;

		API.cryptoMining(player, material);
	}


}
