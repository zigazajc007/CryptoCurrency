package com.rabbitcompany.listeners;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.Message;
import com.rabbitcompany.utils.Number;
import com.rabbitcompany.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SignChangeListener implements Listener {

    private CryptoCurrency cryptoCurrency;

    public SignChangeListener(CryptoCurrency plugin){
        cryptoCurrency = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event){
        String line1 = event.getLine(0);
        String line2 = event.getLine(1);
        String line3 = event.getLine(2);
        String line4 = event.getLine(3);

        if(line1 != null && line2 != null && line3 != null && line4 != null){
            if(line1.equals(cryptoCurrency.getConf().getString("shop_buy_format")) || line1.equals(cryptoCurrency.getConf().getString("shop_sell_format"))){
                String type = (line1.equals(cryptoCurrency.getConf().getString("shop_buy_format"))) ? "buy" : "sell";
                event.setLine(0, Message.chat(cryptoCurrency.getConf().getString("shop_" + type + "_error")));
                if(Number.isNumeric(line2)){
                    int amount = Integer.parseInt(line2);
                    if(!(amount < 1 || amount > 64)){
                        if(Material.getMaterial(line3.toUpperCase()) != null){
                            String currency = "btc";
                            for(String crypto : Settings.cryptos.keySet()) if(line4.contains(crypto.toUpperCase())) currency = crypto;
                            line4 = line4.replaceAll("[^0-9.]", "");
                            if(Number.isNumeric(line4)){
                                double price = Double.parseDouble(line4);
                                NumberFormat formatter = new DecimalFormat("#" + Settings.cryptos.get(currency).format);
                                event.setLine(0, Message.chat(cryptoCurrency.getConf().getString("shop_" + type + "_player_color") + event.getPlayer().getName()));
                                event.setLine(1, Message.chat(cryptoCurrency.getConf().getString("shop_" + type + "_amount_color") + amount));
                                String[] lin3 = line3.split("_");
                                StringBuilder sb = new StringBuilder();
                                for(String li3 : lin3) sb.append(Character.toUpperCase(li3.charAt(0))).append(li3.toLowerCase().substring(1)).append(" ");
                                sb.deleteCharAt(sb.length()-1);
                                event.setLine(2, Message.chat(cryptoCurrency.getConf().getString("shop_" + type + "_material_color") + sb));
                                event.setLine(3, Message.chat(Settings.cryptos.get(currency).color + formatter.format(price) + " " + currency.toUpperCase()));
                            }else{
                                event.setLine(3, Message.chat("&cInvalid price"));
                            }
                        }else{
                            event.setLine(2, Message.chat("&cInvalid material"));
                        }
                    }else{
                        event.setLine(1, Message.chat("&cInvalid area"));
                    }
                }else{
                    event.setLine(1, Message.chat("&cInvalid number"));
                }
            }
        }
    }

}
