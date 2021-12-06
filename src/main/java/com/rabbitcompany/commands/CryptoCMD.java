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
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.chat("&cYou can't sell " + crypto_type.toUpperCase() + " in console."));
                return true;
            }

            if(args.length == 2 && args[0].equals("buy")){
                sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.chat("&cYou can't buy " + crypto_type.toUpperCase() + " in console."));
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

                switch (API.giveCrypto(target, crypto_type, amount_send)){
                    case 2:
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                    case 3:
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                    case 4:
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_player").replace("{player}", target));
                        return true;
                    case 12:
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_max_supply_reached").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                    case 10:
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_send_success").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{player}", target).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        if(target_player != null && target_player.isOnline())
                            target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_receive_success").replace("{player}", "Console").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                }
                return true;
            }

            if(args.length == 3 && args[0].equals("take")){

                if (!Number.isNumeric(args[2])) {
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_number").replace("{number}", args[2]));
                    return true;
                }

                double amount_take = Double.parseDouble(args[2]);

                String target = ChatColor.stripColor(args[1]);
                Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));

                switch (API.takeCrypto(target, crypto_type, amount_take)){
                    case 2:
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                    case 3:
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                    case 4:
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "is_not_a_player").replace("{player}", target));
                        return true;
                    case 10:
                        sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_taken_player").replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{player}", target).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        if(target_player != null && target_player.isOnline())
                            target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_taken_target").replace("{player}", "Console").replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                        return true;
                }
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

        balance = API.getBalance(player.getName(), crypto_type);
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
            double target_balance = API.getBalance(target, crypto_type);
            player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_balance_player").replace("{player}", target).replace("{amount}", API.getFormatter(crypto_type).format(target_balance)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
            return true;
        }

        if(args.length == 2 && args[0].equals("sell")){

            if(!Number.isNumeric(args[1])){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[1]));
                return true;
            }

            double amount_sell = Double.parseDouble(args[1]);
            double money_price = amount_sell * price;

            switch (API.sellCrypto(player.getName(), crypto_type, amount_sell)){
                case 2:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 3:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 9:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "vault_required"));
                    return true;
                case 11:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_not_enough").replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 10:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_sell").replace("{amount}", API.getFormatter(crypto_type).format(amount_sell)).replace("{money}", API.moneyFormatter.format(money_price)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
            }
            return true;
        }

        if(args.length == 2 && args[0].equals("buy")){

            if(!Number.isNumeric(args[1])){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_number").replace("{number}", args[1]));
                return true;
            }

            double amount_buy = Double.parseDouble(args[1]);
            double money_price = amount_buy * price;

            switch (API.buyCrypto(player.getName(), crypto_type, amount_buy)){
                case 2:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 3:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 9:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "vault_required"));
                    return true;
                case 11:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_money_not_enough"));
                    return true;
                case 12:
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_max_supply_reached").replace("{amount}", API.getFormatter(crypto_type).format(amount_buy)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 10:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_buy").replace("{amount}", API.getFormatter(crypto_type).format(amount_buy)).replace("{money}", API.moneyFormatter.format(money_price)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
            }
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

            String target = ChatColor.stripColor(args[1]);
            Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));

            if(player.getName().equals(target)){
                player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_yourself").replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                return true;
            }

            switch(API.sendCrypto(player.getName(), target, crypto_type, amount_send)){
                case 2:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 3:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 5:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                    return true;
                case 6:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_not_enough").replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 10:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_success").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{player}", target).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    if(target_player != null && target_player.isOnline())
                        target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_receive_success").replace("{player}", player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
            }
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

            String target = ChatColor.stripColor(args[1]);
            Player target_player = Bukkit.getServer().getPlayer(target);

            switch (API.giveCrypto(target, crypto_type, amount_send)){
                case 2:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 3:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 4:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                    return true;
                case 12:
                    sender.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(UUID.randomUUID(), "message_max_supply_reached").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 10:
                    if(!player.getName().equals(target))
                        player.sendMessage(Message.getMessage(UUID.randomUUID(), "prefix") + Message.getMessage(player.getUniqueId(), "message_send_success").replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{player}", target).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    if(target_player != null && target_player.isOnline())
                        target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_receive_success").replace("{player}", player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(amount_send)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
            }
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

            String target = ChatColor.stripColor(args[1]);
            Player target_player = Bukkit.getServer().getPlayer(ChatColor.stripColor(args[1]));

            switch (API.takeCrypto(target, crypto_type, amount_take)){
                case 2:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_minimum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).minimum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 3:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_maximum").replace("{amount}", API.getFormatter(crypto_type).format(Settings.cryptos.get(crypto_type).maximum)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
                case 4:
                    player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "is_not_a_player").replace("{player}", target));
                    return true;
                case 10:
                    if(!player.getName().equals(target))
                        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "message_taken_player").replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{player}", target).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    if(target_player != null && target_player.isOnline())
                        target_player.sendMessage(Message.getMessage(target_player.getUniqueId(), "prefix") + Message.getMessage(target_player.getUniqueId(), "message_taken_target").replace("{player}", player.getName()).replace("{amount}", API.getFormatter(crypto_type).format(amount_take)).replace("{color}", Message.chat(Settings.cryptos.get(crypto_type).color)).replace("{crypto}", crypto_type.toUpperCase()));
                    return true;
            }
            return true;
        }

        player.sendMessage(Message.getMessage(player.getUniqueId(), "prefix") + Message.getMessage(player.getUniqueId(), "wrong_arguments").replace("{crypto}", crypto_type));
        return true;
    }
}
