package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MySql {

    public static double getPlayerBalance(String player_uuid, String player_name, String crypto){
        String query = "SELECT balance FROM cryptocurrency_" + crypto + " WHERE uuid = '" + player_uuid + "' OR username = '" + player_name + "';";
        AtomicReference<Double> balance = new AtomicReference<>((double) 0);
        try {
            CryptoCurrency.mySQL.query(query, results -> {
                if (results.next()) {
                    balance.set(results.getDouble("balance"));
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance.get();
    }

    public static void setPlayerBalance(String player_uuid, String player_name, String balance, String crypto){
        try {
            CryptoCurrency.mySQL.update("UPDATE cryptocurrency_" + crypto + " SET balance = " + balance + " WHERE uuid = '" + player_uuid + "' OR username = '" + player_name + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPlayerInDatabase(String player_name, String crypto){
        String query = "SELECT * FROM cryptocurrency_" + crypto + " WHERE username = '" + player_name + "';";
        AtomicBoolean isInDatabase = new AtomicBoolean(false);
        try {
            CryptoCurrency.mySQL.query(query, results -> {
                if (results.next()) {
                    isInDatabase.set(true);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isInDatabase.get();
    }

}
