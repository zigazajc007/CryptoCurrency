package com.rabbitcompany;

import com.rabbitcompany.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
		if (Settings.cryptos.containsKey(command.getName())) {
			List<String> completions = new ArrayList<>();

			if (args.length == 1) {
				if (commandSender.hasPermission("cryptocurrency.balance")) completions.add("balance");
				if (commandSender.hasPermission("cryptocurrency.price")) completions.add("price");
				if (commandSender.hasPermission("cryptocurrency.send")) completions.add("send");
				if (commandSender.hasPermission("cryptocurrency.buy")) completions.add("buy");
				if (commandSender.hasPermission("cryptocurrency.sell")) completions.add("sell");
				if (commandSender.hasPermission("cryptocurrency.give")) completions.add("give");
				if (commandSender.hasPermission("cryptocurrency.take")) completions.add("take");
				if (commandSender.hasPermission("cryptocurrency.reload")) completions.add("reload");
			} else if (args.length == 2) {
				switch (args[0]) {
					case "send":
						if (commandSender.hasPermission("cryptocurrency.send")) {
							for (Player all : Bukkit.getServer().getOnlinePlayers()) {
								completions.add(all.getName());
							}
						}
						break;
					case "give":
						if (commandSender.hasPermission("cryptocurrency.give")) {
							for (Player all : Bukkit.getServer().getOnlinePlayers()) {
								completions.add(all.getName());
							}
						}
						break;
					case "take":
						if (commandSender.hasPermission("cryptocurrency.take")) {
							for (Player all : Bukkit.getServer().getOnlinePlayers()) {
								completions.add(all.getName());
							}
						}
						break;
					case "bal":
					case "balance":
					case "check":
					case "info":
						if (commandSender.hasPermission("cryptocurrency.balance.other")) {
							for (Player all : Bukkit.getServer().getOnlinePlayers()) {
								completions.add(all.getName());
							}
						}
						break;
				}
			}
			return completions;
		}
		return null;
	}
}
