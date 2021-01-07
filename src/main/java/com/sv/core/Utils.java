package com.sv.core;

import com.sv.core.exception.AppException;
import com.sv.core.logger.MyLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.sv.core.Constants.*;

/**
 * Utility static methods and constants.
 */
public class Utils {

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
        return isUpper(ch) || isLower(ch);
    }

    public static boolean isUpper(char ch) {
        int A = 'A';
        int Z = 'Z';

        return (int) ch <= Z && (int) ch >= A;
    }

    public static boolean isLower(char ch) {
        int a = 'a';
        int z = 'z';

        return (int) ch <= z && (int) ch >= a;
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

    public static void sleep1Sec() {
        sleep(TimeUnit.SECONDS.toMillis(ONE));
    }

    public static void sleep500Milli() {
        sleep(500);
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
        return isInCharArr(SPECIAL_CHARS, ch);
    }

    public static boolean isWholeWordChar(char ch) {
        return !isAlphabet(ch) && !isNumeric(ch);
    }

    public static boolean isInCharArr(Character[] arr, char ch) {
        return Arrays.stream(arr).anyMatch(a -> a == ch);
    }

    public static String getTimeDiffSecStr(long time) {
        return "[" + getTimeDiffSec(time) + " sec]";
    }

    public static long getNowMillis() {
        return System.currentTimeMillis();
    }

    public static long getTimeDiffMin(long time) {
        return TimeUnit.MILLISECONDS.toMinutes(getTimeDiff(time));
    }

    /**
     * This method will execute command passed
     *
     * @param cmd command to run
     * @return exception message in case of error else empty string
     */
    public static String runCmd(String cmd) {
        return runCmd(cmd, null);
    }

    /**
     * This method will execute command passed
     *
     * @param cmd    command to run
     * @param logger MyLogger object to print error details
     * @return exception message in case of error else empty string
     */
    public static String runCmd(String cmd, MyLogger logger) {
        try {
            Process process = runProcess(cmd, logger);
            /*if (logger != null) {
                logger.log("Output " + getProcessOutput(process, logger));
            }*/
        } catch (AppException e) {
            return e.getMessage();
        }
        return EMPTY;
    }

    public static void logProcessOutput(Process process, MyLogger logger) {
        String data = getProcessOutput(process, logger);
        if (logger != null) {
            if (Utils.hasValue(data)) {
                logger.log(data);
            } else {
                logger.log("No process output");
            }
        }
    }

    public static String getProcessOutput(Process process, MyLogger logger) {
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader stdError = new BufferedReader(new
                     InputStreamReader(process.getErrorStream()))) {

            String line;
            StringBuilder sb = new StringBuilder();
            do {
                line = reader.readLine();
                if (Utils.hasValue(line)) {
                    sb.append(line);
                }
            } while (line != null);

            if (!Utils.hasValue(sb.toString())) {
                sb = new StringBuilder();
                logger.warn("No data from process. Checking error stream.");
                do {
                    line = stdError.readLine();
                    if (Utils.hasValue(line)) {
                        sb.append(line);
                    }
                } while (line != null);
            }

            return sb.toString();
        } catch (IOException e) {
            logger.error(e);
        }

        return EMPTY;
    }

    public static Process runProcess(String cmd) throws AppException {
        return runProcess(cmd, null);
    }

    public static Process runProcess(String cmd, MyLogger logger) throws AppException {
        if (logger != null) {
            logger.log("Running command " + addBraces(cmd));
        }
        try {
            return Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            if (logger != null) {
                logger.error(e);
            }
            throw new AppException(e.getMessage());
        }
    }

    public static long getTimeDiffSec(long time) {
        return TimeUnit.MILLISECONDS.toSeconds(getTimeDiff(time));
    }

    public static long getTimeDiff(long time) {
        return getNowMillis() - time;
    }

    public static String getTimeNoSec() {
        return getTime(false);
    }

    /**
     * Returns local date time in format <pre>dd-MM-yyyy'T'HH:mm:ss</pre>
     *
     * @return date time
     */
    public static String getFormattedDate() {
        LocalDateTime date = LocalDateTime.now();
        return getFormattedDate(
                Date.from(date.atZone(ZoneId.systemDefault()).toInstant()).getTime()
        );
    }

    public static String getFormattedDate(long dt) {
        Date date = new Date(dt);
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy h:mm:ssa"));
    }

    public static String getHostname(MyLogger logger) {
        String env;
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            env = "";
            logger.warn("Unable to get host name, trying environment variable");
        }

        if (!hasValue(env)) {
            Map<String, String> envs = System.getenv();
            if (envs.containsKey("COMPUTERNAME")) {
                return envs.get("COMPUTERNAME");
            } else if (envs.containsKey("HOSTNAME")) {
                return envs.get("HOSTNAME");
            }

            if (!hasValue(env)) {
                logger.warn("Unable to get host name from environment");
                env = "Unknown";
            }
        }

        return env;
    }

    public static String getTime(boolean addSec) {
        LocalTime time = LocalTime.now();
        String format = "h:mma";
        if (addSec) {
            format = "h:mm:sa";
        }
        return time.format(DateTimeFormatter.ofPattern(format));
    }

    public static String addBraces(boolean s) {
        return addBraces(s + "");
    }

    public static String addBraces(long s) {
        return addBraces(s + "");
    }

    public static String addBraces(String s) {
        return "[" + s + "]";
    }

    public static String changeCase(CaseType type, String str) {
        switch (type) {
            case LOWER:
                return str.toLowerCase();
            case UPPER:
                return str.toUpperCase();
            case TITLE:
                return convertToTitleCase(str);
            case INVERT:
                return invertCase(str);
        }
        return str;
    }

    public static String invertCase(String str) {
        char[] arr = str.toCharArray();

        StringBuilder ans = new StringBuilder();
        for (char c : arr) {
            if (isAlphabet(c)) {
                ans.append(isUpper(c) ? Character.toString(c).toLowerCase() : Character.toString(c).toUpperCase());
            } else {
                ans.append(c);
            }
        }
        return ans.toString();
    }

    public static String convertToTitleCase(String str) {
        String[] titleCaseChars = new String[]{"_", " ", "-"};
        for (String ch : titleCaseChars) {
            String[] arr = str.split(ch);
            StringBuilder ans = new StringBuilder();
            if (arr.length == 1) {
                str = arr[0];
                if (hasValue(str)) {
                    str = Character.toString(str.charAt(0)).toUpperCase() + str.substring(1);
                }
            } else {
                for (String a : arr) {
                    if (hasValue(a)) {
                        ans.append(Character.toString(a.charAt(0)).toUpperCase()).append(a.substring(1));
                    }
                    ans.append(ch);
                }
                str = ans.toString();
            }
        }
        return str;
    }

    public static Object callMethod(Object obj, String name, Object[] args, MyLogger logger) {

        String argsDtl = "No arg";
        Class<?>[] clz = new Class[0];
        if (args != null) {
            clz = new Class[args.length];
            StringBuilder sb = new StringBuilder();
            sb.append(Arrays.asList(args).toString());
            sb.append(", Types: {");
            int x = 0;
            for (Object o : args) {
                clz[x] = o.getClass();
                sb.append(clz[x].getName()).append(", ");
                x++;
            }
            sb.append("}");
            argsDtl = sb.toString();
        }

        logger.debug("Calling method " + addBraces(name)
                + " on class " + addBraces(obj.getClass().getSimpleName())
                + " args " + argsDtl
        );
        try {
            if (args == null) {
                return obj.getClass().getDeclaredMethod(name).invoke(obj);
            }
            return obj.getClass().getDeclaredMethod(name, clz).invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("Error in calling method: " + name + " on class "
                    + obj.getClass().getSimpleName(), e);
        }
        return null;
    }
}
