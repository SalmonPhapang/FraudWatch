package com.fraudwatch.fraudruleengine.ruleengine;

import java.math.BigDecimal;

public class ComparisonHelper {

    public static int compare(Object actual, String expected) {
        if (actual == null || expected == null) {
            return 0;
        }
        try {
            BigDecimal actualNum = new BigDecimal(String.valueOf(actual));
            BigDecimal expectedNum = new BigDecimal(expected);
            return actualNum.compareTo(expectedNum);
        } catch (NumberFormatException e) {
            return String.valueOf(actual).compareTo(expected);
        }
    }
}
