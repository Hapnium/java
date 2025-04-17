package com.hapnium.core.primitives;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.*;

public class StringExtensions {
    private static final Pattern UPPER_ALPHA_REGEX = Pattern.compile("[A-Z]");
    private static final String EMOJI_REGEX = "(?:[\\u203C-\\u3299]|\\uD83C[\\uDC04-\\uDBFF\\uD000-\\uDFFF]|\\uD83D[\\uDC00-\\uDE4F\\uDE80-\\uDEFF]|\\uD83E[\\uDD00-\\uDDFF])";
    private static final Set<Character> SYMBOL_SET = new HashSet<>(Arrays.asList(' ', '.', '/', '_', '\\', '-'));

    @NotNull
    public static List<String> groupIntoWords(@NotNull String text) {
        StringBuilder sb = new StringBuilder();
        List<String> words = new ArrayList<>();
        boolean isAllCaps = text.toUpperCase().equals(text);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            Character nextChar = (i + 1 == text.length()) ? null : text.charAt(i + 1);

            if (SYMBOL_SET.contains(ch)) {
                continue;
            }

            sb.append(ch);
            boolean isEndOfWord = nextChar == null ||
                    (UPPER_ALPHA_REGEX.matcher(Character.toString(nextChar)).find() && !isAllCaps) ||
                    SYMBOL_SET.contains(nextChar);

            if (isEndOfWord) {
                words.add(sb.toString());
                sb.setLength(0); // clear
            }
        }

