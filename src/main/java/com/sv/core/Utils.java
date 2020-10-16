package com.sv.core;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Utility static methods and constants.
 */
public class Utils {

    public static final String FAILED = "Failed - ";
    public static final String CANCELLED = "Cancelled - ";
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String ELLIPSIS = "..";
    public static final String SLASH = "\\";
    public static final String F_SLASH = "/";
    public static final String COMMA = ",";
    public static final String SEMI_COLON = ";";
    public static final String DOUBLE_SPACE = SPACE + SPACE;
    public static final String DASH = "-";
    public static final String DOT = ".";
    public static final String SP_DASH_SP = SPACE + DASH + SPACE;

    // Set of values that imply a true value.
    private static final Character[] SPECIAL_CHARS =
            {'\\', ':', '/', ',', '-', '_', ' '};

    // Set of values that imply a true value.
    private static final String[] trueValues = {"Y", "YES", "TRUE", "T"};

    // Set of values that imply a false value.
    private static final String[] falseValues = {"N", "NO", "FALSE", "F"};

    /**
     * Escape html characters from `HtmlEsc` enum
     *
     * @param data string to escape
     * @return escaped string
     */
    public static String escape(String data) {
        for (HtmlEsc h : HtmlEsc.values()) {
            if (data.contains(h.getCh())) {
                data = data.replaceAll(h.getCh(), h.getEscStr());
            }
        }
        return data;
    }

    public enum HtmlEsc {
        SP(" ", "&nbsp;"),
        LT("<", "&lt;"),
        GT(">", "&gt;"),
        SQ("'", "&#39;"),
        DQ("\"", "&quot;"),
        AMP("&", "&amp;");

        String ch, escStr;

        public String getCh() {
            return ch;
        }

        public String getEscStr() {
            return escStr;
        }

        HtmlEsc(String ch, String escStr) {
            this.ch = ch;
            this.escStr = escStr;
        }
    }


    /**
     * Return true if param has non-null value
     *
     * @param item string to be checked
     * @return boolean status of operation
     */
    public static boolean hasValue(String item) {
        return ((item != null) && (item.length() > 0));
    }

    public static Path createPath(String path) {
        return FileSystems.getDefault().getPath(path);
    }

    public static boolean isInArray(String[] arr, String val) {
        return Arrays.stream(arr).anyMatch(a -> a.equalsIgnoreCase(val));
    }

    /**
     * Return the boolean equivalent of the string argument.
     *
     * @param value Value containing string representation of a boolean value.
     * @return Boolean true/false depending on the value of the input.
     * @throws Exception Thrown if input does not have a valid value.
     */
    public static boolean getBoolean(String value) throws Exception {
        if (!hasValue(value)) {
            throw new Exception("ERROR: Can't convert a null/empty string value to a boolean."); //throw new Exception("ERROR: Can't convert a null/empty string value to a boolean."); throw new Exception("ERROR: Can't convert a null/empty string value to a boolean.");
        }

        value = value.trim();

        for (String trueValue : trueValues) {
            if (value.equalsIgnoreCase(trueValue))
                return true;
        }

        for (String falseValue1 : falseValues) {
            if (value.equalsIgnoreCase(falseValue1))
                return false;
        }

        //Construct error message containing list of valid values
        StringBuilder validValues = new StringBuilder();

        for (int Ix = 0; Ix < trueValues.length; Ix++) {
            if (Ix > 0)
                validValues.append(", ");

            validValues.append(trueValues[Ix]);
        }

        for (String falseValue : falseValues) {
            validValues.append(", ");
            validValues.append(falseValue);
        }

        throw new Exception("ERROR: Candidate boolean value [" + value
                + "] not in valid-value set [" + validValues.toString() + "].");
    }


    /**
     * Return the boolean equivalent of the string argument.
     *
     * @param value       Value containing string representation of a boolean value.
     * @param defaultBool Default boolean to use if the value is empty
     *                    or if it is an invalid value.
     * @return Boolean true/false depending on the value of the input.
     * @throws Exception Thrown if input does not have a valid value.
     */
    public static boolean getBoolean(String value, boolean defaultBool) throws Exception {
        if (!hasValue(value))
            return defaultBool;

        try {
            return getBoolean(value);
        } catch (Exception e) {
            return defaultBool;
        }
    }

    /**
     * Returns true if char is numeric, else false
     *
     * @param ch char to check
     * @return boolean status of operation
     */
    public static boolean isNumeric(char ch) {
        int zero = '0';
        int nine = '9';

        return (int) ch <= nine && (int) ch >= zero;
    }

    /**
     * Returns true if char is alphabetic, else false
     *
     * @param ch char to check
     * @return boolean status of operation
     */
    public static boolean isAlphabet(char ch) {
        int a = 'a';
        int A = 'A';
        int z = 'z';
        int Z = 'Z';

        return ((int) ch <= z && (int) ch >= a) || (((int) ch <= Z && (int) ch >= A));
    }

    /**
     * Get file name from path
     *
     * @param url path
     * @return only name
     */
    public static String getFileName(String url) {
        if (!hasValue(url))
            return "";
        if (url.contains(F_SLASH))
            return url.substring(url.lastIndexOf(F_SLASH) + 1);
        if (url.contains(SLASH))
            return url.substring(url.lastIndexOf(SLASH) + 1);

        return url;
    }

    /**
     * Removes file name extension
     *
     * @param filename path
     * @return only name
     */
    public static String chopFileNameExtn(String filename) {
        if (!hasValue(filename))
            return EMPTY;

        if (filename.contains(DOT))
            return filename.substring(0, filename.lastIndexOf(DOT));

        return filename;
    }

    /**
     * Sleep that handles exception
     *
     * @param millis milli seconds
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sleep that handles exception
     *
     * @param millis milli seconds
     * @param logger to log
     */
    public static void sleep(long millis, MyLogger logger) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }
    }

    /**
     * Returns string for a file size that could
     * be in GB or MB or KB or in bytes.
     *
     * @param fs file size
     * @return String
     */
    public static String getFileSizeString(long fs) {
        long KB = 1024;
        float inKB = (float) fs / KB;
        float inMB = inKB / KB;
        float inGB = inMB / KB;
        if (inGB > 1) {
            return String.format("[%sGB]", formatFloat(inGB));
        } else if (inMB > 1) {
            return String.format("[%sMB]", formatFloat(inMB));
        } else if (inKB > 1) {
            return String.format("[%sKB]", formatFloat(inKB));
        }
        return String.format("[%sBytes]", fs);
    }

    private static String formatFloat(float size) {
        return String.format("%.2f", size);
    }

    /**
     * Returns enum names as String array
     *
     * @param e Enum class
     * @return array
     */
    public static String[] getConfigsAsArr(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    /**
     * This method matches; in lowercase; given string from array of strings.
     * If there is a match then returns the remaining sub string.
     *
     * @param s   to search
     * @param arr of String
     * @return sub string
     */
    public static String getMatchedLCSubStr(String s, String[] arr) {
        String lc = s.toLowerCase();
        return Arrays.stream(arr)
                .filter(a -> a.toLowerCase().startsWith(lc))
                .findFirst()
                .map(a -> a.substring(s.length()))
                .orElse("");
    }

    public static boolean isSpecialChar(char ch) {
        return Arrays.asList(SPECIAL_CHARS).contains(ch);
    }

    public static String getTimeDiffInSec(long time) {
        return "[" + TimeUnit.MILLISECONDS.toSeconds(getTimeDiff(time)) + " sec]";
    }

    public static long getTimeDiff(long time) {
        return System.currentTimeMillis() - time;
    }
}
