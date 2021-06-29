package com.rabbitcompany.listeners;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteListener implements Listener {

    private CryptoCurrency cryptoCurrency;

    public TabCompleteListener(CryptoCurrency plugin){
        cryptoCurrency = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event){
        String[] args = event.getBuffer().replace("/", "").split(" ");
        if(Settings.cryptos.containsKey(args[0])){
            List<String> completions = new ArrayList<>();

            if(args.length == 1){
                completions.add("balance");
                completions.add("price");
                completions.add("send");
                completions.add("buy");
                completions.add("sell");

                if(event.getSender().hasPermission("cryptocurrency.give")) completions.add("give");
                if(event.getSender().hasPermission("cryptocurrency.take")) completions.add("take");
                if(event.getSender().hasPermission("cryptocurrency.reload")) completions.add("reload");
            }else if(args.length == 2){
                switch (args[1]) {
                    case "send":
                        for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                            completions.add(all.getName());
                        }
                        break;
                    case "give":
                        if (event.getSender().hasPermission("cryptocurrency.give")) {
                            for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                                completions.add(all.getName());
                            }
                        }
                        break;
                    case "take":
                        if (event.getSender().hasPermission("cryptocurrency.take")) {
                            for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                                completions.add(all.getName());
                            }
                        }
                        break;
                    case "bal":
                    case "balance":
                    case "check":
                    case "info":
                        if(event.getSender().hasPermission("cryptocurrency.balance")){
                            for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                                completions.add(all.getName());
                            }
                        }
                        break;
                }
            }
            event.setCompletions(completions);
        }
    }
}
