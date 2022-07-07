package com.rabbitcompany.utils;

public class Mining {

    private final String crypto;
    private final String block;
    private final double amount;


    public Mining(String crypto, String block, double amount) {
        this.crypto = crypto;
        this.block = block;
        this.amount = amount;
    }

    public String getCrypto() {
        return this.crypto;
    }

    public String getBlock() {
        return this.block;
    }

    public double getAmount() {
        return this.amount;
    }


}
