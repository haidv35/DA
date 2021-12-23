package com.demo.deserialization;

import com.google.gson.Gson;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordGenerator {
    private static final String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPERCASE = CHAR_LOWERCASE.toUpperCase();
    private static final String DIGIT = "0123456789";
    private static final String OTHER_PUNCTUATION = "!@#&()–[{}]:;',?/*";
    private static final String OTHER_SYMBOL = "~$^+=<>";
    private static final String OTHER_SPECIAL = OTHER_PUNCTUATION + OTHER_SYMBOL;

    private static final String PASSWORD_ALLOW =
            CHAR_LOWERCASE + CHAR_UPPERCASE + DIGIT + OTHER_SPECIAL;

    private static SecureRandom random = new SecureRandom();

    public static String generateStrongPassword(String initPassword, int passwordLength) {
        Gson json = new Gson();

        Password response = new Password();

        StringBuilder result = new StringBuilder(passwordLength);

        // at least 2 chars (lowercase)
        String strLowerCase = generateRandomString(CHAR_LOWERCASE, 2);
        response.setLowerCase(strLowerCase);
        result.append(strLowerCase);

        // at least 2 chars (uppercase)
        String strUpperCase = generateRandomString(CHAR_UPPERCASE, 2);
        response.setUpperCase(strUpperCase);
        result.append(strUpperCase);

        // at least 2 digits
        String strDigit = generateRandomString(DIGIT, 2);
        response.setDigits(strDigit);
        result.append(strDigit);

        // at least 2 special characters (punctuation + symbols)
        String strSpecialChar = generateRandomString(OTHER_SPECIAL, 2);
        response.setSpecialChars(strSpecialChar);
        result.append(strSpecialChar);

        // remaining, just random
        String strOther = generateRandomString(PASSWORD_ALLOW, passwordLength - 8);
        response.setOthers(strOther);
        result.append(strOther);

        // remaining, just random
        String strInit = generateRandomString(initPassword, passwordLength - 8);
        response.setInitPassword(strInit);
        result.append(strOther);

        String password = result.toString();
        // combine all
        response.setPassword(password);
        // shuffle again
        response.setFinalPassword(shuffleString(password));
        response.setPasswordLength(password.length());
        return json.toJson(response);
    }

    // generate a random char[], based on `input`
    private static String generateRandomString(String input, int size) {

        if (input == null || input.length() <= 0)
            throw new IllegalArgumentException("Invalid input.");
        if (size < 1) throw new IllegalArgumentException("Invalid size.");

        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            // produce a random order
            int index = random.nextInt(input.length());
            result.append(input.charAt(index));
        }
        return result.toString();
    }

    // for final password, make it more random
    public static String shuffleString(String input) {
        List<String> result = Arrays.asList(input.split(""));
        Collections.shuffle(result);
        // java 8
        return result.stream().collect(Collectors.joining());
    }
}
