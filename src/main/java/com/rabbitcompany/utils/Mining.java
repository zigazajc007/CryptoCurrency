package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Mining {

	private final String crypto;
	private final double max;
	private final double min;
	private final double chance;
	private final int messageType;
	private final String message;
	private final List<String> gamemodes;
	private final List<String> worlds;
	private final Random random;


	public Mining(String crypto, double max, double min, double chance, int messageType, String message, List<String> gamemodes, List<String> worlds) {
		this.crypto = crypto;
		this.max = max;
		this.min = min;
		this.chance = chance;
		this.messageType = messageType;
		this.message = message;
		this.gamemodes = gamemodes;
		this.worlds = worlds;
		this.random = new Random();
	}

	public static void initializeMining(){
		Settings.mining.clear();

		for (String block_str : CryptoCurrency.getInstance().getMining().getKeys(false)) {
			String block = block_str.toUpperCase(Locale.ROOT);

			for(String crypto_str : CryptoCurrency.getInstance().getMining().getConfigurationSection(block_str).getKeys(false)) {
				String crypto = crypto_str.toLowerCase(Locale.ROOT);

				if (!API.isCryptoEnabled(crypto)) continue;

				List<Mining> miningList = Settings.mining.get(block);
				if(miningList == null) miningList = new ArrayList<>();

				miningList.add(new Mining(
					crypto,
					CryptoCurrency.getInstance().getMining().getDouble(block_str + "." + crypto_str + ".max", 0),
					CryptoCurrency.getInstance().getMining().getDouble(block_str + "." + crypto_str + ".min", 0),
					CryptoCurrency.getInstance().getMining().getDouble(block_str + "." + crypto_str + ".chance", 0),
					CryptoCurrency.getInstance().getMining().getInt(block_str + "." + crypto_str + ".message_type", 1),
					CryptoCurrency.getInstance().getMining().getString(block_str + "." + crypto_str + ".message", null),
					CryptoCurrency.getInstance().getMining().getStringList(block_str + "." + crypto_str + ".gamemodes"),
					CryptoCurrency.getInstance().getMining().getStringList(block_str + "." + crypto_str + ".worlds")
				));

				Settings.mining.put(block_str, miningList);
			}
		}
	}

	public String getCrypto() {
		return this.crypto;
	}

	public String getMessage(){
		return this.message;
	}

	public int getMessageType(){
		return this.messageType;
	}

	public List<String> getGamemodes(){
		return this.gamemodes;
	}

	public List<String> getWorlds(){
		return this.worlds;
	}

	public double getAmount() {
		return min + (max - min) * random.nextDouble();
	}

	public boolean isAwarded(){
		return random.nextInt(100) < chance;
	}

}
