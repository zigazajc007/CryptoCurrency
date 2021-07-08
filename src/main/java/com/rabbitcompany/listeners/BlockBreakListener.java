package com.rabbitcompany.listeners;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final CryptoCurrency cryptoCurrency;

    public BlockBreakListener(CryptoCurrency plugin){
        cryptoCurrency = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent event){
        if(!(event.getBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) event.getBlock().getState();
        String line1 = sign.getLine(0);
        if(!line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_sell_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_sell_success")))) return;

        String formatted_location = sign.getLocation().getBlockX() + "|" + sign.getLocation().getBlockY() + "|" + sign.getLocation().getBlockZ();
        String owner = cryptoCurrency.getSignShops().getString(formatted_location, null);
        if(owner == null || event.getPlayer().getUniqueId().toString().equals(owner) || event.getPlayer().hasPermission("cryptocurrency.shop.break")){
            cryptoCurrency.getSignShops().set(formatted_location, null);
            cryptoCurrency.saveSignShops();
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(event.getBlock().getType() == Material.STONE || event.getBlock().getType() == Material.COBBLESTONE || event.getBlock().getType() == Material.NETHERITE_BLOCK || event.getBlock().getType() == Material.DIRT || event.getBlock().getType() == Material.GRASS) return;

        BlockFace blockFace = BlockFace.NORTH;
        for(int i = 0; i < 2; i++){
            if(event.getBlock().getRelative(blockFace).getState() instanceof Sign){
                Sign sign = (Sign) event.getBlock().getRelative(blockFace).getState();
                String line1 = sign.getLine(0);
                if(!line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_sell_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_sell_success")))) continue;
                event.setCancelled(true);
                return;
            }
            blockFace = blockFace.getOppositeFace();
        }

        blockFace = BlockFace.WEST;
        for(int i = 0; i < 2; i++){
            if(event.getBlock().getRelative(blockFace).getState() instanceof Sign){
                Sign sign = (Sign) event.getBlock().getRelative(blockFace).getState();
                String line1 = sign.getLine(0);
                if(!line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_sell_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_sell_success")))) continue;
                event.setCancelled(true);
                return;
            }
            blockFace = blockFace.getOppositeFace();
        }
    }

}
