package com.rabbitcompany;

import com.rabbitcompany.commands.BTC;
import com.rabbitcompany.listeners.PlayerJoinListener;
import com.rabbitcompany.utils.Message;
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

    @Override
    public void onEnable() {
        instance = this;
        this.co = new File(getDataFolder(), "config.yml");
        this.en = new File(getDataFolder(), "Languages/English.yml");
        this.bw = new File(getDataFolder(), "Wallets/btc_wallets.yml");

        mkdir();
        loadYamls();

        //VaultAPI
        if (getServer().getPluginManager().getPlugin("Vault") != null){
            vault = setupEconomy();
        }

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
                conn.close();
            } catch (SQLException e) {
                conn = null;
            }
        }

        //Listeners
        new PlayerJoinListener(this);

        //Commands
        this.getCommand("btc").setExecutor(new BTC());

        //Updater
        new UpdateChecker(this, 71157).getVersion(updater_version -> {
            if (!getDescription().getVersion().equalsIgnoreCase(updater_version)) {
                new_version = updater_version;
            }
            info("&aEnabling");
        });

        //Check for updates
        Updater.sendConsole();
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
    }

    public YamlConfiguration getConf() { return this.conf; }
    public YamlConfiguration getEngl() { return this.engl; }
    public YamlConfiguration getBtcw() { return this.btcw; }

    public void saveBtcw() {
        try {
            this.btcw.save(this.bw);
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
            Bukkit.getConsoleSender().sendMessage(Message.chat("&8|   &9Version: &b" + getDescription().getVersion() + " (FREE) (&6update available&b)"));
        }else{
            Bukkit.getConsoleSender().sendMessage(Message.chat("&8|   &9Version: &b" + getDescription().getVersion() + " (FREE)"));
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
