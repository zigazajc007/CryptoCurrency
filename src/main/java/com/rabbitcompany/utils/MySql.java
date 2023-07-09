package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySql {

	public static double getPlayerBalance(String player_uuid, String player_name, String crypto) {
		String query = "SELECT balance FROM cryptocurrency_" + crypto + " WHERE uuid = ? OR username = ?;";
		double balance = 0;
		try {
			Connection conn = CryptoCurrency.hikari.getConnection();
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, player_uuid);
			ps.setString(2, player_name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) balance = rs.getDouble("balance");
			rs.close();
			ps.close();
			conn.close();
		} catch (SQLException ignored) {
		}
		return balance;
	}

	public static boolean setPlayerBalance(String player_uuid, String player_name, String balance, String crypto) {
		String query = "UPDATE cryptocurrency_" + crypto + " SET balance = " + balance + " WHERE uuid = ? OR username = ?;";
		try {
			Connection conn = CryptoCurrency.hikari.getConnection();
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, player_uuid);
			ps.setString(2, player_name);
			ps.executeUpdate();
			ps.close();
			conn.close();
			return true;
		} catch (SQLException ignored) {
		}
		return false;
	}

	public static boolean isPlayerInDatabase(String player_name, String crypto) {
		String query = "SELECT * FROM cryptocurrency_" + crypto + " WHERE username = ?;";
		boolean isInDB = false;
		try {
			Connection conn = CryptoCurrency.hikari.getConnection();
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, player_name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) isInDB = true;
			rs.close();
			ps.close();
			conn.close();
		} catch (SQLException ignored) {
		}
		return isInDB;
	}

	public static boolean createPlayerWallet(String player_uuid, String player_name, String crypto) {
		String query = "INSERT INTO cryptocurrency_" + crypto + " VALUES (?, ?, 0);";
		try {
			Connection conn = CryptoCurrency.hikari.getConnection();
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, player_uuid);
			ps.setString(2, player_name);
			ps.executeUpdate();
			ps.close();
			conn.close();
			return true;
		} catch (SQLException ignored) {
		}
		return false;
	}

	public static double getCryptoSupply(String crypto) {
		String query = "SELECT SUM(balance) as supply FROM cryptocurrency_" + crypto + ";";
		double supply = 0;
		try {
			Connection conn = CryptoCurrency.hikari.getConnection();
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) supply = rs.getDouble("supply");
			rs.close();
			ps.close();
			conn.close();
		} catch (SQLException ignored) {
		}
		return supply;
	}

}
