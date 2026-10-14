/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.bigfont;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;
import java.util.stream.IntStream;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigFontUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected static final Map<Character, String> BARLINE = map(BarlineFont.NUMBERS).with(BarlineFont.UPPERCASE_LETTERS).with(BarlineFont.LOWERCASE_LETTERS).with(BarlineFont.SPECIAL);
    protected static final Map<Character, String> FAT = map(FatFont.NUMBERS).with(FatFont.UPPERCASE_LETTERS).with(FatFont.LOWERCASE_LETTERS).with(FatFont.SPECIAL);

    public static String getLogo() {
        return Logo.LOGO_SMALL;
    }

    /**
     * Returns the value converted to fat font.
     *
     * <p>
     * It accept only letters and numbers.</p>
     *
     * @param value the value to convert
     * @return the value converted to big font
     */
    public static String convertToFatFont(String value) {
        return convertToBigFont(FAT, value, false);
    }

    /**
     * Returns the value converted to barline font.
     *
     * <p>
     * It accept only letters and numbers.</p>
     *
     * @param value the value to convert
     * @return the value converted to big font
     */
    public static String convertToBarlineFont(String value) {
        return convertToBigFont(BARLINE, value, true);
    }

    /**
     * Returns the value converted to barline font.
     *
     * <p>
     * It accept only letters and numbers.</p>
     *
     * @param bigfont map to convert characters
     * @param value the value to convert
     * @param merge merge 2 characters if possible
     * @return the value converted to big font
     */
    private static String convertToBigFont(Map<Character, String> bigfont, String value, boolean merge) {
        try {
            List<String> characters = list();
            IntStream.range(0, value.length()).mapToObj(value::charAt).map(bigfont::get).forEach(characters::add);
            StringBuilder converted = new StringBuilder();
            int rows = characters.stream().map(b -> b.lines().toList().size()).max(Integer::compareTo).get() + 1;
            IntStream.range(0, rows).forEach(row -> {
                StringBuilder line = new StringBuilder();
                characters.forEach(bf -> {
                    StringBuilder letter = bf.lines().toList().size() > row ? new StringBuilder(bf.lines().toList().get(row)) : new StringBuilder();
                    IntStream.range(0, bf.lines().map(String::length).max(Integer::compareTo).get() - letter.length()).forEach(ic -> letter.append(" "));
                    mergeBigCharacters(line, letter, merge);
                });
                converted.append(line).append("\n");
            });
            return converted.toString().lines().map(String::stripTrailing).collect(joining("\n")); // stripTrailing from all lines
        } catch (Exception ex) {
            LOGGER.warn("unable to convert =< {} > to big font", value, ex);
            return value;
        }
    }

    /**
     * Merge line buffer and the letter line. The middle letter is merged by
     * {@link BigFontUtils#getMiddleBigCharacter}
     *
     * @param line the line buffer
     * @param letter the line letter
     */
    private static void mergeBigCharacters(StringBuilder line, StringBuilder letter, boolean merge) {
        if (merge && !line.isEmpty()) {
            String middleChar = getMiddleBigCharacter(Character.toString(line.charAt(line.length() - 1)), Character.toString(letter.charAt(0)));
            line.deleteCharAt(line.length() - 1);
            letter.deleteCharAt(0);
            line.append(middleChar);
        }
        line.append(letter);
    }

    /**
     * Returns the middle char from the last char of the line buffer and the
     * first char of the line buffer
     *
     * @param line the line buffer
     * @param letter the line letter
     */
    private static String getMiddleBigCharacter(String firstChar, String lastChar) {
        if (isNotBlank(firstChar) && isNotBlank(lastChar)) {
            if (firstChar.equals("|")) {
                return lastChar;
            } else if (lastChar.equals("|")) {
                return firstChar;
            }
        }
        if (isBlank(lastChar)) {
            return firstChar;
        }
        return lastChar;
    }
}
