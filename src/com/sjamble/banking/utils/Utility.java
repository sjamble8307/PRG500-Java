package com.sjamble.banking.utils;

import java.util.Random;

/** Generates account numbers. generateAccountNumber() is package-private;
 * getAccountNumber() is the public bridge. */
public class Utility {

    private static final Random RANDOM = new Random();

    String generateAccountNumber() {
        return String.valueOf(10_000_000 + RANDOM.nextInt(90_000_000));
    }

    public String getAccountNumber() {
        return generateAccountNumber();
    }
}
