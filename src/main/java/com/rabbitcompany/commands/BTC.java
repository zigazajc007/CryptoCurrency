package com.rabbitcompany.commands;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.Message;
import com.rabbitcompany.utils.Number;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class BTC implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if(args.length == 0){
            player.sendMessage(Message.chat("&7Listing commands:"));
            player.sendMessage(Message.chat(""));
            player.sendMessage(Message.chat("&b/btc balance &7- Check btc balance"));
            player.sendMessage(Message.chat("&b/btc send <player> <amount> &7- Send btc to player"));
            player.sendMessage(Message.chat("&b/btc give <player> <amount> &7- Give btc to player"));
            player.sendMessage(Message.chat(""));
        }else if(args.length == 1){
            if(args[0].equals("reload")){
                if(player.hasPermission("cryptocurrency.reload")){
                    CryptoCurrency.getInstance().loadYamls();
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.chat("&aPlugin is reloaded."));
                }else{
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "permission"));
                }
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
                    int balance = CryptoCurrency.getInstance().getBtcw().getInt(player.getUniqueId().toString());
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_balance").replace("{amount}", String.valueOf(balance)));
                }
            }else if(args[0].equals("help")){
                player.sendMessage(Message.chat("&7Listing commands:"));
                player.sendMessage(Message.chat(""));
                player.sendMessage(Message.chat("&b/btc balance &7- Check btc balance"));
                player.sendMessage(Message.chat("&b/btc send <player> <amount> &7- Send btc to player"));
                player.sendMessage(Message.chat("&b/btc give <player> <amount> &7- Give btc to player"));
                player.sendMessage(Message.chat(""));
            }else{
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_btc_arguments"));
            }
        }else if(args.length == 2){
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_btc_arguments"));
        }else if(args.length == 3){
            if(args[0].equals("send")){
                if(Number.isNumeric(args[2])){
                    int amount_send = Integer.parseInt(args[2]);
                    if(CryptoCurrency.conn != null){

                    }else{
                        Player target = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                        if(target != null) {
                            int balance = CryptoCurrency.getInstance().getBtcw().getInt(player.getUniqueId().toString());
                            int target_balance = CryptoCurrency.getInstance().getBtcw().getInt(target.getUniqueId().toString());
                            if (balance >= amount_send) {
                                CryptoCurrency.getInstance().getBtcw().set(player.getUniqueId().toString(), balance - amount_send);
                                CryptoCurrency.getInstance().getBtcw().set(target.getUniqueId().toString(), target_balance + amount_send);
                                CryptoCurrency.getInstance().saveBtcw();
                                if(player != target) {
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_send_success").replace("{amount}", String.valueOf(amount_send)).replace("{player}", target.getName()));
                                }
                                target.sendMessage(Message.getMessage(target.getUniqueId(), "prefix") + Message.getMessage(target.getUniqueId(), "message_btc_receive_success").replace("{player}", player.getName()).replace("{amount}", String.valueOf(amount_send)));
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
                        int amount_send = Integer.parseInt(args[2]);
                        if (CryptoCurrency.conn != null) {

                        } else {
                            Player target = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                            if (target != null) {
                                int target_balance = CryptoCurrency.getInstance().getBtcw().getInt(target.getUniqueId().toString());
                                CryptoCurrency.getInstance().getBtcw().set(target.getUniqueId().toString(), target_balance + amount_send);
                                CryptoCurrency.getInstance().saveBtcw();
                                if(player != target){
                                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_btc_send_success").replace("{amount}", String.valueOf(amount_send)).replace("{player}", target.getName()));
                                }
                                target.sendMessage(Message.getMessage(target.getUniqueId(), "prefix") + Message.getMessage(target.getUniqueId(), "message_btc_receive_success").replace("{player}", player.getName()).replace("{amount}", String.valueOf(amount_send)));
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
            }else{
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_btc_arguments"));
            }
        }

        return true;
    }
}
