package com.rabbitcompany;

import com.rabbitcompany.commands.CryptoCMD;
import com.rabbitcompany.listeners.*;
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
import java.util.Set;

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

    //Crypto Currencies
    private File cc = null;
    private final YamlConfiguration crypto = new YamlConfiguration();

    //Players
    private File pl = null;
    private final YamlConfiguration players = new YamlConfiguration();

    //Sign Shop
    private File ss = null;
    private final YamlConfiguration signs = new YamlConfiguration();

    //English
    private File en = null;
    private final YamlConfiguration engl = new YamlConfiguration();

    @Override
    public void onEnable() {
        instance = this;
        this.co = new File(getDataFolder(), "config.yml");
        this.cc = new File(getDataFolder(), "cryptocurrencies.yml");
        this.pl = new File(getDataFolder(), "players.yml");
        this.ss = new File(getDataFolder(), "signshops.yml");
        this.en = new File(getDataFolder(), "Languages/English.yml");

        mkdir();
        loadYamls();

        //Initialize all crypto currencies
        Set<String> crypto_keys = getCrypto().getKeys(false);
        for(String crypto_str : crypto_keys){
            Settings.cryptos.put(crypto_str, new Crypto(getCrypto().getString(crypto_str+ ".name"), crypto_str, getCrypto().getString(crypto_str+ ".color", "6"), getCrypto().getString(crypto_str+".format", "0.0000"), getCrypto().getDouble(crypto_str+".maximum", 100), getCrypto().getDouble(crypto_str+".minimum", 0.0001)));
        }

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
                for(String crypto_str : crypto_keys){
                    conn.createStatement().execute("CREATE TABLE IF NOT EXISTS cryptocurrency_" + crypto_str + "(uuid char(36) NOT NULL PRIMARY KEY, username varchar(25) NOT NULL, balance double);");
                }
                conn.close();
            } catch (SQLException e) {
                conn = null;
            }
        }

        //Listeners
        new PlayerJoinListener(this);
        new TabCompleteListener(this);
        //Sign Shop
        if(getConf().getBoolean("shop_enabled", true)){
            new SignChangeListener(this);
            new PlayerInteractListener(this);
            new BlockBreakListener(this);
            new BlockExplodeListener(this);
        }

        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            for(String crypto_str : crypto_keys){
                commandMap.register(crypto_str, new CryptoCMD(crypto_str));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        //PlaceholderAPI
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Placeholders().register();
        }

        //Update Checker
        new UpdateChecker(this, 51).getVersion(updater_version -> {
            if (!getDescription().getVersion().equalsIgnoreCase(updater_version)) new_version = updater_version;
            info("&aEnabling");
        });

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
        if(!this.co.exists()) saveResource("config.yml", false);
        if(!this.cc.exists()) saveResource("cryptocurrencies.yml", false);
        if(!this.pl.exists()) saveResource("players.yml", false);
        if(!this.ss.exists()) saveResource("signshops.yml", false);
        if(!this.en.exists()) saveResource("Languages/English.yml", false);
    }

    public void loadYamls(){

        try{
            this.conf.load(this.co);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try{
            this.crypto.load(this.cc);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try{
            this.players.load(this.pl);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try{
            this.signs.load(this.ss);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            this.engl.load(this.en);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    public YamlConfiguration getConf() { return this.conf; }
    public YamlConfiguration getCrypto() { return this.crypto; }
    public YamlConfiguration getPlayers() { return this.players; }
    public YamlConfiguration getSignShops() { return this.signs; }
    public YamlConfiguration getEngl() { return this.engl; }

    public void savePlayers(){
        try {
            this.players.save(pl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSignShops(){
        try {
            this.signs.save(ss);
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
