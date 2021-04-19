package com.rabbitcompany.commands;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.API;
import com.rabbitcompany.utils.Message;
import com.rabbitcompany.utils.MySql;
import com.rabbitcompany.utils.Number;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ETH implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        NumberFormat formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("eth_format", "0.000"));
        NumberFormat money_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("money_format", "###,###.00"));

        if(args.length == 0){
            Message.Help(player, "eth");
        }else if(args.length == 1){
            if(args[0].equals("reload")){
                if(player.hasPermission("cryptocurrency.reload")){
                    CryptoCurrency.getInstance().loadYamls();
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.chat("&aPlugin is reloaded."));
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "permission"));
                }
            }else if(args[0].equals("price")){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_price").replace("{amount}", money_formatter.format(API.eth_price)));
            }else if(args[0].equals("balance") || args[0].equals("bal") || args[0].equals("check") || args[0].equals("info")){
                if(CryptoCurrency.conn != null){
                    double balance = MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(),"eth");
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_balance").replace("{amount}", formatter.format(balance)));
                }else{
                    double balance = CryptoCurrency.getInstance().getEthw().getDouble(player.getUniqueId().toString());
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_balance").replace("{amount}", formatter.format(balance)));
                }
            }else if(args[0].equals("help")){
                Message.Help(player, "eth");
            }else{
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_eth_arguments"));
            }
        }else if(args.length == 2){
            if(args[0].equals("sell")){
                if(CryptoCurrency.vault) {
                    if (Number.isNumeric(args[1])) {
                        double amount_sell = Double.parseDouble(args[1]);
                        if(amount_sell >= CryptoCurrency.getInstance().getConf().getDouble("eth_minimum")) {
                            double money_price = amount_sell * API.eth_price;
                            if (CryptoCurrency.conn != null) {
                                double balance = MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "eth");
                                if (balance >= amount_sell) {
                                    MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), formatter.format(balance - amount_sell), "eth");
                                    CryptoCurrency.getEconomy().depositPlayer(player, money_price);
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_sell").replace("{eth}", formatter.format(amount_sell)).replace("{money}", formatter.format(money_price)));
                                } else {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_not_enough"));
                                }
                            } else {
                                double balance = CryptoCurrency.getInstance().getEthw().getDouble(player.getUniqueId().toString());
                                if (balance >= amount_sell) {
                                    CryptoCurrency.getInstance().getEthw().set(player.getUniqueId().toString(), balance - amount_sell);
                                    CryptoCurrency.getInstance().saveEthw();
                                    CryptoCurrency.getEconomy().depositPlayer(player, money_price);
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_sell").replace("{eth}", formatter.format(amount_sell)).replace("{money}", formatter.format(money_price)));
                                } else {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_not_enough"));
                                }
                            }
                        }else{
                            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble("eth_minimum"))));
                        }
                    } else {
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[1]));
                    }
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "vault_required"));
                }
            }else if(args[0].equals("buy")){
                if(CryptoCurrency.vault) {
                    if (Number.isNumeric(args[1])) {
                        double amount_buy = Double.parseDouble(args[1]);
                        if(amount_buy >= CryptoCurrency.getInstance().getConf().getDouble("eth_minimum")) {
                            double money_price = amount_buy * API.eth_price;
                            double balance = CryptoCurrency.getEconomy().getBalance(player);
                            if (CryptoCurrency.conn != null) {
                                double eth_balance = MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "eth");
                                if (balance >= money_price) {
                                    MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), formatter.format(eth_balance + amount_buy), "eth");
                                    CryptoCurrency.getEconomy().withdrawPlayer(player, money_price);
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_buy").replace("{eth}", formatter.format(amount_buy)).replace("{money}", formatter.format(money_price)));
                                } else {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_money_not_enough"));
                                }
                            } else {
                                double eth_balance = CryptoCurrency.getInstance().getEthw().getDouble(player.getUniqueId().toString());
                                if (balance >= money_price) {
                                    CryptoCurrency.getInstance().getEthw().set(player.getUniqueId().toString(), eth_balance + amount_buy);
                                    CryptoCurrency.getInstance().saveEthw();
                                    CryptoCurrency.getEconomy().withdrawPlayer(player, money_price);
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_buy").replace("{eth}", formatter.format(amount_buy)).replace("{money}", formatter.format(money_price)));
                                } else {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_money_not_enough"));
                                }
                            }
                        }else{
                            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble("eth_minimum"))));
                        }
                    } else {
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[1]));
                    }
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "vault_required"));
                }
            }else{
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_eth_arguments"));
            }
        }else if(args.length == 3){
            if(args[0].equals("send")){
                if(Number.isNumeric(args[2])){
                    double amount_send = Double.parseDouble(args[2]);
                    if(amount_send >= CryptoCurrency.getInstance().getConf().getDouble("eth_minimum")) {
                        if (CryptoCurrency.conn != null) {
                            String target = ChatColor.stripColor(args[1]);
                            Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                            if (target_player != null) {
                                double balance = MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "eth");
                                double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), "eth");
                                if (balance >= amount_send) {
                                    if (player != target_player) {
                                        MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), formatter.format(balance - amount_send), "eth");
                                        MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), formatter.format(target_balance + amount_send), "eth");
                                    }
                                    if (!player.getName().equals(target)) {
                                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target_player.getName()));
                                    }
                                    target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_eth_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
                                } else {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_not_enough"));
                                }
                            } else {
                                if (MySql.isPlayerInDatabase(target, "eth")) {
                                    double balance = MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), "eth");
                                    double target_balance = MySql.getPlayerBalance("null", target, "eth");
                                    if (balance >= amount_send) {
                                        MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), formatter.format(balance - amount_send), "eth");
                                        MySql.setPlayerBalance("null", target, formatter.format(target_balance + amount_send), "eth");
                                        if (!player.getName().equals(target)) {
                                            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target));
                                        } else {
                                            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
                                        }
                                    } else {
                                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_not_enough"));
                                    }
                                } else {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                                }
                            }
                        } else {
                            Player target = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                            if (target != null) {
                                double balance = CryptoCurrency.getInstance().getEthw().getDouble(player.getUniqueId().toString());
                                double target_balance = CryptoCurrency.getInstance().getEthw().getDouble(target.getUniqueId().toString());
                                if (balance >= amount_send) {
                                    CryptoCurrency.getInstance().getEthw().set(player.getUniqueId().toString(), balance - amount_send);
                                    CryptoCurrency.getInstance().getEthw().set(target.getUniqueId().toString(), target_balance + amount_send);
                                    CryptoCurrency.getInstance().saveEthw();
                                    if (player != target) {
                                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target.getName()));
                                    }
                                    target.sendMessage(Message.getMessage(target.getUniqueId(), "prefix") + Message.getMessage(target.getUniqueId(), "message_eth_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
                                } else {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_not_enough"));
                                }
                            } else {
                                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                            }
                        }
                    }else{
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble("eth_minimum"))));
                    }
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[2]));
                }
            }else if(args[0].equals("give")){
                if(player.hasPermission("cryptocurrency.give")) {
                    if (Number.isNumeric(args[2])) {
                        double amount_send = Double.parseDouble(args[2]);
                        if(amount_send >= CryptoCurrency.getInstance().getConf().getDouble("eth_minimum")) {
                            if (CryptoCurrency.conn != null) {
                                Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                                if(target_player != null){
                                    double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), "eth");
                                    MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), formatter.format(target_balance + amount_send), "eth");
                                    if(player != target_player){
                                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target_player.getName()));
                                    }
                                    target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_eth_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
                                }else{
                                    String target = ChatColor.stripColor(args[1]);
                                    if(MySql.isPlayerInDatabase(target, "eth")){
                                        double target_balance = MySql.getPlayerBalance("null", target, "eth");
                                        MySql.setPlayerBalance("null", target, formatter.format(target_balance + amount_send), "eth");
                                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target));
                                    }else{
                                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                                    }
                                }
                            } else {
                                Player target = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                                if (target != null) {
                                    double target_balance = CryptoCurrency.getInstance().getEthw().getDouble(target.getUniqueId().toString());
                                    CryptoCurrency.getInstance().getEthw().set(target.getUniqueId().toString(), target_balance + amount_send);
                                    CryptoCurrency.getInstance().saveEthw();
                                    if (player != target) {
                                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target.getName()));
                                    }
                                    target.sendMessage(Message.getMessage(target.getUniqueId(), "prefix") + Message.getMessage(target.getUniqueId(), "message_eth_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
                                } else {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                                }
                            }
                        }else{
                            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble("eth_minimum"))));
                        }
                    } else {
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[2]));
                    }
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "permission"));
                }
            }else if(args[0].equals("take")) {
                if(player.hasPermission("cryptocurrency.take")){
                    if (Number.isNumeric(args[2])) {
                        double amount_take = Double.parseDouble(args[2]);
                        if(amount_take >= CryptoCurrency.getInstance().getConf().getDouble("eth_minimum")) {
                            if (CryptoCurrency.conn != null) {
                                Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                                if(target_player != null){
                                    double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), "eth");
                                    if(target_balance >= amount_take){
                                        MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), formatter.format(target_balance-amount_take), "eth");
                                    }else{
                                        MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), "0", "eth");
                                    }
                                    if (player != target_player) {
                                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_taken_player").replace("{amount}", formatter.format(amount_take)).replace("{player}", target_player.getName()));
                                    }
                                    target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_eth_taken_target").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_take)));
                                }else{
                                    String target = ChatColor.stripColor(args[1]);
                                    if(MySql.isPlayerInDatabase(target, "eth")){
                                        double target_balance = MySql.getPlayerBalance("null", target, "eth");
                                        if(target_balance >= amount_take){
                                            MySql.setPlayerBalance("null", target, formatter.format(target_balance-amount_take), "eth");
                                        }else{
                                            MySql.setPlayerBalance("null", target, "0", "eth");
                                        }
                                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_taken_player").replace("{amount}", formatter.format(amount_take)).replace("{player}", target));
                                    }else{
                                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                                    }
                                }
                            } else {
                                Player target = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                                if (target != null) {
                                    double target_balance = CryptoCurrency.getInstance().getEthw().getDouble(target.getUniqueId().toString());
                                    if (target_balance >= amount_take) {
                                        CryptoCurrency.getInstance().getEthw().set(target.getUniqueId().toString(), target_balance - amount_take);
                                    } else {
                                        CryptoCurrency.getInstance().getEthw().set(target.getUniqueId().toString(), 0);
                                    }
                                    CryptoCurrency.getInstance().saveEthw();
                                    if (player != target) {
                                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_taken_player").replace("{amount}", formatter.format(amount_take)).replace("{player}", target.getName()));
                                    }
                                    target.sendMessage(Message.getMessage(target.getUniqueId(), "prefix") + Message.getMessage(target.getUniqueId(), "message_eth_taken_target").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_take)));
                                } else {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                                }
                            }
                        }else{
                            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_eth_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble("eth_minimum"))));
                        }
                    }else{
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[2]));
                    }
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "permission"));
                }
            }else{
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_eth_arguments"));
            }
        }else{
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_eth_arguments"));
        }
        return true;
    }

}
