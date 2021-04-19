package com.rabbitcompany.utils;

public class Number {

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException ignored){}
        return false;
    }

}
