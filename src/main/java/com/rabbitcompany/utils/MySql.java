package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySql {

    public static double getPlayerBalance(String player_uuid, String player_name, String crypto){
        String query = "SELECT balance FROM cryptocurrency_" + crypto + " WHERE uuid = '" + player_uuid + "' OR username = '" + player_name + "';";
        double balance = 0;
        try {
            Connection conn = CryptoCurrency.hikari.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) balance = rs.getDouble("balance");
            conn.close();
        } catch (SQLException ignored) {}
        return balance;
    }

    public static boolean setPlayerBalance(String player_uuid, String player_name, String balance, String crypto){
        try {
            Connection conn = CryptoCurrency.hikari.getConnection();
            conn.createStatement().executeUpdate("UPDATE cryptocurrency_" + crypto + " SET balance = " + balance + " WHERE uuid = '" + player_uuid + "' OR username = '" + player_name + "';");
            conn.close();
            return true;
        } catch (SQLException ignored) {}
        return false;
    }

    public static boolean isPlayerInDatabase(String player_name, String crypto){
        String query = "SELECT * FROM cryptocurrency_" + crypto + " WHERE username = '" + player_name + "';";
        try {
            Connection conn = CryptoCurrency.hikari.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            conn.close();
            if(rs.next()) return true;
        } catch (SQLException ignored) {}
        return false;
    }

    public static boolean createPlayerWallet(String player_uuid, String player_name, String crypto){
        try {
            Connection conn = CryptoCurrency.hikari.getConnection();
            conn.createStatement().executeUpdate("INSERT INTO cryptocurrency_" + crypto + " VALUES ('" + player_uuid + "', '" + player_name + "', 0);");
            conn.close();
            return true;
        } catch (SQLException ignored) {}
        return false;
    }

    public static double getCryptoSupply(String crypto){
        String query = "SELECT SUM(balance) as supply FROM cryptocurrency_" + crypto + ";";
        double supply = 0;
        try {
            Connection conn = CryptoCurrency.hikari.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) supply = rs.getDouble("supply");
            conn.close();
        } catch (SQLException ignored) {}
        return supply;
    }

}
