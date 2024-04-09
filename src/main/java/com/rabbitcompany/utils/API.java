package com.rabbitcompany.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rabbitcompany.CryptoCurrency;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class API {

	public static NumberFormat moneyFormatter = new DecimalFormat("#" + CryptoCurrency.getInstance().getConf().getString("money_format", "###,##0.00"));

	public static boolean isCryptoEnabled(String crypto) {
		return Settings.cryptos.get(crypto) != null;
	}

	public static List<String> getEnabledCryptos() {
		return new ArrayList<>(Settings.cryptos.keySet());
	}

	public static String getAPICurrency() {
		return CryptoCurrency.getInstance().getConf().getString("api_currency", "USD");
	}

	public static NumberFormat getFormatter(String crypto) {
		if (!isCryptoEnabled(crypto)) return new DecimalFormat("#0.0000");
		return new DecimalFormat("#" + Settings.cryptos.get(crypto).format);
	}

	public static boolean reloadCrypto(String crypto) {
		if (!isCryptoEnabled(crypto)) return false;
		Settings.cryptos.get(crypto).initializeWallet();
		return true;
	}

	public static void reloadConfigFiles() {
		CryptoCurrency.getInstance().loadYamls();
	}

	public static String getName(String uuid) {
		Player target = Bukkit.getPlayer(UUID.fromString(uuid));
		if (target != null && target.isOnline())
			return target.getName();
		return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
	}

	public static String getUUID(String player) {
		Player target = Bukkit.getPlayer(player);
		if (target != null && target.isOnline())
			return target.getUniqueId().toString();
		return Bukkit.getOfflinePlayer(player).getUniqueId().toString();
	}

	public static double getTotalAssets(String player) {
		double balance = 0;
		for (String crypto : Settings.cryptos.keySet())
			balance += getCryptoPrice(crypto, getBalance(player, crypto));
		return balance;
	}

	public static double getBalance(String player, String crypto) {
		if (!isCryptoEnabled(crypto)) return 0;
		String UUID = getUUID(player);
		if (CryptoCurrency.conn != null) return MySql.getPlayerBalance(UUID, player, crypto);
		return Settings.cryptos.get(crypto).wallet.getDouble(UUID, 0);
	}

	public static String getBalanceFormatted(String player, String crypto) {
		return getFormatter(crypto).format(getBalance(player, crypto));
	}

	public static double getCryptoPrice(String crypto) {
		if (!isCryptoEnabled(crypto)) return 0;
		return Settings.cryptos.get(crypto).price;
	}

	public static double getCryptoPrice(String crypto, double amount) {
		return getCryptoPrice(crypto) * amount;
	}

	public static String getCryptoPriceFormatted(String crypto) {
		return moneyFormatter.format(getCryptoPrice(crypto));
	}

	public static String getCryptoPriceFormatted(String crypto, double amount) {
		return moneyFormatter.format(getCryptoPrice(crypto, amount));
	}

	public static void calculateCryptoSupply(String crypto) {
		if (!isCryptoEnabled(crypto)) return;

		if (CryptoCurrency.conn != null) {
			Settings.cryptos.get(crypto).supply = MySql.getCryptoSupply(crypto);
		} else {
			double supply = 0;
			for (String player : Settings.cryptos.get(crypto).wallet.getKeys(false)) {
				supply += Settings.cryptos.get(crypto).wallet.getDouble(player, 0);
			}
			Settings.cryptos.get(crypto).supply = supply;
		}
	}

	public static double getCryptoSupply(String crypto) {
		if (!isCryptoEnabled(crypto)) return 0;
		return Settings.cryptos.get(crypto).supply;
	}

	public static double getCryptoMaxSupply(String crypto) {
		if (!isCryptoEnabled(crypto)) return 0;
		return Settings.cryptos.get(crypto).max_supply;
	}

	public static double getCryptoMarketCap(String crypto) {
		if (!isCryptoEnabled(crypto)) return 0;
		return getCryptoSupply(crypto) * getCryptoPrice(crypto);
	}

	public static int giveCrypto(String toPlayer, String crypto, double amount) {
		if (!isCryptoEnabled(crypto)) return 1;
		if (amount < Settings.cryptos.get(crypto).minimum) return 2;
		if (amount > Settings.cryptos.get(crypto).maximum) return 3;
		if (amount > (getCryptoMaxSupply(crypto) - getCryptoSupply(crypto))) return 12;
		String UUID = getUUID(toPlayer);
		double balance = getBalance(toPlayer, crypto);
		if (CryptoCurrency.conn != null) {
			MySql.setPlayerBalance(UUID, toPlayer, getFormatter(crypto).format(balance + amount), crypto);
			Settings.cryptos.get(crypto).supply += amount;
			return 10;
		}
		Settings.cryptos.get(crypto).wallet.set(UUID, balance + amount);
		Settings.cryptos.get(crypto).saveWallet();
		Settings.cryptos.get(crypto).supply += amount;
		return 10;
	}

	public static void cryptoMining(Player player, String material) {

		if (!Settings.mining.containsKey(material)) return;

		String UUID = player.getUniqueId().toString();
		String name = player.getName();

		Mining cryptoMining = Settings.mining.get(material);
		String crypto = cryptoMining.getCrypto();
		double amountAdded = cryptoMining.getAmount();
		double balance = getBalance(name, crypto);

		if (amountAdded > (getCryptoMaxSupply(crypto) - getCryptoSupply(crypto))) return;

		if (CryptoCurrency.conn != null) {
			MySql.setPlayerBalance(UUID, name, getFormatter(crypto).format(balance + amountAdded), crypto);
			Settings.cryptos.get(crypto).supply += amountAdded;
			return;
		}

		Settings.cryptos.get(crypto).wallet.set(UUID, balance + amountAdded);
		Settings.cryptos.get(crypto).saveWallet();
		Settings.cryptos.get(crypto).supply += amountAdded;
	}


	public static int buyCrypto(String player, String crypto, double amount) {
		if (!CryptoCurrency.vault) return 9;
		if (!isCryptoEnabled(crypto)) return 1;
		if (amount < Settings.cryptos.get(crypto).minimum) return 2;
		if (amount > Settings.cryptos.get(crypto).maximum) return 3;
		if (amount > (getCryptoMaxSupply(crypto) - getCryptoSupply(crypto))) return 12;
		String UUID = getUUID(player);
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
		double balance = getBalance(player, crypto);
		double player_balance = CryptoCurrency.getEconomy().getBalance(offlinePlayer);
		double money_price = getCryptoPrice(crypto, amount);
		if (player_balance < money_price) return 11;
		if (CryptoCurrency.conn != null) {
			MySql.setPlayerBalance(UUID, player, API.getFormatter(crypto).format(balance + amount), crypto);
			CryptoCurrency.getEconomy().withdrawPlayer(offlinePlayer, money_price);
			Settings.cryptos.get(crypto).supply += amount;
			return 10;
		}
		Settings.cryptos.get(crypto).wallet.set(UUID, balance + amount);
		Settings.cryptos.get(crypto).saveWallet();
		CryptoCurrency.getEconomy().withdrawPlayer(offlinePlayer, money_price);
		Settings.cryptos.get(crypto).supply += amount;
		return 10;
	}

	public static int takeCrypto(String fromPlayer, String crypto, double amount) {
		if (!isCryptoEnabled(crypto)) return 1;
		if (amount < Settings.cryptos.get(crypto).minimum) return 2;
		if (amount > Settings.cryptos.get(crypto).maximum) return 3;
		String UUID = getUUID(fromPlayer);
		double balance = getBalance(fromPlayer, crypto);
		if (balance - amount < 0) amount = balance;
		if (CryptoCurrency.conn != null) {
			MySql.setPlayerBalance(UUID, fromPlayer, getFormatter(crypto).format(balance - amount), crypto);
			Settings.cryptos.get(crypto).supply -= amount;
			return 10;
		}
		Settings.cryptos.get(crypto).wallet.set(UUID, balance - amount);
		Settings.cryptos.get(crypto).saveWallet();
		Settings.cryptos.get(crypto).supply -= amount;
		return 10;
	}

	public static int sellCrypto(String player, String crypto, double amount) {
		if (!CryptoCurrency.vault) return 9;
		if (!isCryptoEnabled(crypto)) return 1;
		if (amount < Settings.cryptos.get(crypto).minimum) return 2;
		if (amount > Settings.cryptos.get(crypto).maximum) return 3;
		String UUID = getUUID(player);
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
		double balance = getBalance(player, crypto);
		double money_price = getCryptoPrice(crypto, amount);
		if (balance < amount) return 11;
		if (CryptoCurrency.conn != null) {
			MySql.setPlayerBalance(UUID, player, API.getFormatter(crypto).format(balance - amount), crypto);
			CryptoCurrency.getEconomy().depositPlayer(offlinePlayer, money_price);
			Settings.cryptos.get(crypto).supply -= amount;
			return 10;
		}
		Settings.cryptos.get(crypto).wallet.set(UUID, balance - amount);
		Settings.cryptos.get(crypto).saveWallet();
		CryptoCurrency.getEconomy().depositPlayer(offlinePlayer, money_price);
		Settings.cryptos.get(crypto).supply -= amount;
		return 10;
	}

	public static int sendCrypto(String fromPlayer, String toPlayer, String crypto, double amount) {
		if (!isCryptoEnabled(crypto)) return 1;
		if (amount < Settings.cryptos.get(crypto).minimum) return 2;
		if (amount > Settings.cryptos.get(crypto).maximum) return 3;
		double fromBalance = getBalance(fromPlayer, crypto);

		if (fromBalance < amount) return 6;
		if (takeCrypto(fromPlayer, crypto, amount) != 10) return 7;
		if (giveCrypto(toPlayer, crypto, amount) != 10) return 8;
		return 10;
	}

	public static int sendCrypto(String fromPlayer, String crypto, double amount) {
		if (!isCryptoEnabled(crypto)) return 1;
		if (amount < Settings.cryptos.get(crypto).minimum) return 2;
		if (amount > Settings.cryptos.get(crypto).maximum) return 3;
		double fromBalance = getBalance(fromPlayer, crypto);

		if (fromBalance < amount) return 6;
		if (takeCrypto(fromPlayer, crypto, amount) != 10) return 7;
		return 10;
	}

	public static void startCoinbasePriceFetcher(String currency) {
		Bukkit.getScheduler().runTaskTimerAsynchronously(CryptoCurrency.getInstance(), () -> {
			try {
				String jsonS = new Scanner(new URL("https://api.coinbase.com/v2/exchange-rates?currency=" + currency).openStream(), "UTF-8").useDelimiter("\\A").next();

				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);

				for (String cur : Settings.cryptos.keySet()) {
					String value = jsonObject.getAsJsonObject("data").getAsJsonObject("rates").get(cur.toUpperCase()).getAsString();
					double price = (1 / Double.parseDouble(value)) * Settings.cryptos.get(cur).price_multiplier;
					Settings.cryptos.get(cur).previousPrice = Settings.cryptos.get(cur).price;
					Settings.cryptos.get(cur).price = price;

					if (!CryptoCurrency.getInstance().getConf().getBoolean("monitor_history", true)) continue;
					double max = Settings.cryptos.get(cur).history.getDouble(LocalDate.now() + ".max", Double.MIN_VALUE);
					double min = Settings.cryptos.get(cur).history.getDouble(LocalDate.now() + ".min", Double.MAX_VALUE);
					if (price > max)
						Settings.cryptos.get(cur).history.set(LocalDate.now() + ".max", Number.roundDouble(price, 2));
					if (price < min)
						Settings.cryptos.get(cur).history.set(LocalDate.now() + ".min", Number.roundDouble(price, 2));
					if (Settings.cryptos.get(cur).history.getDouble(LocalDate.now() + ".open", 0) == 0)
						Settings.cryptos.get(cur).history.set(LocalDate.now() + ".open", price);
					Settings.cryptos.get(cur).history.set(LocalDate.now() + ".close", price);
					Settings.cryptos.get(cur).history.set(LocalDate.now() + ".avg", Number.roundDouble((max + min) / 2.0, 2));
					Settings.cryptos.get(cur).saveHistory();
				}

			} catch (IOException ignored) {
			}
		}, 0L, 20L * CryptoCurrency.getInstance().getConf().getInt("price_fetch", 20));
	}

	public static void getBinanceArrayPositions() {
		try {
			String jsonS = new Scanner(new URL("https://api.binance.com/api/v3/ticker/price").openStream(), "UTF-8").useDelimiter("\\A").next();

			Gson gson = new Gson();
			JsonArray jsonArray = gson.fromJson(jsonS, JsonArray.class);

			for (String cur : Settings.cryptos.keySet()) {
				if (cur.equalsIgnoreCase("USDT")) {
					Settings.cryptos.get(cur).price = 1;
					continue;
				}
				for (int i = 0; i < jsonArray.getAsJsonArray().size(); i++) {
					if (jsonArray.getAsJsonArray().get(i).getAsJsonObject().get("symbol").getAsString().equals(cur.toUpperCase() + "USDT")) {
						Settings.cryptos.get(cur).binanceArrayPosition = i;
						break;
					}
				}
			}
		} catch (IOException ignored) {
		}
	}

	public static void startBinancePriceFetcher() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(CryptoCurrency.getInstance(), () -> {
			try {
				String jsonS = new Scanner(new URL("https://api.binance.com/api/v3/ticker/price").openStream(), "UTF-8").useDelimiter("\\A").next();

				Gson gson = new Gson();
				JsonArray jsonArray = gson.fromJson(jsonS, JsonArray.class);

				for (String cur : Settings.cryptos.keySet()) {
					if (cur.equalsIgnoreCase("USDT")) continue;
					if (Settings.cryptos.get(cur).binanceArrayPosition < 0) continue;
					String value = jsonArray.get(Settings.cryptos.get(cur).binanceArrayPosition).getAsJsonObject().get("price").getAsString();
					double price = Double.parseDouble(value) * Settings.cryptos.get(cur).price_multiplier;
					Settings.cryptos.get(cur).previousPrice = Settings.cryptos.get(cur).price;
					Settings.cryptos.get(cur).price = price;

					if (!CryptoCurrency.getInstance().getConf().getBoolean("monitor_history", true)) continue;
					double max = Settings.cryptos.get(cur).history.getDouble(LocalDate.now() + ".max", Double.MIN_VALUE);
					double min = Settings.cryptos.get(cur).history.getDouble(LocalDate.now() + ".min", Double.MAX_VALUE);
					if (price > max)
						Settings.cryptos.get(cur).history.set(LocalDate.now() + ".max", Number.roundDouble(price, 2));
					if (price < min)
						Settings.cryptos.get(cur).history.set(LocalDate.now() + ".min", Number.roundDouble(price, 2));
					if (Settings.cryptos.get(cur).history.getDouble(LocalDate.now() + ".open", 0) == 0)
						Settings.cryptos.get(cur).history.set(LocalDate.now() + ".open", price);
					Settings.cryptos.get(cur).history.set(LocalDate.now() + ".close", price);
					Settings.cryptos.get(cur).history.set(LocalDate.now() + ".avg", Number.roundDouble((max + min) / 2.0, 2));
					Settings.cryptos.get(cur).saveHistory();
				}
			} catch (IOException ignored) {
			}
		}, 0L, 20L * CryptoCurrency.getInstance().getConf().getInt("price_fetch", 20));
	}

	public static void startSupplyCalculator() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(CryptoCurrency.getInstance(), () -> {
			for (String cur : Settings.cryptos.keySet()) calculateCryptoSupply(cur);
		}, 0L, 20L * CryptoCurrency.getInstance().getConf().getInt("supply_calculator", 60));
	}

}
