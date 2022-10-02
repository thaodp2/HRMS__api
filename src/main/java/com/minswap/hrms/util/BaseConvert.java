package com.minswap.hrms.util;

import java.math.BigInteger;

public class BaseConvert {
    public static final int MIN_RADIX = 2;
    public static final int MAX_RADIX = 62;
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public BaseConvert() {
    }

    public static int digit(char ch, int radix) {
        if (radix <= 36) {
            ch = Character.toLowerCase(ch);
        }

        return "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(0, radix).indexOf(ch);
    }

    public static char forDigit(int digit, int radix) {
        return "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(0, radix).charAt(digit);
    }

    public static String convert(String source, int sourceRadix, int targetRadix) {
        StringBuilder result = new StringBuilder();
        if (sourceRadix >= 2 && targetRadix >= 2 && sourceRadix <= 62 && targetRadix <= 62) {
            BigInteger radixFrom = BigInteger.valueOf((long)sourceRadix);
            BigInteger radixTo = BigInteger.valueOf((long)targetRadix);
            BigInteger value = BigInteger.ZERO;
            BigInteger multiplier = BigInteger.ONE;

            for(int i = source.length() - 1; i >= 0; --i) {
                int digit = digit(source.charAt(i), sourceRadix);
                if (digit == -1) {
                    throw new IllegalArgumentException("The character '" + source.charAt(i) + "' is not defined for the radix " + sourceRadix);
                }

                value = value.add(multiplier.multiply(BigInteger.valueOf((long)digit)));
                multiplier = multiplier.multiply(radixFrom);
            }

            while(BigInteger.ZERO.compareTo(value) < 0) {
                BigInteger[] quotientAndRemainder = value.divideAndRemainder(radixTo);
                char c = forDigit(quotientAndRemainder[1].intValue(), targetRadix);
                result.insert(0, c);
                value = quotientAndRemainder[0];
            }

            return result.toString();
        } else {
            throw new IllegalArgumentException("Source and target radix both need to be in a range from 2 to 62");
        }
    }
}
