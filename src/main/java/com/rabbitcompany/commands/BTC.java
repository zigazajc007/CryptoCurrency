package com.rabbitcompany.commands;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.API;
import com.rabbitcompany.utils.Message;
import com.rabbitcompany.utils.Number;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class BTC implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        NumberFormat formatter = new DecimalFormat("#0.00000000");

        if(args.length == 0){
            Message.Help(player, "btc");
        }else if(args.length == 1){
            if(args[0].equals("reload")){
                if(player.hasPermission("cryptocurrency.reload")){
                    CryptoCurrency.getInstance().loadYamls();
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.chat("&aPlugin is reloaded."));
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "permission"));
                }
            }else if(args[0].equals("price")){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_price").replace("{amount}", String.valueOf(Math.round(1 / API.getBTCFromPrice(CryptoCurrency.getInstance().getConf().getString("btc_api_currency"),"1")))));
            }else if(args[0].equals("balance") || args[0].equals("bal") || args[0].equals("check") || args[0].equals("info")){
                if(CryptoCurrency.conn != null){
                    try {
                        CryptoCurrency.mySQL.query("SELECT balance FROM cryptocurrency_btc WHERE uuid = '" + player.getUniqueId() + "';", results -> {
                            if (results != null) {
                                if(results.next()) {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_balance").replace("{amount}", results.getString("balance")));
                                }else{
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_balance").replace("{amount}", "0"));
                                }
                            } else {
                                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_balance").replace("{amount}", "0"));
                            }
                        });
                    } catch (SQLException e) {
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_lost_wallet"));
                    }
                }else{
                    double balance = CryptoCurrency.getInstance().getBtcw().getDouble(player.getUniqueId().toString());
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_balance").replace("{amount}", formatter.format(balance)));
                }
            }else if(args[0].equals("help")){
                Message.Help(player, "btc");
            }else{
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_btc_arguments"));
            }
        }else if(args.length == 2){
            if(args[0].equals("sell")){
                if(CryptoCurrency.vault) {
                    if (Number.isNumeric(args[1])) {
                        double amount_sell = Double.parseDouble(args[1]);
                        double money_price = amount_sell / API.getBTCFromPrice(CryptoCurrency.getInstance().getConf().getString("btc_api_currency"), "1");
                        if (CryptoCurrency.conn != null) {

                        } else {
                            double balance = CryptoCurrency.getInstance().getBtcw().getDouble(player.getUniqueId().toString());
                            if (balance >= amount_sell) {
                                CryptoCurrency.getInstance().getBtcw().set(player.getUniqueId().toString(), balance - amount_sell);
                                CryptoCurrency.getInstance().saveBtcw();
                                CryptoCurrency.getEconomy().depositPlayer(player, money_price);
                                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_sell").replace("{btc}", formatter.format(amount_sell)).replace("{money}", String.valueOf(Math.round(money_price))));
                            } else {
                                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_not_enough"));
                            }
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
                        double money_price = amount_buy / API.getBTCFromPrice(CryptoCurrency.getInstance().getConf().getString("btc_api_currency"), "1");
                        if (CryptoCurrency.conn != null) {

                        } else {
                            double balance = CryptoCurrency.getEconomy().getBalance(player);
                            double btc_balance = CryptoCurrency.getInstance().getBtcw().getDouble(player.getUniqueId().toString());
                            if (balance >= money_price) {
                                CryptoCurrency.getInstance().getBtcw().set(player.getUniqueId().toString(), btc_balance + amount_buy);
                                CryptoCurrency.getInstance().saveBtcw();
                                CryptoCurrency.getEconomy().withdrawPlayer(player, money_price);
                                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_buy").replace("{btc}", formatter.format(amount_buy)).replace("{money}", String.valueOf(Math.round(money_price))));
                            } else {
                                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_money_not_enough"));
                            }
                        }
                    } else {
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[1]));
                    }
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "vault_required"));
                }
            }else{
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_btc_arguments"));
            }
        }else if(args.length == 3){
            if(args[0].equals("send")){
                if(Number.isNumeric(args[2])){
                    double amount_send = Double.parseDouble(args[2]);
                    if(CryptoCurrency.conn != null){

                    }else{
                        Player target = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                        if(target != null) {
                            double balance = CryptoCurrency.getInstance().getBtcw().getDouble(player.getUniqueId().toString());
                            double target_balance = CryptoCurrency.getInstance().getBtcw().getDouble(target.getUniqueId().toString());
                            if (balance >= amount_send) {
                                CryptoCurrency.getInstance().getBtcw().set(player.getUniqueId().toString(), balance - amount_send);
                                CryptoCurrency.getInstance().getBtcw().set(target.getUniqueId().toString(), target_balance + amount_send);
                                CryptoCurrency.getInstance().saveBtcw();
                                if(player != target) {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target.getName()));
                                }
                                target.sendMessage(Message.getMessage(target.getUniqueId(), "prefix") + Message.getMessage(target.getUniqueId(), "message_btc_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
                            } else {
                                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_not_enough"));
                            }
                        }else{
                            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                        }
                    }
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[2]));
                }
            }else if(args[0].equals("give")){
                if(player.hasPermission("cryptocurrency.btc.give")) {
                    if (Number.isNumeric(args[2])) {
                        double amount_send = Double.parseDouble(args[2]);
                        if (CryptoCurrency.conn != null) {

                        } else {
                            Player target = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                            if (target != null) {
                                double target_balance = CryptoCurrency.getInstance().getBtcw().getDouble(target.getUniqueId().toString());
                                CryptoCurrency.getInstance().getBtcw().set(target.getUniqueId().toString(), target_balance + amount_send);
                                CryptoCurrency.getInstance().saveBtcw();
                                if(player != target){
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target.getName()));
                                }
                                target.sendMessage(Message.getMessage(target.getUniqueId(), "prefix") + Message.getMessage(target.getUniqueId(), "message_btc_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
                            } else {
                                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                            }
                        }
                    } else {
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[2]));
                    }
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "permission"));
                }
            }else if(args[0].equals("take")) {
                if(player.hasPermission("cryptocurrency.btc.take")){
                    if (Number.isNumeric(args[2])) {
                        double amount_take = Double.parseDouble(args[2]);
                        if (CryptoCurrency.conn != null) {

                        } else {
                            Player target = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                            if (target != null) {
                                double target_balance = CryptoCurrency.getInstance().getBtcw().getDouble(target.getUniqueId().toString());
                                if(target_balance >= amount_take){
                                    CryptoCurrency.getInstance().getBtcw().set(target.getUniqueId().toString(), target_balance - amount_take);
                                }else{
                                    CryptoCurrency.getInstance().getBtcw().set(target.getUniqueId().toString(), 0);
                                }
                                CryptoCurrency.getInstance().saveBtcw();
                                if(player != target){
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_taken_player").replace("{amount}", formatter.format(amount_take)).replace("{player}", target.getName()));
                                }
                                target.sendMessage(Message.getMessage(target.getUniqueId(), "prefix") + Message.getMessage(target.getUniqueId(), "message_btc_taken_target").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_take)));
                            } else {
                                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                            }
                        }
                    }else{
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[2]));
                    }
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "permission"));
                }
            }else{
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_btc_arguments"));
            }
        }else{
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_btc_arguments"));
        }
        return true;
    }
}
