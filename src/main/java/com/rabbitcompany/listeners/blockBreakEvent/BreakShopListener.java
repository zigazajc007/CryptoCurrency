package com.rabbitcompany.listeners.blockBreakEvent;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakShopListener implements Listener {

	private static final BlockFace[] SIDES = new BlockFace[]{
		BlockFace.UP,
		BlockFace.NORTH,
		BlockFace.SOUTH,
		BlockFace.WEST,
		BlockFace.EAST
	};
	private final CryptoCurrency cryptoCurrency;

	public BreakShopListener(CryptoCurrency plugin) {
		cryptoCurrency = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		if (!(event.getBlock().getBlockData() instanceof WallSign)) return;
		Sign sign = (Sign) event.getBlock().getState();
		String line1 = sign.getLine(0);
		if (!line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_sell_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_sell_success"))))
			return;

		String formatted_location = sign.getLocation().getBlockX() + "|" + sign.getLocation().getBlockY() + "|" + sign.getLocation().getBlockZ();
		String owner = cryptoCurrency.getSignShops().getString(formatted_location, null);
		if (owner == null || event.getPlayer().getUniqueId().toString().equals(owner) || event.getPlayer().hasPermission("cryptocurrency.shop.break")) {
			cryptoCurrency.getSignShops().set(formatted_location, null);
			cryptoCurrency.saveSignShops();
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		for (BlockFace side : SIDES) {
			final Block b = event.getBlock().getRelative(side);
			if (b.getBlockData() instanceof WallSign) {
				WallSign wallSign = (WallSign) b.getBlockData();
				if (b.getRelative(wallSign.getFacing().getOppositeFace()).equals(event.getBlock())) {
					Sign sign = (Sign) b.getState();
					String line1 = sign.getLine(0);
					if (!line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_sell_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_sell_success"))))
						continue;
					event.setCancelled(true);
				}
			}
		}
	}

}
