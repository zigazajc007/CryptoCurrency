package com.rabbitcompany.listeners;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.Message;
import com.rabbitcompany.utils.Number;
import com.rabbitcompany.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final CryptoCurrency cryptoCurrency;

    public PlayerInteractListener(CryptoCurrency plugin){
        cryptoCurrency = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getClickedBlock() == null) return;
        if(!(event.getClickedBlock().getState() instanceof Sign)) return;

        Sign sign = (Sign) event.getClickedBlock().getState();
        String line1 = sign.getLine(0);
        String line2 = ChatColor.stripColor(sign.getLine(1)).replace("x", "");
        String line3 = ChatColor.stripColor(sign.getLine(2));
        String line4 = ChatColor.stripColor(sign.getLine(3));

        if(!Number.isNumeric(line2)) return;
        double amount = Double.parseDouble(line2);

        line3 = line3.replace(" ", "_").toUpperCase();
        if(Material.getMaterial(line3) == null) return;

        String currency = null;
        for(String crypto : Settings.cryptos.keySet()) if(line4.contains(crypto.toUpperCase())) currency = crypto;
        if(currency == null) return;
        line4 = line4.replaceAll("[^0-9.]", "");
        if(!Number.isNumeric(line4)) return;
        double price = Double.parseDouble(line4);

        if(!line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_sell_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_buy_success"))) && !line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_sell_success")))) return;

        String formatted_location = sign.getLocation().getBlockX() + "|" + sign.getLocation().getBlockY() + "|" + sign.getLocation().getBlockZ();
        String owner = cryptoCurrency.getSignShops().getString(formatted_location, null);
        if(owner == null) return;

        if(owner.equals("AdminShop")){

            return;
        }

        event.getPlayer().sendMessage("Shop is valid!");
    }

}
