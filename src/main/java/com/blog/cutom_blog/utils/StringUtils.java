package com.blog.cutom_blog.utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Random randomGenerator = new Random();

    public StringUtils() {
    }

    public static boolean isBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isAnyBlank(String... strings) {
        return (Boolean) Arrays.stream(strings).map(StringUtils::isBlank).reduce(false, (total, current) -> total || current);
    }

    public static boolean isNumeric(String string) {
        return isBlank(string) ? false : string.matches("\\d+");
    }

    public static String stripPunct(String s) {
        return isBlank(s) ? null : s.replaceAll("\\W", "");
    }

    public static boolean hasOnlyLetters(String str) {
        return !isBlank(str) && !str.matches(".*\\d.*");
    }

    public static String cleanSpaceAndSymbols(String s) {
        s = s.replaceAll("\\s+", "");
        s = s.replaceAll("-", "").replaceAll(",", "").replaceAll("\\+", "");
        return s;
    }

    public static boolean equalsAllowNull(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        } else {
            return (str1 == null || str2 != null) && str1 != null ? str1.equals(str2) : false;
        }
    }

    public static String getRandomInList(List<String> list) {
        int index = randomGenerator.nextInt(list.size());
        return (String)list.get(index);
    }

    public static Optional<String> stripNonDigit(String str) {
        if (isBlank(str)) {
            return Optional.empty();
        } else {
            String res = str.replaceAll("[^\\d.]", "");
            return isBlank(res) ? Optional.empty() : Optional.of(res);
        }
    }

    public static String parse0906(String str) {
        if (!isBlank(str) && str.contains("0906")) {
            int index = str.indexOf("0906");
            return index + 11 > str.length() ? null : str.substring(index, index + 11);
        } else {
            return null;
        }
    }

    public static String getNDigitTextFromString(int n, String text) {
        Pattern p = Pattern.compile("(\\d{" + n + "})");
        Matcher m = p.matcher(text);
        return m.find() ? m.group(0) : null;
    }

    public static List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList();
        String[] words = str.split(" ");

        for(int i = 0; i < words.length - n + 1; ++i) {
            ngrams.add(concat(words, i, i + n));
        }

        return ngrams;
    }

    private static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();

        for(int i = start; i < end; ++i) {
            sb.append((i > start ? " " : "") + words[i]);
        }

        return sb.toString();
    }

    public static int getRandomInRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static String trim(String s) {
        return s == null ? null : s.trim();
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static String toTitleCase(String input) {
        if (input != null && !input.isEmpty()) {
            StringBuilder titleCase = new StringBuilder();
            boolean nextTitleCase = true;

            for(char c : input.toCharArray()) {
                if (Character.isSpaceChar(c)) {
                    nextTitleCase = true;
                } else if (nextTitleCase) {
                    c = Character.toTitleCase(c);
                    nextTitleCase = false;
                } else {
                    c = Character.toLowerCase(c);
                }

                titleCase.append(c);
            }

            return titleCase.toString();
        } else {
            return input;
        }
    }

    public static String capitalize(String input) {
        return input != null && !input.isEmpty() ? String.format("%s,%s", Character.toUpperCase(input.charAt(0)), input.substring(1).toLowerCase()) : input;
    }
}

