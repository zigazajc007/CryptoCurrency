package com.rabbitcompany.listeners.blockExplodeEvent;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.Message;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;

import java.util.List;

public abstract class AbstracExplodeListener {

	private final CryptoCurrency cryptoCurrency;

	public AbstracExplodeListener(CryptoCurrency plugin) {
		cryptoCurrency = plugin;
	}

	// BlockExplodeListener
	// EntityExplodeListener
	public void onExplode(List<Block> blocks) {

		for (Block block : blocks) {
			if (!(block.getBlockData() instanceof WallSign)) continue;
			Sign sign = (Sign) block.getState();
			String line1 = sign.getLine(0);
			if (!line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_sell_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_sell_success"))))
				continue;
			String formatted_location = sign.getLocation().getBlockX() + "|" + sign.getLocation().getBlockY() + "|" + sign.getLocation().getBlockZ();
			if (cryptoCurrency.getSignShops().getString(formatted_location, null) == null) continue;
			break;
		}

	}

}
