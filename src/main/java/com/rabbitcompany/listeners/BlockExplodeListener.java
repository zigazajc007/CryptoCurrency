package com.rabbitcompany.listeners;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockExplodeListener implements Listener {

    private final CryptoCurrency cryptoCurrency;

    public BlockExplodeListener(CryptoCurrency plugin){
        cryptoCurrency = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (!(block.getBlockData() instanceof WallSign)) continue;
            Sign sign = (Sign) block.getState();
            String line1 = sign.getLine(0);
            if (!line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_sell_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_sell_success")))) continue;
            String formatted_location = sign.getLocation().getBlockX() + "|" + sign.getLocation().getBlockY() + "|" + sign.getLocation().getBlockZ();
            if (cryptoCurrency.getSignShops().getString(formatted_location, null) == null) continue;
            event.setCancelled(true);
            break;
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (!(block.getBlockData() instanceof WallSign)) continue;
            Sign sign = (Sign) block.getState();
            String line1 = sign.getLine(0);
            if (!line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_sell_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_sell_success")))) continue;
            String formatted_location = sign.getLocation().getBlockX() + "|" + sign.getLocation().getBlockY() + "|" + sign.getLocation().getBlockZ();
            if (cryptoCurrency.getSignShops().getString(formatted_location, null) == null) continue;
            event.setCancelled(true);
            break;
        }
    }
}
