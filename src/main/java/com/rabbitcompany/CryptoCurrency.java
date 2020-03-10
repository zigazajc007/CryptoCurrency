package com.rabbitcompany;

import com.rabbitcompany.commands.BTC;
import com.rabbitcompany.utils.Message;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pro.husk.mysql.MySQL;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public final class CryptoCurrency extends JavaPlugin {

    private static CryptoCurrency instance;

    //SQL
    public static MySQL mySQL;
    public static Connection conn = null;

    //VaultAPI
    private static Economy econ = null;
    public static boolean vault = false;

    //Config
    private File co = null;
    private YamlConfiguration conf = new YamlConfiguration();

    //English
    private File en = null;
    private YamlConfiguration engl = new YamlConfiguration();

    //BTC Wallets
    private File bw = null;
    private YamlConfiguration btcw = new YamlConfiguration();

    @Override
    public void onEnable() {
        instance = this;
        this.co = new File(getDataFolder(), "config.yml");
        this.en = new File(getDataFolder(), "Languages/English.yml");
        this.bw = new File(getDataFolder(), "Wallets/btc_wallets.yml");

        mkdir();
        loadYamls();

        info("&aEnabling");

        //VaultAPI
        if(setupEconomy()){
            vault = true;
        }

        //SQL
        if(getConf().getBoolean("mysql", false)){
            try {
                mySQL = new MySQL(getConf().getString("mysql_host"), getConf().getString("mysql_port"), getConf().getString("mysql_database"), getConf().getString("mysql_user"), getConf().getString("mysql_password"), "?useSSL=" + getConf().getBoolean("mysql_useSSL") +"&allowPublicKeyRetrieval=true");
                conn = mySQL.getConnection();
                conn.createStatement().execute("CREATE TABLE IF NOT EXISTS cryptocurrency_btc(uuid char(36) NOT NULL PRIMARY KEY, username varchar(25) NOT NULL, balance BIGINT UNSIGNED);");
            } catch (SQLException e) {
                conn = null;
            }
        }

        //Commands
        this.getCommand("btc").setExecutor((CommandExecutor) new BTC());
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
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
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
        Bukkit.getConsoleSender().sendMessage(Message.chat("&8|   &9Version: &b1.0"));
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
