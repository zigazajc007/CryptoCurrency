package com.rabbitcompany.listeners;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.Message;
import com.rabbitcompany.utils.Number;
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
                            if(line4.contains("BCH")) currency = "bch";
                            if(line4.contains("ETH")) currency = "eth";
                            if(line4.contains("ETC")) currency = "etc";
                            if(line4.contains("DOGE")) currency = "doge";
                            if(line4.contains("LTC")) currency = "ltc";
                            if(line4.contains("USDT")) currency = "usdt";
                            event.getPlayer().sendMessage("Before: " + line4);
                            line4 = line4.replaceAll("[^0-9.]", "");
                            event.getPlayer().sendMessage("After: " + line4);
                            if(Number.isNumeric(line4)){
                                double price = Double.parseDouble(line4);
                                NumberFormat formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString(currency + "_format", "0.0000"));
                                event.setLine(0, Message.chat(cryptoCurrency.getConf().getString("shop_" + type + "_player_color") + event.getPlayer().getName()));
                                event.setLine(1, Message.chat(cryptoCurrency.getConf().getString("shop_" + type + "_amount_color") + amount));
                                event.setLine(2, Message.chat(cryptoCurrency.getConf().getString("shop_" + type + "_material_color") + line3.toLowerCase()));
                                switch (currency){
                                    case "bch":
                                        event.setLine(3, Message.chat("&a" + formatter.format(price) + " BCH"));
                                        break;
                                    case "eth":
                                        event.setLine(3, Message.chat("&b" + formatter.format(price) + " ETH"));
                                        break;
                                    case "etc":
                                        event.setLine(3, Message.chat("&a" + formatter.format(price) + " ETC"));
                                        break;
                                    case "doge":
                                        event.setLine(3, Message.chat("&6" + formatter.format(price) + " DOGE"));
                                        break;
                                    case "ltc":
                                        event.setLine(3, Message.chat("&7" + formatter.format(price) + " LTC"));
                                        break;
                                    case "usdt":
                                        event.setLine(3, Message.chat("&2" + formatter.format(price) + " USDT"));
                                        break;
                                    default:
                                        event.setLine(3, Message.chat("&6" + formatter.format(price) + " BTC"));
                                        break;
                                }
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
