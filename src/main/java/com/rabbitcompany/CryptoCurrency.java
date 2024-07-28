package com.rabbitcompany;

import com.rabbitcompany.commands.CryptoCMD;
import com.rabbitcompany.listeners.blockBreakEvent.BreakShopListener;
import com.rabbitcompany.listeners.blockBreakEvent.CryptoMiningListener;
import com.rabbitcompany.listeners.blockExplodeEvent.BlockExplodeListener;
import com.rabbitcompany.listeners.blockExplodeEvent.EntityExplodeListener;
import com.rabbitcompany.listeners.playerInteractEvent.SignShopListener;
import com.rabbitcompany.listeners.playerJoinEvent.CreateWalletListener;
import com.rabbitcompany.listeners.signChangeEvent.CreateSignShopListener;
import com.rabbitcompany.listeners.tabCompleteEvent.CryptosTabCompleteListener;
import com.rabbitcompany.utils.*;
import com.zaxxer.hikari.HikariDataSource;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public final class CryptoCurrency extends JavaPlugin {

	public static String new_version = null;
	//SQL
	public static HikariDataSource hikari;
	public static Connection conn = null;
	public static boolean vault = false;
	private static CryptoCurrency instance;
	//VaultAPI
	private static Economy econ = null;
	private final YamlConfiguration conf = new YamlConfiguration();
	private final YamlConfiguration crypto = new YamlConfiguration();
	private final YamlConfiguration signs = new YamlConfiguration();
	private final YamlConfiguration mater = new YamlConfiguration();
	private final YamlConfiguration mining = new YamlConfiguration();
	private final YamlConfiguration engl = new YamlConfiguration();
	private final YamlConfiguration russi = new YamlConfiguration();
	private final YamlConfiguration spanni = new YamlConfiguration();
	String username = "%%__USERNAME__%%";
	String user_id = "%%__USER__%%";
	//Config
	private File co = null;
	//Crypto Currencies
	private File cc = null;
	//Sign Shop
	private File ss = null;
	//Materials
	private File ma = null;
	//Mining
	private File mi = null;
	//English
	private File en = null;
	//Russian
	private File ru = null;
	//Spanish
	private File sp = null;

	public static Economy getEconomy() {
		return econ;
	}

	public static CryptoCurrency getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		this.co = new File(getDataFolder(), "config.yml");
		this.cc = new File(getDataFolder(), "cryptocurrencies.yml");
		this.ss = new File(getDataFolder(), "signshops.yml");
		this.ma = new File(getDataFolder(), "materials.yml");
		this.mi = new File(getDataFolder(), "mining.yml");
		this.en = new File(getDataFolder(), "Languages/English.yml");
		this.ru = new File(getDataFolder(), "Languages/Russian.yml");
		this.sp = new File(getDataFolder(), "Languages/Spanish.yml");

		mkdir();
		loadYamls();

		//Initialize all crypto currencies
		Set<String> crypto_keys = getCrypto().getKeys(false);
		for (String crypto_str : crypto_keys) {
			Settings.cryptos.put(crypto_str, new Crypto(getCrypto().getString(crypto_str + ".name"), crypto_str, getCrypto().getString(crypto_str + ".color", "6"), getCrypto().getString(crypto_str + ".format", "0.0000"), getCrypto().getDouble(crypto_str + ".maximum", 100), getCrypto().getDouble(crypto_str + ".minimum", 0.0001), getCrypto().getDouble(crypto_str + ".max_supply", 21000000), getCrypto().getDouble(crypto_str + ".price_multiplier", 1)));
		}

		//VaultAPI
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			vault = setupEconomy();
		}

		//Metrics
		new Metrics(this, 11090);

		//SQL
		if (getConf().getBoolean("mysql", false)) {
			try {
				hikari = new HikariDataSource();
				hikari.setMaximumPoolSize(10);
				hikari.setJdbcUrl("jdbc:mysql://" + getConf().getString("mysql_host") + ":" + getConf().getString("mysql_port") + "/" + getConf().getString("mysql_database"));
				hikari.setUsername(getConf().getString("mysql_user"));
				hikari.setPassword(getConf().getString("mysql_password"));
				hikari.addDataSourceProperty("useSSL", getConf().getString("mysql_useSSL"));
				hikari.addDataSourceProperty("cachePrepStmts", "true");
				hikari.addDataSourceProperty("prepStmtCacheSize", "250");
				hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

				conn = hikari.getConnection();
				for (String crypto_str : crypto_keys) {
					conn.createStatement().execute("CREATE TABLE IF NOT EXISTS cryptocurrency_" + crypto_str + "(uuid char(36) NOT NULL PRIMARY KEY, username varchar(25) NOT NULL, balance double);");
				}
				conn.close();
			} catch (SQLException e) {
				conn = null;
			}
		}

		//Listeners
		new CreateWalletListener(this);
		new CryptosTabCompleteListener(this);
		if (getConfig().getBoolean("mining", false)) {
			new CryptoMiningListener(this);

			//Initialize all crypto mining
			for (String block_str : getMining().getKeys(false)) {
				String block = block_str.toUpperCase(Locale.ROOT);

				for(String crypto_str : getMining().getConfigurationSection(block_str).getKeys(false)) {
					String crypto = crypto_str.toLowerCase(Locale.ROOT);

					if (!API.isCryptoEnabled(crypto)) continue;

					Mining[] miningArray = Settings.mining.get(block);

					List<Mining> miningList;
					if (miningArray == null) {
						miningList = new ArrayList<>();
					} else {
						miningList = new ArrayList<>(Arrays.asList(miningArray));
					}

					miningList.add(new Mining(
						crypto,
						getMining().getDouble(block_str + "." + crypto_str + ".max", 0),
						getMining().getDouble(block_str + "." + crypto_str + ".min", 0),
						getMining().getDouble(block_str + "." + crypto_str + ".chance", 0),
						getMining().getInt(block_str + "." + crypto_str + ".message_type", 1),
						getMining().getString(block_str + "." + crypto_str + ".message", null)
					));

					Settings.mining.put(block_str, miningList.toArray(new Mining[0]));
				}
			}
		}

		//Sign Shop - Quick Fix
		if (Version.isAtLeast(Version.MC1_16)) {
			if (getConf().getBoolean("shop_enabled", true)) {
				new CreateSignShopListener(this);
				new SignShopListener(this);
				new BreakShopListener(this);
				new BlockExplodeListener(this);
				new EntityExplodeListener(this);
			}
		}

		try {
			final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			bukkitCommandMap.setAccessible(true);
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

			for (String crypto_str : crypto_keys) {
				commandMap.register(crypto_str, new CryptoCMD(crypto_str));
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		//PlaceholderAPI
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new Placeholders().register();
		}

		//Update Checker
		new UpdateChecker(this, 51).getVersion(updater_version -> {
			if (!getDescription().getVersion().equalsIgnoreCase(updater_version)) new_version = updater_version;
			info("&aEnabling");
		});

		if (getConf().getInt("crypto_exchange", 1) == 2) {
			API.getBinanceArrayPositions();
			API.startBinancePriceFetcher();
		} else {
			API.startCoinbasePriceFetcher(API.getAPICurrency());
		}

		API.startSupplyCalculator();

	}

	@Override
	public void onDisable() {
		info("&4Disabling");

		//SQL
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException ignored) {
			}
		}
	}

	//VaultAPI
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) return false;
		econ = rsp.getProvider();
		return true;
	}

	private void mkdir() {
		if (!this.co.exists()) saveResource("config.yml", false);
		if (!this.cc.exists()) saveResource("cryptocurrencies.yml", false);
		if (!this.ss.exists()) saveResource("signshops.yml", false);
		if (!this.ma.exists()) saveResource("materials.yml", false);
		if (!this.mi.exists()) saveResource("mining.yml", false);
		if (!this.en.exists()) saveResource("Languages/English.yml", false);
		if (!this.ru.exists()) saveResource("Languages/Russian.yml", false);
		if (!this.sp.exists()) saveResource("Languages/Spanish.yml", false);
	}

	public void loadYamls() {

		try {
			this.conf.load(this.co);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {
			this.crypto.load(this.cc);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {
			this.signs.load(this.ss);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {
			this.mater.load(this.ma);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {
			this.mining.load(this.mi);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {
			this.engl.load(this.en);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {
			this.russi.load(this.ru);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {
			this.spanni.load(this.sp);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public YamlConfiguration getConf() {
		return this.conf;
	}

	public YamlConfiguration getCrypto() {
		return this.crypto;
	}

	public YamlConfiguration getSignShops() {
		return this.signs;
	}

	public YamlConfiguration getMaterials() {
		return this.mater;
	}

	public YamlConfiguration getMining() {
		return this.mining;
	}

	public YamlConfiguration getEngl() {
		return this.engl;
	}

	public YamlConfiguration getRussi() {
		return this.russi;
	}

	public YamlConfiguration getSpanni() {
		return this.spanni;
	}

	public void saveSignShops() {
		try {
			this.signs.save(ss);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void info(String message) {
		String text = "\n\n";
		text += "&8[]========[" + message + " &cCryptoCurrency&8]========[]\n";
		text += "&8|\n";
		text += "&8| &cInformation:\n";
		text += "&8|\n";
		text += "&8|   &9Name: &bCryptoCurrency\n";
		text += "&8|   &9Developer: &bBlack1_TV\n";
		if (!username.contains("%%__")) {
			text += "&8|   &9Plugin owner: &b" + username + "\n";
		} else if (!user_id.contains("%%__")) {
			text += "&8|   &9Plugin owner: &b" + user_id + "\n";
		} else {
			text += "&8|   &9Plugin owner: &4&lCRACKED\n";
		}
		if (new_version != null) {
			text += "&8|   &9Version: &b" + getDescription().getVersion() + " (&6update available&b)\n";
		} else {
			text += "&8|   &9Version: &b" + getDescription().getVersion() + "\n";
		}
		text += "&8|   &9Website: &bhttps://rabbit-company.com\n";
		text += "&8|\n";
		text += "&8| &cSponsors:\n";
		text += "&8|\n";
		text += "&8|   &9- &6https://rabbitserverlist.com\n";
		text += "&8|\n";
		text += "&8| &cSupport:\n";
		text += "&8|\n";
		text += "&8|   &9Discord: &bziga.zajc007\n";
		text += "&8|   &9Mail: &bziga.zajc007@gmail.com\n";
		text += "&8|   &9Discord: &bhttps://discord.gg/hUNymXX\n";
		text += "&8|\n";
		text += "&8[]=========================================[]\n";

		Bukkit.getConsoleSender().sendMessage(Message.chat(text));
	}
}
