package com.manga.server.shared.regex;

import java.util.regex.Pattern;

public class RegexUtils {

    private RegexUtils() {}

    public static String accentInsensitive(String text) {
        if (text == null || text.isBlank()) return ".*";

        StringBuilder regex = new StringBuilder(".*");
        for (char c : text.toCharArray()) {
            regex.append(mapChar(c));
        }
        return regex.append(".*").toString();
    }

    private static String mapChar(char c) {
        return switch (Character.toLowerCase(c)) {
            case 'a' -> "[aáàäâãåā]";
            case 'e' -> "[eéèëêēė]";
            case 'i' -> "[iíìïîīį]";
            case 'o' -> "[oóòöôõōø]";
            case 'u' -> "[uúùüûū]";
            case 'n' -> "[nñ]";
            case 'c' -> "[cç]";
            default -> Pattern.quote(String.valueOf(c));
        };
    }
}
