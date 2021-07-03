package com.rabbitcompany.commands;

import com.rabbitcompany.CryptoCurrency;
import com.rabbitcompany.utils.*;
import com.rabbitcompany.utils.Number;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CryptoCMD extends Command {

    public CryptoCMD(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String crypto_type, @NotNull String[] args) {
        double price = Settings.cryptos.get(crypto_type).price;
        double balance;
        YamlConfiguration wallet = Settings.cryptos.get(crypto_type).wallet;

        if(!(sender instanceof Player)){

            if(args.length >= 4){
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "wrong_arguments").replace("{crypto}", crypto_type));
                return true;
            }

            if(args.length == 0 || (args.length == 1 && args[0].equals("help"))){
                Message.Help(sender, crypto_type);
                return true;
            }

            if(args.length == 1 && args[0].equals("reload")){
                CryptoCurrency.getInstance().loadYamls();
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.chat("&aPlugin is reloaded."));
                return true;
            }

            if(args.length == 1 && (args[0].equals("price") || args[0].equals("worth"))){
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_price").replace("{amount}", "1").replace("{money}", API.moneyFormatter.format(price)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(args.length == 2 && (args[0].equals("price") || args[0].equals("worth"))){

                if (!Number.isNumeric(args[1])) {
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_price").replace("{amount}", "1").replace("{money}", API.moneyFormatter.format(price)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                }
                double amount = Double.parseDouble(args[1]);

                if(amount > Settings.cryptos.get(crypto_type).maximum){
                    amount = Settings.cryptos.get(crypto_type).maximum;
                }else if(amount < Settings.cryptos.get(crypto_type).minimum){
                    amount = Settings.cryptos.get(crypto_type).minimum;
                }

                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_price").replace("{amount}", API.getFormatter(crypto_type).format(amount)).replace("{money}", API.moneyFormatter.format(price * amount)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(args.length == 1 && (args[0].equals("balance") || args[0].equals("bal") || args[0].equals("check") || args[0].equals("info"))){
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_balance").replace("{amount}", "∞").replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(args.length == 2 && (args[0].equals("balance") || args[0].equals("bal") || args[0].equals("check") || args[0].equals("info"))){
                String target = ChatColor.stripColor(args[1]);
                double target_balance = API.getBalance(target, crypto_type);
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_balance_player").replace("{player}", target).replace("{amount}", API.getFormatter(crypto_type).format(target_balance)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(args.length == 2 && args[0].equals("sell")){
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + "&cYou can't sell " + crypto_type + " in console.");
                return true;
            }

            if(args.length == 2 && args[0].equals("buy")){
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + "&cYou can't buy " + crypto_type + " in console.");
                return true;
            }

            if(args.length == 3 && (args[0].equals("send") || args[0].equals("give"))){
                if(!Number.isNumeric(args[2])){
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_number").replace("{number}", args[2]));
                    return true;
                }

                double amount_send = Double.parseDouble(args[2]);

                String target = ChatColor.stripColor(args[1]);
                Player target_player = Bukkit.getServer().getPlayer(target);

                if(!API.hasWallet(target)){
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_player").replace("{player}", target));
                    return true;
                }

                if(API.giveCrypto(target, crypto_type, amount_send)){
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_send_success").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{player}", target).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    if(target_player != null && target_player.isOnline()){
                        target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_receive_success").replace("{player}", "Console").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    }
                    return true;
                }

                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_player").replace("{player}", target));
            }

            if(args.length == 3 && args[0].equals("take")){

                if (!Number.isNumeric(args[2])) {
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_number").replace("{number}", args[2]));
                    return true;
                }

                double amount_take = Double.parseDouble(args[2]);
                if(amount_take < Settings.cryptos.get(crypto_type).minimum) {
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                }

                if(amount_take > Settings.cryptos.get(crypto_type).maximum) {
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                }

                String target = ChatColor.stripColor(args[1]);
                Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
                if (CryptoCurrency.conn != null) {
                    if(target_player != null){
                        double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), crypto_type);
                        if(target_balance >= amount_take){
                            MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), API.getFormatter(crypto_type).format(target_balance-amount_take), crypto_type);
                        }else{
                            MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), "0", crypto_type);
                        }
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_taken_player").replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{player}", target_player.getName()).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_taken_target").replace("{player}", "Console").replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                    }

                    if(!MySql.isPlayerInDatabase(target, crypto_type)){
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_player").replace("{player}", target));
                        return true;
                    }

                    double target_balance = MySql.getPlayerBalance("null", target, crypto_type);
                    if(target_balance >= amount_take){
                        MySql.setPlayerBalance("null", target, API.getFormatter(crypto_type).format(target_balance-amount_take), crypto_type);
                    }else{
                        MySql.setPlayerBalance("null", target, "0", crypto_type);
                    }
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_taken_player").replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{player}", target).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
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
                Settings.cryptos.get(crypto_type).saveWallet();
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_taken_player").replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{player}", target_player.getName()).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_taken_target").replace("{player}", "Console").replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }
            sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "wrong_arguments").replace("{crypto}", crypto_type));
            return true;
        }

        Player player = (Player) sender;

        if(args.length >= 4){
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_arguments").replace("{crypto}", crypto_type));
            return true;
        }

        if(args.length == 0 || (args.length == 1 && args[0].equals("help"))){
            Message.Help(player, crypto_type);
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
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_price").replace("{amount}", "1").replace("{money}", API.moneyFormatter.format(price)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
            return true;
        }

        if(args.length == 2 && (args[0].equals("price") || args[0].equals("worth"))){

            if (!Number.isNumeric(args[1])) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_price").replace("{amount}", "1").replace("{money}", API.moneyFormatter.format(price)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }
            double amount = Double.parseDouble(args[1]);

            if(amount > Settings.cryptos.get(crypto_type).maximum){
                amount = Settings.cryptos.get(crypto_type).maximum;
            }else if(amount < Settings.cryptos.get(crypto_type).minimum){
                amount = Settings.cryptos.get(crypto_type).minimum;
            }

            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_price").replace("{amount}", API.getFormatter(crypto_type).format(amount)).replace("{money}", API.moneyFormatter.format(price * amount)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
            return true;
        }

        balance = (CryptoCurrency.conn != null) ? MySql.getPlayerBalance(player.getUniqueId().toString(), player.getName(), crypto_type) : wallet.getDouble(player.getUniqueId().toString());
        if(args.length == 1 && (args[0].equals("balance") || args[0].equals("bal") || args[0].equals("check") || args[0].equals("info"))){
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_balance").replace("{amount}", API.getFormatter(crypto_type).format(balance)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
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
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_balance").replace("{amount}", API.getFormatter(crypto_type).format(balance)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                    }
                    target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), crypto_type);
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_balance_player").replace("{player}", target_player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(target_balance)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                }

                if(!MySql.isPlayerInDatabase(target, crypto_type)){
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                    return true;
                }

                target_balance = MySql.getPlayerBalance("null", target, crypto_type);
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_balance_player").replace("{player}", target).replace("{amount}", API.getFormatter(crypto_type).format(target_balance)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(target_player == null){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                return true;
            }

            if(player.getName().equals(target_player.getName())){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_balance").replace("{amount}", API.getFormatter(crypto_type).format(balance)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }
            target_balance = wallet.getDouble(target_player.getUniqueId().toString());
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_balance_player").replace("{player}", target_player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(target_balance)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
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
            if(amount_sell < Settings.cryptos.get(crypto_type).minimum){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(amount_sell > Settings.cryptos.get(crypto_type).maximum){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            double money_price = amount_sell * price;
            if(balance < amount_sell){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_not_enough").replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(CryptoCurrency.conn != null) {
                MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), API.getFormatter(crypto_type).format(balance - amount_sell), crypto_type);
                CryptoCurrency.getEconomy().depositPlayer(player, money_price);
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_sell").replace("{amount}", API.getFormatter(crypto_type).format(amount_sell)).replace("{money}", API.moneyFormatter.format(money_price)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            wallet.set(player.getUniqueId().toString(), balance - amount_sell);
            Settings.cryptos.get(crypto_type).saveWallet();
            CryptoCurrency.getEconomy().depositPlayer(player, money_price);
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_sell").replace("{amount}", API.getFormatter(crypto_type).format(amount_sell)).replace("{money}", API.moneyFormatter.format(money_price)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
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
            if(amount_buy < Settings.cryptos.get(crypto_type).minimum){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(amount_buy > Settings.cryptos.get(crypto_type).maximum){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            double money_price = amount_buy * price;
            double player_balance = CryptoCurrency.getEconomy().getBalance(player);
            if(player_balance < money_price){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_money_not_enough"));
                return true;
            }

            if(CryptoCurrency.conn != null){
                MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), API.getFormatter(crypto_type).format(balance + amount_buy), crypto_type);
                CryptoCurrency.getEconomy().withdrawPlayer(player, money_price);
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_buy").replace("{amount}", API.getFormatter(crypto_type).format(amount_buy)).replace("{money}", API.moneyFormatter.format(money_price)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            wallet.set(player.getUniqueId().toString(), balance + amount_buy);
            Settings.cryptos.get(crypto_type).saveWallet();
            CryptoCurrency.getEconomy().withdrawPlayer(player, money_price);
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_buy").replace("{amount}", API.getFormatter(crypto_type).format(amount_buy)).replace("{money}", API.moneyFormatter.format(money_price)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
            return true;
        }

        if(args.length == 3 && args[0].equals("send")){
            if(player.getName().equals(ChatColor.stripColor(args[1]))){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_yourself").replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(!Number.isNumeric(args[2])){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[2]));
                return true;
            }

            double amount_send = Double.parseDouble(args[2]);
            if(amount_send < Settings.cryptos.get(crypto_type).minimum) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(amount_send > Settings.cryptos.get(crypto_type).maximum) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            String target = ChatColor.stripColor(args[1]);
            Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
            if(CryptoCurrency.conn != null){

                if(target_player != null){

                    if(player.getName().equals(target_player.getName())){
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_yourself").replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                    }

                    double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), crypto_type);
                    if(balance < amount_send){
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_not_enough").replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                    }

                    MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), API.getFormatter(crypto_type).format(balance - amount_send), crypto_type);
                    MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), API.getFormatter(crypto_type).format(target_balance + amount_send), crypto_type);
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_success").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{player}", target_player.getName()).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_receive_success").replace("{player}", player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                }

                if(MySql.isPlayerInDatabase(target, crypto_type)){
                    double target_balance = MySql.getPlayerBalance("null", target, crypto_type);

                    if(balance < amount_send){
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_not_enough").replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                    }

                    MySql.setPlayerBalance(player.getUniqueId().toString(), player.getName(), API.getFormatter(crypto_type).format(balance - amount_send), crypto_type);
                    MySql.setPlayerBalance("null", target, API.getFormatter(crypto_type).format(target_balance + amount_send), crypto_type);
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_success").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{player}", target).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    if(player.getName().equals(target)) player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_receive_success").replace("{player}", player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
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
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_yourself").replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            double target_balance = wallet.getDouble(target_player.getUniqueId().toString());
            if(balance < amount_send){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_not_enough").replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            wallet.set(player.getUniqueId().toString(), balance - amount_send);
            wallet.set(target_player.getUniqueId().toString(), target_balance + amount_send);
            Settings.cryptos.get(crypto_type).saveWallet();
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_success").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{player}", target_player.getName()).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
            target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_receive_success").replace("{player}", player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
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
            if(amount_send < Settings.cryptos.get(crypto_type).minimum){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(amount_send > Settings.cryptos.get(crypto_type).maximum){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
            if(CryptoCurrency.conn != null){
                if(target_player != null){
                    double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), crypto_type);
                    MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), API.getFormatter(crypto_type).format(target_balance + amount_send), crypto_type);
                    if(player != target_player){
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_success").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{player}", target_player.getName()).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    }
                    target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_receive_success").replace("{player}", player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                }

                String target = ChatColor.stripColor(args[1]);
                if(!MySql.isPlayerInDatabase(target, crypto_type)){
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                    return true;
                }
                double target_balance = MySql.getPlayerBalance("null", target, crypto_type);
                MySql.setPlayerBalance("null", target, API.getFormatter(crypto_type).format(target_balance + amount_send), crypto_type);
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_success").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{player}", target).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(target_player == null){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", args[1]));
                return true;
            }

            double target_balance = wallet.getDouble(target_player.getUniqueId().toString());
            wallet.set(target_player.getUniqueId().toString(), target_balance + amount_send);
            Settings.cryptos.get(crypto_type).saveWallet();
            if (player != target_player) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_success").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{player}", target_player.getName()).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
            }
            target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_receive_success").replace("{player}", player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
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
            if(amount_take < Settings.cryptos.get(crypto_type).minimum) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            if(amount_take > Settings.cryptos.get(crypto_type).maximum) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            String target = ChatColor.stripColor(args[1]);
            Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));
            if (CryptoCurrency.conn != null) {
                if(target_player != null){
                    double target_balance = MySql.getPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), crypto_type);
                    if(target_balance >= amount_take){
                        MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), API.getFormatter(crypto_type).format(target_balance-amount_take), crypto_type);
                    }else{
                        MySql.setPlayerBalance(target_player.getUniqueId().toString(), target_player.getName(), "0", crypto_type);
                    }
                    if (player != target_player) {
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_taken_player").replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{player}", target_player.getName()).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    }
                    target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_taken_target").replace("{player}", player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                }

                if(!MySql.isPlayerInDatabase(target, crypto_type)){
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                    return true;
                }

                double target_balance = MySql.getPlayerBalance("null", target, crypto_type);
                if(target_balance >= amount_take){
                    MySql.setPlayerBalance("null", target, API.getFormatter(crypto_type).format(target_balance-amount_take), crypto_type);
                }else{
                    MySql.setPlayerBalance("null", target, "0", crypto_type);
                }
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_taken_player").replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{player}", target).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
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
            Settings.cryptos.get(crypto_type).saveWallet();
            if (player != target_player) {
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_taken_player").replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{player}", target_player.getName()).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
            }
            target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_taken_target").replace("{player}", player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
            return true;
        }

        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_arguments").replace("{crypto}", crypto_type));
        return true;
    }
}
