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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;

public class Crypto implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        NumberFormat formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString(command.getName() + "_format", "0.0000"));
        NumberFormat money_formatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("money_format", "###,###.00"));
        double price;
        double balance;
        YamlConfiguration wallet;

        switch (command.getName()){
            case "bch":
                price = API.bch_price;
                wallet = CryptoCurrency.getInstance().getBchw();
                break;
            case "eth":
                price = API.eth_price;
                wallet = CryptoCurrency.getInstance().getEthw();
                break;
            case "etc":
                price = API.etc_price;
                wallet = CryptoCurrency.getInstance().getEtcw();
                break;
            case "doge":
                price = API.doge_price;
                wallet = CryptoCurrency.getInstance().getDogew();
                break;
            case "ltc":
                price = API.ltc_price;
                wallet = CryptoCurrency.getInstance().getLtcw();
                break;
            case "usdt":
                price = API.usdt_price;
                wallet = CryptoCurrency.getInstance().getUsdtw();
                break;
            default:
                price = API.btc_price;
                wallet = CryptoCurrency.getInstance().getBtcw();
                break;
        }

        if(!(sender instanceof Player)){

            if(args.length >= 4){
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "wrong_" + command.getName() + "_arguments"));
                return true;
            }

            if(args.length == 0 || (args.length == 1 && args[0].equals("help"))){
                Message.Help(sender, command.getName());
                return true;
            }

            if(args.length == 1 && args[0].equals("reload")){
                CryptoCurrency.getInstance().loadYamls();
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.chat("&aPlugin is reloaded."));
                return true;
            }

            if(args.length == 1 && (args[0].equals("price") || args[0].equals("worth"))){
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_price").replace("{amount}", "1").replace("{money}", money_formatter.format(price)));
                return true;
            }

            if(args.length == 2 && (args[0].equals("price") || args[0].equals("worth"))){

                if (!Number.isNumeric(args[1])) {
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_price").replace("{amount}", "1").replace("{money}", money_formatter.format(price)));
                    return true;
                }
                double amount = Double.parseDouble(args[1]);

                if(amount > CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum")){
                    amount = CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum");
                }else if(amount < CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum")){
                    amount = CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum");
                }

                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_price").replace("{amount}", formatter.format(amount)).replace("{money}", money_formatter.format(price * amount)));
                return true;
            }

            if(args.length == 1 && (args[0].equals("balance") || args[0].equals("bal") || args[0].equals("check") || args[0].equals("info"))){
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_balance").replace("{amount}", "∞"));
                return true;
            }

            if(args.length == 2 && (args[0].equals("balance") || args[0].equals("bal") || args[0].equals("check") || args[0].equals("info"))){
                String target = ChatColor.stripColor(args[1]);
                Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                double target_balance;
                if(CryptoCurrency.conn != null){
                    if(target_player != null){
                        target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), command.getName());
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_balance_player").replace("{player}", target_player.getName()).replace("{amount}", formatter.format(target_balance)));
                        return true;
                    }

                    if(!MySql.isPlayerInDatabase(target, command.getName())){
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_player").replace("{player}", target));
                        return true;
                    }

                    target_balance = MySql.getPlayerBalance("null", target, command.getName());
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_balance_player").replace("{player}", target).replace("{amount}", formatter.format(target_balance)));
                    return true;
                }

                if(target_player == null){
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_player").replace("{player}", args[1]));
                    return true;
                }

                target_balance = wallet.getDouble(target_player.getUniqueId().toString());
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_balance_player").replace("{player}", target_player.getName()).replace("{amount}", formatter.format(target_balance)));
                return true;
            }

            if(args.length == 2 && args[0].equals("sell")){
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + "&cYou can't sell " + command.getName() + " in console.");
                return true;
            }

            if(args.length == 2 && args[0].equals("buy")){
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + "&cYou can't buy " + command.getName() + " in console.");
                return true;
            }

            if(args.length == 3 && (args[0].equals("send") || args[0].equals("give"))){
                if(!Number.isNumeric(args[2])){
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_number").replace("{number}", args[2]));
                    return true;
                }

                double amount_send = Double.parseDouble(args[2]);
                if(amount_send < CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum")) {
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum"))));
                    return true;
                }

                if(amount_send > CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum")) {
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_maximum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum"))));
                    return true;
                }

                String target = ChatColor.stripColor(args[1]);
                Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                if(CryptoCurrency.conn != null){

                    if(target_player != null){
                        double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), command.getName());

                        MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), formatter.format(target_balance + amount_send), command.getName());
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target_player.getName()));
                        target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_" + command.getName() + "_receive_success").replace("{player}", "Console").replace("{amount}", formatter.format(amount_send)));
                        return true;
                    }

                    if(MySql.isPlayerInDatabase(target, command.getName())){
                        double target_balance = MySql.getPlayerBalance("null", target, command.getName());

                        MySql.setPlayerBalance("null", target, formatter.format(target_balance + amount_send), command.getName());
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target));
                        return true;
                    }

                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_player").replace("{player}", target));
                    return true;
                }

                if(target_player == null){
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_player").replace("{player}", args[1]));
                    return true;
                }

                double target_balance = wallet.getDouble(target_player.getUniqueId().toString());

                wallet.set(target_player.getUniqueId().toString(), target_balance + amount_send);
                saveWallet(command.getName());
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target_player.getName()));
                target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_" + command.getName() + "_receive_success").replace("{player}", "Console").replace("{amount}", formatter.format(amount_send)));
                return true;
            }

            if(args.length == 3 && args[0].equals("take")){

                if (!Number.isNumeric(args[2])) {
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_number").replace("{number}", args[2]));
                    return true;
                }

                double amount_take = Double.parseDouble(args[2]);
                if(amount_take < CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum")) {
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum"))));
                    return true;
                }

                if(amount_take > CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum")) {
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_maximum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum"))));
                    return true;
                }

                String target = ChatColor.stripColor(args[1]);
                Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                if (CryptoCurrency.conn != null) {
                    if(target_player != null){
                        double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), command.getName());
                        if(target_balance >= amount_take){
                            MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), formatter.format(target_balance-amount_take), command.getName());
                        }else{
                            MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), "0", command.getName());
                        }
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_taken_player").replace("{amount}", formatter.format(amount_take)).replace("{player}", target_player.getName()));
                        target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_" + command.getName() + "_taken_target").replace("{player}", "Console").replace("{amount}", formatter.format(amount_take)));
                        return true;
                    }

                    if(!MySql.isPlayerInDatabase(target, command.getName())){
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_player").replace("{player}", target));
                        return true;
                    }

                    double target_balance = MySql.getPlayerBalance("null", target, command.getName());
                    if(target_balance >= amount_take){
                        MySql.setPlayerBalance("null", target, formatter.format(target_balance-amount_take), command.getName());
                    }else{
                        MySql.setPlayerBalance("null", target, "0", command.getName());
                    }
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_taken_player").replace("{amount}", formatter.format(amount_take)).replace("{player}", target));
                    return true;
                }

                if(target_player == null){
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_player").replace("{player}", args[1]));
                    return true;
                }

                double target_balance = wallet.getDouble(target_player.getUniqueId().toString());
                if (target_balance >= amount_take) {
                    wallet.set(target_player.getUniqueId().toString(), target_balance - amount_take);
                } else {
                    wallet.set(target_player.getUniqueId().toString(), 0);
                }
                saveWallet(command.getName());
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_" + command.getName() + "_taken_player").replace("{amount}", formatter.format(amount_take)).replace("{player}", target_player.getName()));
                target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_" + command.getName() + "_taken_target").replace("{player}", "Console").replace("{amount}", formatter.format(amount_take)));
                return true;
            }
            sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "wrong_" + command.getName() + "_arguments"));
            return true;
        }

        Player player = (Player) sender;

        if(args.length >= 4){
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_" + command.getName() + "_arguments"));
            return true;
        }

        if(args.length == 0 || (args.length == 1 && args[0].equals("help"))){
            Message.Help(player, command.getName());
            return true;
        }

        if(args.length == 1 && args[0].equals("reload")){
            if(player.hasPermission("cryptocurrency.reload")){
                CryptoCurrency.getInstance().loadYamls();
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.chat("&aPlugin is reloaded."));
                return true;
            }
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "permission"));
            return true;
        }

        if(args.length == 1 && (args[0].equals("price") || args[0].equals("worth"))){
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_price").replace("{amount}", "1").replace("{money}", money_formatter.format(price)));
            return true;
        }

        if(args.length == 2 && (args[0].equals("price") || args[0].equals("worth"))){

            if (!Number.isNumeric(args[1])) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_price").replace("{amount}", "1").replace("{money}", money_formatter.format(price)));
                return true;
            }
            double amount = Double.parseDouble(args[1]);

            if(amount > CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum")){
                amount = CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum");
            }else if(amount < CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum")){
                amount = CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum");
            }

            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_price").replace("{amount}", formatter.format(amount)).replace("{money}", money_formatter.format(price * amount)));
            return true;
        }

        balance = (CryptoCurrency.conn != null) ? MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), command.getName()) : wallet.getDouble(player.getUniqueId().toString());
        if(args.length == 1 && (args[0].equals("balance") || args[0].equals("bal") || args[0].equals("check") || args[0].equals("info"))){
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_balance").replace("{amount}", formatter.format(balance)));
            return true;
        }

        if(args.length == 2 && (args[0].equals("balance") || args[0].equals("bal") || args[0].equals("check") || args[0].equals("info"))){

            if(!player.hasPermission("cryptocurrency.balance")){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "permission"));
                return true;
            }

            String target = ChatColor.stripColor(args[1]);
            Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
            double target_balance;
            if(CryptoCurrency.conn != null){
                if(target_player != null){
                    if(player.getName().equals(target_player.getName())){
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_balance").replace("{amount}", formatter.format(balance)));
                        return true;
                    }
                    target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), command.getName());
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_balance_player").replace("{player}", target_player.getName()).replace("{amount}", formatter.format(target_balance)));
                    return true;
                }

                if(!MySql.isPlayerInDatabase(target, command.getName())){
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                    return true;
                }

                target_balance = MySql.getPlayerBalance("null", target, command.getName());
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_balance_player").replace("{player}", target).replace("{amount}", formatter.format(target_balance)));
                return true;
            }

            if(target_player == null){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                return true;
            }

            if(player.getName().equals(target_player.getName())){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_balance").replace("{amount}", formatter.format(balance)));
                return true;
            }
            target_balance = wallet.getDouble(target_player.getUniqueId().toString());
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_balance_player").replace("{player}", target_player.getName()).replace("{amount}", formatter.format(target_balance)));
            return true;
        }

        if(args.length == 2 && args[0].equals("sell")){
            if(!CryptoCurrency.vault){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "vault_required"));
                return true;
            }

            if(!Number.isNumeric(args[1])){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[1]));
                return true;
            }

            double amount_sell = Double.parseDouble(args[1]);
            if(amount_sell < CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum")){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum"))));
                return true;
            }

            if(amount_sell < CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum")){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_maximum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum"))));
                return true;
            }

            double money_price = amount_sell * price;
            if(balance < amount_sell){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_not_enough"));
                return true;
            }

            if(CryptoCurrency.conn != null) {
                MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), formatter.format(balance - amount_sell), command.getName());
                CryptoCurrency.getEconomy().depositPlayer(player, money_price);
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_sell").replace("{amount}", formatter.format(amount_sell)).replace("{money}", formatter.format(money_price)));
                return true;
            }

            wallet.set(player.getUniqueId().toString(), balance - amount_sell);
            saveWallet(command.getName());
            CryptoCurrency.getEconomy().depositPlayer(player, money_price);
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_sell").replace("{amount}", formatter.format(amount_sell)).replace("{money}", formatter.format(money_price)));
            return true;
        }

        if(args.length == 2 && args[0].equals("buy")){
            if(!CryptoCurrency.vault){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "vault_required"));
                return true;
            }

            if(!Number.isNumeric(args[1])){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[1]));
                return true;
            }

            double amount_buy = Double.parseDouble(args[1]);
            if(amount_buy < CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum")){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum"))));
                return true;
            }

            if(amount_buy > CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum")){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_maximum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum"))));
                return true;
            }

            double money_price = amount_buy * price;
            double player_balance = CryptoCurrency.getEconomy().getBalance(player);
            if(player_balance < money_price){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_money_not_enough"));
                return true;
            }

            if(CryptoCurrency.conn != null){
                MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), formatter.format(balance + amount_buy), command.getName());
                CryptoCurrency.getEconomy().withdrawPlayer(player, money_price);
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_buy").replace("{amount}", formatter.format(amount_buy)).replace("{money}", formatter.format(money_price)));
                return true;
            }

            wallet.set(player.getUniqueId().toString(), balance + amount_buy);
            saveWallet(command.getName());
            CryptoCurrency.getEconomy().withdrawPlayer(player, money_price);
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_buy").replace("{amount}", formatter.format(amount_buy)).replace("{money}", formatter.format(money_price)));
            return true;
        }

        if(args.length == 3 && args[0].equals("send")){
            if(player.getName().equals(ChatColor.stripColor(args[1]))){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_send_yourself"));
                return true;
            }

            if(!Number.isNumeric(args[2])){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[2]));
                return true;
            }

            double amount_send = Double.parseDouble(args[2]);
            if(amount_send < CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum")) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum"))));
                return true;
            }

            if(amount_send > CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum")) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_maximum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum"))));
                return true;
            }

            String target = ChatColor.stripColor(args[1]);
            Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
            if(CryptoCurrency.conn != null){

                if(target_player != null){

                    if(player.getName().equals(target_player.getName())){
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_send_yourself"));
                        return true;
                    }

                    double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), command.getName());
                    if(balance < amount_send){
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_not_enough"));
                        return true;
                    }

                    MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), formatter.format(balance - amount_send), command.getName());
                    MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), formatter.format(target_balance + amount_send), command.getName());
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target_player.getName()));
                    target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_" + command.getName() + "_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
                    return true;
                }

                if(MySql.isPlayerInDatabase(target, command.getName())){
                    double target_balance = MySql.getPlayerBalance("null", target, command.getName());

                    if(balance < amount_send){
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_not_enough"));
                        return true;
                    }

                    MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), formatter.format(balance - amount_send), command.getName());
                    MySql.setPlayerBalance("null", target, formatter.format(target_balance + amount_send), command.getName());
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target));
                    if(player.getName().equals(target)) player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
                    return true;
                }

                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                return true;
            }

            if(target_player == null){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                return true;
            }

            if(player.getName().equals(target_player.getName())){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_send_yourself"));
                return true;
            }

            double target_balance = wallet.getDouble(target_player.getUniqueId().toString());
            if(balance < amount_send){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_not_enough"));
                return true;
            }

            wallet.set(player.getUniqueId().toString(), balance - amount_send);
            wallet.set(target_player.getUniqueId().toString(), target_balance + amount_send);
            saveWallet(command.getName());
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target_player.getName()));
            target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_" + command.getName() + "_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
            return true;
        }

        if(args.length == 3 && args[0].equals("give")){
            if(!player.hasPermission("cryptocurrency.give")){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "permission"));
                return true;
            }

            if (!Number.isNumeric(args[2])) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[2]));
                return true;
            }

            double amount_send = Double.parseDouble(args[2]);
            if(amount_send < CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum")){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum"))));
                return true;
            }

            if(amount_send > CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum")){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_maximum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum"))));
                return true;
            }

            Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
            if(CryptoCurrency.conn != null){
                if(target_player != null){
                    double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), command.getName());
                    MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), formatter.format(target_balance + amount_send), command.getName());
                    if(player != target_player){
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target_player.getName()));
                    }
                    target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_" + command.getName() + "_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
                    return true;
                }

                String target = ChatColor.stripColor(args[1]);
                if(!MySql.isPlayerInDatabase(target, command.getName())){
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                    return true;
                }
                double target_balance = MySql.getPlayerBalance("null", target, command.getName());
                MySql.setPlayerBalance("null", target, formatter.format(target_balance + amount_send), command.getName());
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target));
                return true;
            }

            if(target_player == null){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                return true;
            }

            double target_balance = CryptoCurrency.getInstance().getBtcw().getDouble(target_player.getUniqueId().toString());
            wallet.set(target_player.getUniqueId().toString(), target_balance + amount_send);
            saveWallet(command.getName());
            if (player != target_player) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_send_success").replace("{amount}", formatter.format(amount_send)).replace("{player}", target_player.getName()));
            }
            target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_" + command.getName() + "_receive_success").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_send)));
            return true;
        }

        if(args.length == 3 && args[0].equals("take")){
            if(!player.hasPermission("cryptocurrency.take")){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "permission"));
                return true;
            }

            if (!Number.isNumeric(args[2])) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[2]));
                return true;
            }

            double amount_take = Double.parseDouble(args[2]);
            if(amount_take < CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum")) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_minimum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_minimum"))));
                return true;
            }

            if(amount_take > CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum")) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_maximum").replace("{amount}", formatter.format(CryptoCurrency.getInstance().getConf().getDouble(command.getName() + "_maximum"))));
                return true;
            }

            String target = ChatColor.stripColor(args[1]);
            Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
            if (CryptoCurrency.conn != null) {
                if(target_player != null){
                    double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), command.getName());
                    if(target_balance >= amount_take){
                        MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), formatter.format(target_balance-amount_take), command.getName());
                    }else{
                        MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), "0", command.getName());
                    }
                    if (player != target_player) {
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_taken_player").replace("{amount}", formatter.format(amount_take)).replace("{player}", target_player.getName()));
                    }
                    target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_" + command.getName() + "_taken_target").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_take)));
                    return true;
                }

                if(!MySql.isPlayerInDatabase(target, command.getName())){
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                    return true;
                }

                double target_balance = MySql.getPlayerBalance("null", target, command.getName());
                if(target_balance >= amount_take){
                    MySql.setPlayerBalance("null", target, formatter.format(target_balance-amount_take), command.getName());
                }else{
                    MySql.setPlayerBalance("null", target, "0", command.getName());
                }
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_taken_player").replace("{amount}", formatter.format(amount_take)).replace("{player}", target));
                return true;
            }

            if(target_player == null){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                return true;
            }

            double target_balance = wallet.getDouble(target_player.getUniqueId().toString());
            if (target_balance >= amount_take) {
                wallet.set(target_player.getUniqueId().toString(), target_balance - amount_take);
            } else {
                wallet.set(target_player.getUniqueId().toString(), 0);
            }
            saveWallet(command.getName());
            if (player != target_player) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_" + command.getName() + "_taken_player").replace("{amount}", formatter.format(amount_take)).replace("{player}", target_player.getName()));
            }
            target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_" + command.getName() + "_taken_target").replace("{player}", player.getName()).replace("{amount}", formatter.format(amount_take)));
            return true;
        }

        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_" + command.getName() + "_arguments"));
        return true;
    }

    private void saveWallet(String crypto){
        switch (crypto){
            case "bch":
                CryptoCurrency.getInstance().saveBchw();
                break;
            case "eth":
                CryptoCurrency.getInstance().saveEthw();
                break;
            case "etc":
                CryptoCurrency.getInstance().saveEtcw();
                break;
            case "doge":
                CryptoCurrency.getInstance().saveDogew();
                break;
            case "ltc":
                CryptoCurrency.getInstance().saveLtcw();
                break;
            case "usdt":
                CryptoCurrency.getInstance().saveUsdtw();
            default:
                CryptoCurrency.getInstance().saveBtcw();
                break;
        }
    }
}