        return words;
    }

    /**
     * Case-insensitive equality check.
     *
     * @param word
     * @param other
     * @return
     */
    @Contract(value = "_, null -> false", pure = true)
    public static boolean equalsIgnoreCase(@NotNull String word, String other) {
        return word.equalsIgnoreCase(other);
    }

    @Contract(value = "_, null -> true", pure = true)
    public static boolean notEqualsIgnoreCase(@NotNull String word, String other) {
        return !word.equalsIgnoreCase(other);
    }

    @Contract(value = "_, null -> false", pure = true)
    public static boolean equals(@NotNull String word, String other) {
        return word.equals(other);
    }

    @Contract(value = "_, null -> true", pure = true)
    public static boolean notEquals(@NotNull String word, String other) {
        return !word.equals(other);
    }

    public static boolean equalsAny(String word, List<String> values, boolean isUpperCase) {
        if (isUpperCase) {
            return values.stream().anyMatch(v -> v.equalsIgnoreCase(word));
        }
        return values.stream().anyMatch(v -> v.equalsIgnoreCase(word));
    }

    public static boolean notEqualsAny(String word, List<String> values, boolean isUpperCase) {
        return notEqualsAll(word, values, isUpperCase);
    }

    public static boolean equalsAll(String word, List<String> values, boolean isUpperCase) {
        if (isUpperCase) {
            return values.stream().allMatch(v -> v.equalsIgnoreCase(word));
        }
        return values.stream().allMatch(v -> v.equalsIgnoreCase(word));
    }

    public static boolean notEqualsAll(String word, List<String> values, boolean isUpperCase) {
        if (isUpperCase) {
            return values.stream().noneMatch(v -> v.equalsIgnoreCase(word));
        }
        return values.stream().noneMatch(v -> v.equalsIgnoreCase(word));
    }

    public static boolean containsIgnoreCase(@NotNull String word, @NotNull String value) {
        return word.toLowerCase().contains(value.toLowerCase());
    }

    public static boolean isNumeric(String word) {
        if (word == null || word.isEmpty()) return false;
        try {
            Double.parseDouble(word);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @NotNull
    public static String withAorAn(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.matches("^[aeiouAEIOU].*") ? "an " + word.toLowerCase() : "a " + word.toLowerCase();
    }

    public static String capitalizeFirst(String word) {
        if (word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    public static String capitalizeEach(String value) {
        if (value == null || value.isEmpty()) return value;
        String[] words = value.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    // Helper for regex match
    public static boolean matchesRegex(String word, String pattern) {
        return Pattern.compile(pattern).matcher(word).matches();
    }

    @Contract(pure = true)
    public static boolean isNumericOnly(@NotNull String text) {
        return text.matches("^\\d+$");
    }

    @Contract(pure = true)
    public static boolean isAlphabetOnly(@NotNull String text) {
        return text.matches("^[a-zA-Z]+$");
    }

    @Contract(pure = true)
    public static boolean hasCapitalLetter(@NotNull String text) {
        return text.matches(".*[A-Z].*");
    }

    @Contract(pure = true)
    public static boolean isURL(@NotNull String text) {
        return text.matches("^((((H|h)(T|t)|(F|f))(T|t)(P|p)(S|s)?):\\/\\/)?(www\\.|[a-zA-Z0-9].)[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,7}(\\:[0-9]{1,5})*(\\/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\\\+&%\\$#=~_\\-]+))*$");
    }

    @Contract(pure = true)
    public static boolean isEmail(@NotNull String text) {
        return text.matches("^(([^<>()[\\]\\\\.,;:\\s@\"]+(\\.[^<>()[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
    }

    public static boolean isPhoneNumber(@NotNull String text) {
        if (text.length() < 9 || text.length() > 16) return false;
        return text.matches("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$");
    }

    @Contract(pure = true)
    public static boolean isDateTime(@NotNull String text) {
        return text.matches("^\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z?$");
    }

    @Contract(pure = true)
    public static boolean isMD5(@NotNull String text) {
        return text.matches("^[a-f0-9]{32}$");
    }

    @Contract(pure = true)
    public static boolean isSHA1(@NotNull String text) {
        return text.matches("(([A-Fa-f0-9]{2}:){19}[A-Fa-f0-9]{2}|[A-Fa-f0-9]{40})");
    }

    @Contract(pure = true)
    public static boolean isSHA256(@NotNull String text) {
        return text.matches("(([A-Fa-f0-9]{2}:){31}[A-Fa-f0-9]{2}|[A-Fa-f0-9]{64})");
    }

    @Contract(pure = true)
    public static boolean isSSN(@NotNull String text) {
        return text.matches("^(?!0{3}|6{3}|9[0-9]{2})[0-9]{3}-?(?!0{2})[0-9]{2}-?(?!0{4})[0-9]{4}$");
    }

    @Contract(pure = true)
    public static boolean isBinary(@NotNull String text) {
        return text.matches("^[0-1]+$");
    }

    @Contract(pure = true)
    public static boolean isIPv4(@NotNull String text) {
        return text.matches("^(?:(?:^|\\.)(?:2(?:5[0-5]|[0-4]\\d)|1?\\d?\\d)){4}$");
    }

    @Contract(pure = true)
    public static boolean isIPv6(@NotNull String text) {
        return text.matches("^((([0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){6}:[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){5}:([0-9A-Fa-f]{1,4}:)?[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){4}:([0-9A-Fa-f]{1,4}:){0,2}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){3}:([0-9A-Fa-f]{1,4}:){0,3}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){2}:([0-9A-Fa-f]{1,4}:){0,4}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){6}((\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b)\\.){3}(\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b))|(([0-9A-Fa-f]{1,4}:){0,5}:((\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b)\\.){3}(\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b))|(::([0-9A-Fa-f]{1,4}:){0,5}((\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b)\\.){3}(\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b))|([0-9A-Fa-f]{1,4}::([0-9A-Fa-f]{1,4}:){0,5}[0-9A-Fa-f]{1,4})|(::([0-9A-Fa-f]{1,4}:){0,6}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){1,7}:))$");
    }

    @Contract(pure = true)
    public static boolean isHexadecimal(@NotNull String text) {
        return text.matches("^#?([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$");
    }

    @Contract(pure = true)
    public static boolean isPassport(@NotNull String text) {
        return text.matches("^(?!^0+$)[a-zA-Z0-9]{6,9}$");
    }

    @Contract(pure = true)
    public static boolean isCurrency(@NotNull String text) {
        return text.matches("^(S?\\$|₩|Rp|¥|€|₹|₽|fr|R\\$|R)?[ ]?[-]?([0-9]{1,3}[,.]([0-9]{3}[,.])*[0-9]{3}|[0-9]+)([,.][0-9]{1,2})?( ?(USD?|AUD|NZD|CAD|CHF|GBP|CNY|EUR|JPY|IDR|MXN|NOK|KRW|TRY|INR|RUB|BRL|ZAR|SGD|MYR))?$");
    }

    @Contract(pure = true)
    public static boolean isEmoji(@NotNull String text) {
        return text.matches("^" + EMOJI_REGEX + "$");
    }

    @Contract(pure = true)
    public static boolean containsEmoji(@NotNull String text) {
        return text.matches(".*" + EMOJI_REGEX + ".*");
    }

    public static boolean isNotEmoji(String text) {
        return !isEmoji(text);
    }

    public static boolean containsOnlyEmoji(@NotNull String text) {
        if (text.isEmpty()) return false;
        String withoutEmojis = text.replaceAll(EMOJI_REGEX, "");
        return withoutEmojis.isEmpty();
    }

    /**
     * Determines the file type based on the file extension.
     *
     * @param path The file path.
     * @return The file type (e.g., "image", "video", "audio", "document", "other").
     */
    @NotNull
    public static String getType(@NotNull String path) {
        String ext = path.toLowerCase();

        if (isVideo(ext)) {
            return "video";
        } else if (isImage(ext)) {
            return "photo";
        } else if(isVector(ext)) {
            return "svg";
        } else if (isAudio(ext)) {
            return "audio";
        } else if (isDocument(ext)) {
            return "document";
        } else if(isAPK(ext)) {
            return "apk";
        } else if(isHTML(ext)) {
            return "html";
        } else {
            return "other";
        }
    }

    /**
     * Checks if the given file extension is a video file extension.
     *
     * @param ext The file extension (e.g., ".mp4", ".avi").
     * @return `true` if the extension is a video extension, `false` otherwise.
     */
    public static boolean isVideo(@NotNull String ext) {
        return ext.endsWith(".mp4") ||
                ext.endsWith(".avi") ||
                ext.endsWith(".wmv") ||
                ext.endsWith(".rmvb") ||
                ext.endsWith(".mpg") ||
                ext.endsWith(".mpeg") ||
                ext.endsWith(".3gp");
    }

    /**
     * Checks if the given file extension is an image file extension.
     *
     * @param ext The file extension (e.g., ".jpg", ".png").
     * @return `true` if the extension is an image extension, `false` otherwise.
     */
    public static boolean isImage(@NotNull String ext) {
        return ext.endsWith(".jpg") ||
                ext.endsWith(".jpeg") ||
                ext.endsWith(".png") ||
                ext.endsWith(".gif") ||
                ext.endsWith(".bmp");
    }

    /**
     * Checks if the given file extension is an audio file extension.
     *
     * @param ext The file extension (e.g., ".mp3", ".wav").
     * @return `true` if the extension is an audio extension, `false` otherwise.
     */
    public static boolean isAudio(@NotNull String ext) {
        return ext.endsWith(".mp3") ||
                ext.endsWith(".wav") ||
                ext.endsWith(".wma") ||
                ext.endsWith(".amr") ||
                ext.endsWith(".ogg");
    }

    /**
     * Checks if the given file extension is a document file extension.
     *
     * @param ext The file extension (e.g., ".pdf", ".doc", ".docx").
     * @return `true` if the extension is a document extension, `false` otherwise.
     */
    public static boolean isDocument(String ext) {
        return isPDF(ext) || isPPT(ext) || isWord(ext) || isExcel(ext) || isTxt(ext) || isChm(ext);
    }

    /**
     * Checks if the given file extension is a PowerPoint file extension.
     *
     * @param ext The file extension (e.g., ".ppt", ".pptx").
     * @return `true` if the extension is a PowerPoint extension, `false` otherwise.
     */
    public static boolean isPPT(@NotNull String ext) {
        return ext.endsWith(".ppt") || ext.endsWith(".pptx");
    }

    /**
     * Checks if the given file extension is a Word file extension.
     *
     * @param ext The file extension (e.g., ".doc", ".docx").
     * @return `true` if the extension is a Word extension, `false` otherwise.
     */
    public static boolean isWord(@NotNull String ext) {
        return ext.endsWith(".doc") || ext.endsWith(".docx");
    }

    /**
     * Checks if the given file extension is an Excel file extension.
     *
     * @param ext The file extension (e.g., ".xls", ".xlsx").
     * @return `true` if the extension is an Excel extension, `false` otherwise.
     */
    public static boolean isExcel(@NotNull String ext) {
        return ext.endsWith(".xls") || ext.endsWith(".xlsx");
    }

    /**
     * Checks if the given file extension is an APK file extension.
     *
     * @param ext The file extension (e.g., ".apk").
     * @return `true` if the extension is an APK extension, `false` otherwise.
     */
    public static boolean isAPK(@NotNull String ext) {
        return ext.toLowerCase().endsWith(".apk");
    }

    /**
     * Checks if the given file extension is a PDF file extension.
     *
     * @param ext The file extension (e.g., ".pdf").
     * @return `true` if the extension is a PDF extension, `false` otherwise.
     */
    public static boolean isPDF(@NotNull String ext) {
        return ext.toLowerCase().endsWith(".pdf");
    }

    /**
     * Checks if the given file extension is a TXT file extension.
     *
     * @param ext The file extension (e.g., ".txt").
     * @return `true` if the extension is a TXT extension, `false` otherwise.
     */
    public static boolean isTxt(@NotNull String ext) {
        return ext.toLowerCase().endsWith(".txt");
    }

    /**
     * Checks if the given file extension is a CHM file extension.
     *
     * @param ext The file extension (e.g., ".chm").
     * @return `true` if the extension is a CHM extension, `false` otherwise.
     */
    public static boolean isChm(@NotNull String ext) {
        return ext.toLowerCase().endsWith(".chm");
    }

    /**
     * Checks if the given file extension is a vector file extension.
     *
     * @param ext The file extension (e.g., ".svg").
     * @return `true` if the extension is a vector extension, `false` otherwise.
     */
    public static boolean isVector(@NotNull String ext) {
        return ext.toLowerCase().endsWith(".svg");
    }

    /**
     * Checks if the given file extension is an HTML file extension.
     *
     * @param ext The file extension (e.g., ".html").
     * @return `true` if the extension is an HTML extension, `false` otherwise.
     */
    public static boolean isHTML(@NotNull String ext) {
        return ext.toLowerCase().endsWith(".html");
    }
}