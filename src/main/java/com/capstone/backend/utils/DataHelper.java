package com.capstone.backend.utils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class DataHelper {
    public static Set<Long> parseStringToLongSet(String str) {
        str = str.replaceAll("[\\[\\] ]", "");
        String[] tagArray = str.split(",");
        Set<Long> tags = new HashSet<>();
        Arrays.stream(tagArray).forEach(tag -> tags.add(Long.parseLong(tag)));
        return tags;
    }

    public static String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
}
