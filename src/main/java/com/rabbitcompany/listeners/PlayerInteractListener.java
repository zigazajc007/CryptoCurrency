package com.rabbitcompany.listeners;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.API;
import com.rabbitcompany.utils.Message;
import com.rabbitcompany.utils.Number;
import com.rabbitcompany.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
        int amount = Integer.parseInt(line2);

        String str_material = line3.replace(" ", "_").toUpperCase();
        Material material = Material.getMaterial(str_material);
        if(material == null) return;

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

        Player player = event.getPlayer();
        NumberFormat formatter = new DecimalFormat("#" + Settings.cryptos.get(currency).format);

        if(line1.equals(Message.chat(cryptoCurrency.getConf().getString("shop_buy_success"))) || line1.equals(Message.chat(cryptoCurrency.getConf().getString("admin_shop_buy_success")))){
            if(player.getInventory().firstEmpty() == -1){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_full_inventory"));
                return;
            }

            if(owner.equals("AdminShop")){
                switch(API.sendCrypto(event.getPlayer().getName(), currency, price)){
                    case 2:
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(currency).format(Settings.cryptos.get(currency).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", currency.toUpperCase()));
                        return;
                    case 3:
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(currency).format(Settings.cryptos.get(currency).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", currency.toUpperCase()));
                        return;
                    case 6:
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_not_enough").replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", currency.toUpperCase()));
                        return;
                    case 10:
                        player.getInventory().addItem(new ItemStack(material, amount));
                        player.updateInventory();
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_bought").replace("{amount}", ""+amount).replace("{material}", line3).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", formatter.format(price) + " " + currency.toUpperCase()));
                        return;
                }
                return;
            }

            String ownerName = API.getName(owner);
            if(ownerName == null) return;

            if(!(event.getClickedBlock().getBlockData() instanceof Directional)) return;
            Directional directional = (Directional) event.getClickedBlock().getBlockData();
            if(event.getClickedBlock().getRelative(directional.getFacing().getOppositeFace()).getType() != Material.CHEST) return;

            Chest chest = (Chest) event.getClickedBlock().getRelative(directional.getFacing().getOppositeFace()).getState();
            int hasAmount = 0;
            for(ItemStack item : chest.getInventory().getStorageContents()){
                if(item == null) continue;
                if(item.getType() != material) continue;
                hasAmount += item.getAmount();
                if(hasAmount >= amount) break;
            }

            if(hasAmount < amount){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_empty_chest"));
                return;
            }

            switch (API.sendCrypto(player.getName(), ownerName, currency, price)){
                case 2:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(currency).format(Settings.cryptos.get(currency).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", currency.toUpperCase()));
                    return;
                case 3:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(currency).format(Settings.cryptos.get(currency).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", currency.toUpperCase()));
                    return;
                case 6:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_not_enough").replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", currency.toUpperCase()));
                    return;
                case 10:
                    chest.getInventory().removeItem(new ItemStack(material, amount));
                    player.getInventory().addItem(new ItemStack(material, amount));
                    player.updateInventory();
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_bought").replace("{amount}", ""+amount).replace("{material}", line3).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", formatter.format(price) + " " + currency.toUpperCase()));
                    return;
            }
            return;
        }

        int hasAmount = 0;
        for(ItemStack item : player.getInventory().getStorageContents()){
            if(item == null) continue;
            if(item.getType() != material) continue;
            hasAmount += item.getAmount();
            if(hasAmount >= amount) break;
        }

        if(hasAmount < amount){
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_not_enough_items").replace("{material}", line3));
            return;
        }

        if(owner.equals("AdminShop")){
            switch (API.giveCrypto(player.getName(), currency, price)){
                case 2:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(currency).format(Settings.cryptos.get(currency).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", currency.toUpperCase()));
                    return;
                case 3:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(currency).format(Settings.cryptos.get(currency).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", currency.toUpperCase()));
                    return;
                case 10:
                    player.getInventory().removeItem(new ItemStack(material, amount));
                    player.updateInventory();
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_sold").replace("{amount}", ""+amount).replace("{material}", line3).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", formatter.format(price) + " " + currency.toUpperCase()));
                    return;
            }
            return;
        }

        String ownerName = API.getName(owner);
        if(ownerName == null) return;

        if(!(event.getClickedBlock().getBlockData() instanceof Directional)) return;
        Directional directional = (Directional) event.getClickedBlock().getBlockData();
        if(event.getClickedBlock().getRelative(directional.getFacing().getOppositeFace()).getType() != Material.CHEST) return;

        Chest chest = (Chest) event.getClickedBlock().getRelative(directional.getFacing().getOppositeFace()).getState();
        if(chest.getInventory().firstEmpty() == -1){
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_full_chest"));
            return;
        }

        switch (API.sendCrypto(ownerName, player.getName(), currency, price)){
            case 2:
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(currency).format(Settings.cryptos.get(currency).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", currency.toUpperCase()));
                return;
            case 3:
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(currency).format(Settings.cryptos.get(currency).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", currency.toUpperCase()));
                return;
            case 6:
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_not_enough_money").replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", currency.toUpperCase()).replace("{player}", ownerName));
                return;
            case 10:
                chest.getInventory().addItem(new ItemStack(material, amount));
                player.getInventory().removeItem(new ItemStack(material, amount));
                player.updateInventory();
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_sold").replace("{amount}", ""+amount).replace("{material}", line3).replace("{color}", Message.chat(Settings.cryptos.get(currency).color)).replace("{crypto}", formatter.format(price) + " " + currency.toUpperCase()));
                return;
        }

    }

}
