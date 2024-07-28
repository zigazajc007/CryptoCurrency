package com.rabbitcompany.utils;

import java.util.Random;

public class Mining {

	private final String crypto;
	private final double max;
	private final double min;
	private final double chance;
	private final int messageType;
	private final String message;
	private final Random random;


	public Mining(String crypto, double max, double min, double chance, int messageType, String message) {
		this.crypto = crypto;
		this.max = max;
		this.min = min;
		this.chance = chance;
		this.messageType = messageType;
		this.message = message;
		this.random = new Random();
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

	public double getAmount() {
		return min + (max - min) * random.nextDouble();
	}

	public boolean isAwarded(){
		return random.nextInt(100) < chance;
	}


}
