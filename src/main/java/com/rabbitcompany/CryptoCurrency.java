package com.rabbitcompany;

import com.rabbitcompany.commands.Crypto;
import com.rabbitcompany.listeners.PlayerJoinListener;
import com.rabbitcompany.utils.API;
import com.rabbitcompany.utils.Message;
import com.rabbitcompany.utils.Placeholders;
import com.zaxxer.hikari.HikariDataSource;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public final class CryptoCurrency extends JavaPlugin {

    private static CryptoCurrency instance;

    public static String new_version = null;

    //SQL
    public static HikariDataSource hikari;
    public static Connection conn = null;

    //VaultAPI
    private static Economy econ = null;
    public static boolean vault = false;

    //Config
    private File co = null;
    private final YamlConfiguration conf = new YamlConfiguration();

    //English
    private File en = null;
    private final YamlConfiguration engl = new YamlConfiguration();

    //BTC Wallets
    private File bw = null;
    private final YamlConfiguration btcw = new YamlConfiguration();

    //BCH Wallets
    private File bcw = null;
    private final YamlConfiguration bchw = new YamlConfiguration();

    //ETH Wallets
    private File ew = null;
    private final YamlConfiguration ethw = new YamlConfiguration();

    //ETC Wallets
    private File etw = null;
    private final YamlConfiguration etcw = new YamlConfiguration();

    //DOGE Wallets
    private File dw = null;
    private final YamlConfiguration dogew = new YamlConfiguration();

    //LTC Wallets
    private File lw = null;
    private final YamlConfiguration ltcw = new YamlConfiguration();

    //USDT Wallets
    private File uw = null;
    private final YamlConfiguration usdtw = new YamlConfiguration();

    @Override
    public void onEnable() {
        instance = this;
        this.co = new File(getDataFolder(), "config.yml");
        this.en = new File(getDataFolder(), "Languages/English.yml");
        this.bw = new File(getDataFolder(), "Wallets/btc_wallets.yml");
        this.bcw = new File(getDataFolder(), "Wallets/bch_wallets.yml");
        this.ew = new File(getDataFolder(), "Wallets/eth_wallets.yml");
        this.etw = new File(getDataFolder(), "Wallets/etc_wallets.yml");
        this.dw = new File(getDataFolder(), "Wallets/doge_wallets.yml");
        this.lw = new File(getDataFolder(), "Wallets/ltc_wallets.yml");
        this.uw = new File(getDataFolder(), "Wallets/usdt_wallets.yml");

        mkdir();
        loadYamls();

        //VaultAPI
        if (getServer().getPluginManager().getPlugin("Vault") != null){
            vault = setupEconomy();
        }

        //Metrics
        new Metrics(this, 11090);

        //SQL
        if(getConf().getBoolean("mysql", false)){
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
                conn.createStatement().execute("CREATE TABLE IF NOT EXISTS cryptocurrency_btc(uuid char(36) NOT NULL PRIMARY KEY, username varchar(25) NOT NULL, balance double);");
                conn.createStatement().execute("CREATE TABLE IF NOT EXISTS cryptocurrency_bch(uuid char(36) NOT NULL PRIMARY KEY, username varchar(25) NOT NULL, balance double);");
                conn.createStatement().execute("CREATE TABLE IF NOT EXISTS cryptocurrency_eth(uuid char(36) NOT NULL PRIMARY KEY, username varchar(25) NOT NULL, balance double);");
                conn.createStatement().execute("CREATE TABLE IF NOT EXISTS cryptocurrency_etc(uuid char(36) NOT NULL PRIMARY KEY, username varchar(25) NOT NULL, balance double);");
                conn.createStatement().execute("CREATE TABLE IF NOT EXISTS cryptocurrency_doge(uuid char(36) NOT NULL PRIMARY KEY, username varchar(25) NOT NULL, balance double);");
                conn.createStatement().execute("CREATE TABLE IF NOT EXISTS cryptocurrency_ltc(uuid char(36) NOT NULL PRIMARY KEY, username varchar(25) NOT NULL, balance double);");
                conn.createStatement().execute("CREATE TABLE IF NOT EXISTS cryptocurrency_usdt(uuid char(36) NOT NULL PRIMARY KEY, username varchar(25) NOT NULL, balance double);");
                conn.close();
            } catch (SQLException e) {
                conn = null;
            }
        }

        //Listeners
        new PlayerJoinListener(this);

        //Commands
        this.getCommand("btc").setExecutor(new Crypto());
        this.getCommand("btc").setTabCompleter(new TabCompletion());
        this.getCommand("bch").setExecutor(new Crypto());
        this.getCommand("bch").setTabCompleter(new TabCompletion());
        this.getCommand("eth").setExecutor(new Crypto());
        this.getCommand("eth").setTabCompleter(new TabCompletion());
        this.getCommand("etc").setExecutor(new Crypto());
        this.getCommand("etc").setTabCompleter(new TabCompletion());
        this.getCommand("doge").setExecutor(new Crypto());
        this.getCommand("doge").setTabCompleter(new TabCompletion());
        this.getCommand("ltc").setExecutor(new Crypto());
        this.getCommand("ltc").setTabCompleter(new TabCompletion());
        this.getCommand("usdt").setExecutor(new Crypto());
        this.getCommand("usdt").setTabCompleter(new TabCompletion());

        //PlaceholderAPI
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Placeholders().register();
        }

        //Update Checker
        new UpdateChecker(this, 49).getVersion(updater_version -> {
            if (!getDescription().getVersion().equalsIgnoreCase(updater_version)) new_version = updater_version;
            info("&aEnabling");
        });

        API.startPriceFetcher(getConf().getString("api_currency", "USD"));
    }

    @Override
    public void onDisable() {
        info("&4Disabling");

        //SQL
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException ignored) { }
        }
    }

    //VaultAPI
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }

    private void mkdir(){

        if(!this.co.exists()){
            saveResource("config.yml", false);
        }

        if (!this.en.exists()) {
            saveResource("Languages/English.yml", false);
        }

        if (!this.bw.exists()) {
            saveResource("Wallets/btc_wallets.yml", false);
        }

        if (!this.bcw.exists()) {
            saveResource("Wallets/bch_wallets.yml", false);
        }

        if (!this.ew.exists()) {
            saveResource("Wallets/eth_wallets.yml", false);
        }

        if (!this.etw.exists()) {
            saveResource("Wallets/etc_wallets.yml", false);
        }

        if (!this.dw.exists()) {
            saveResource("Wallets/doge_wallets.yml", false);
        }

        if (!this.lw.exists()) {
            saveResource("Wallets/ltc_wallets.yml", false);
        }

        if (!this.uw.exists()) {
            saveResource("Wallets/usdt_wallets.yml", false);
        }
    }

    public void loadYamls(){

        try{
            this.conf.load(this.co);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            this.engl.load(this.en);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            this.btcw.load(this.bw);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            this.bchw.load(this.bcw);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            this.ethw.load(this.ew);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            this.etcw.load(this.etw);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            this.dogew.load(this.dw);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            this.ltcw.load(this.lw);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            this.usdtw.load(this.uw);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConf() { return this.conf; }
    public YamlConfiguration getEngl() { return this.engl; }
    public YamlConfiguration getBtcw() { return this.btcw; }
    public YamlConfiguration getBchw() { return this.bchw; }
    public YamlConfiguration getEthw() { return this.ethw; }
    public YamlConfiguration getEtcw() { return this.etcw; }
    public YamlConfiguration getDogew() { return this.dogew; }
    public YamlConfiguration getLtcw() { return this.ltcw; }
    public YamlConfiguration getUsdtw() { return this.usdtw; }

    public void saveBtcw() {
        try {
            this.btcw.save(this.bw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBchw() {
        try {
            this.bchw.save(this.bcw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveEthw() {
        try {
            this.ethw.save(this.ew);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveEtcw() {
        try {
            this.etcw.save(this.etw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDogew() {
        try {
            this.dogew.save(this.dw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLtcw() {
        try {
            this.ltcw.save(this.lw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUsdtw(){
        try {
            this.usdtw.save(this.uw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void info(String message){
        Bukkit.getConsoleSender().sendMessage(Message.chat(""));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8[]=====[" + message + " &bCryptoCurrency&8]=====[]"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8| &cInformation:"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|   &9Name: &bCryptoCurrency"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|   &9Developer: &bBlack1_TV"));
        if(new_version != null){
            Bukkit.getConsoleSender().sendMessage(Message.chat("&8|   &9Version: &b" + getDescription().getVersion() + " (&6update available&b)"));
        }else{
            Bukkit.getConsoleSender().sendMessage(Message.chat("&8|   &9Version: &b" + getDescription().getVersion()));
        }
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|   &9Premium: &bhttps://rabbit-company.com"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8| &cSupport:"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|   &9Discord: &bCrazy Rabbit#0001"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|   &9Mail: &bziga.zajc007@gmail.com"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|   &9Discord: &bhttps://discord.gg/hUNymXX"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|"));
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8[]=====================================[]"));
        Bukkit.getConsoleSender().sendMessage(Message.chat(""));
    }

    public static CryptoCurrency getInstance(){
        return instance;
    }
}
