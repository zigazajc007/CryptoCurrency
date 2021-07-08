package com.rabbitcompany.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Number {

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException ignored){}
        return false;
    }

    public static double roundDouble(double d, int places) {
        BigDecimal bigDecimal = new BigDecimal(Double.toString(d));
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
