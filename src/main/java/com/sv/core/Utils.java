package com.sv.core;

import com.sv.core.exception.AppException;
import com.sv.core.logger.MyLogger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * Opposite of escaping
     *
     * @param data string to unescape
     * @return unescaped string
     */
    public static String unescape(String data) {
        for (HtmlEsc h : HtmlEsc.values()) {
            if (data.contains(h.getCh())) {
                data = data.replaceAll(h.getEscStr(), h.getCh());
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

    public static long getFileSize(String path, long defaultValue) {
        Path p = createPath(path);
        if (Files.exists(p)) {
            try {
                return Files.size(p);
            } catch (IOException e) {
                // no action
            }
        }
        return defaultValue;
    }

    public static long getFileSize(String path) {
        return getFileSize(path, -1);
    }

    public static String getFileSizeString(String path) {
        long sz;
        try {
            sz = Files.size(createPath(path));
        } catch (IOException e) {
            sz = 0;
        }
        return getSizeString(sz, true, true, 2);
    }

    public static String getSizeString(long sz) {
        return getSizeString(sz, true, true, 2);
    }

    public static String formatNumber(double n) {
        return NumberFormat.getNumberInstance().format(n);
    }

    public static String formatTime(int sec) {
        long min = TimeUnit.SECONDS.toMinutes(sec);
        long rsec = sec - TimeUnit.MINUTES.toSeconds(min);
        String s = min + COLON;
        return (rsec < 10) ? s + "0" + rsec : s + rsec;
    }

    /**
     * Returns string for a size that could
     * be in GB or MB or KB or in bytes.
     *
     * @param sz             size in bytes
     * @param addBraces      - if need [ ] around result
     * @param addBSuffix     - if B added like MB or just M
     * @param digitsAfterDot - How many digits after dot
     * @return size notation e.g. 1024 becomes 1KB
     */
    public static String getSizeString(long sz, boolean addBraces, boolean addBSuffix, int digitsAfterDot) {
        float inKB = (float) sz / KB;
        float inMB = inKB / KB;
        float inGB = inMB / KB;
        String pre = "%s", suf = "B";
        if (digitsAfterDot < 0) {
            digitsAfterDot = 0;
        }
        if (!addBSuffix) {
            suf = "";
        }
        if (addBraces) {
            pre = "[" + pre;
            suf += "]";
        }
        if (inGB > 1) {
            return String.format(pre + "G" + suf, formatFloat(inGB, digitsAfterDot));
        } else if (inMB > 1) {
            return String.format(pre + "M" + suf, formatFloat(inMB, digitsAfterDot));
        } else if (inKB > 1) {
            return String.format(pre + "K" + suf, formatFloat(inKB, digitsAfterDot));
        }
        return String.format(pre + suf, sz);
    }

    private static String formatFloat(float size) {
        return formatFloat(size, 2);
    }

    private static String formatFloat(float size, int digitsAfterDot) {
        return String.format("%." + digitsAfterDot + "f", size);
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

    public static String filterFromCharArr(Character[] allowed, String toCheck) {
        return toCheck.chars()
                .filter(c -> isInCharArr(allowed, (char) c))
                .mapToObj(c -> "" + (char) c)
                .collect(Collectors.joining());
    }

    public static String filterNumbers(String toCheck) {
        return filterFromCharArr(NUM_ARR, toCheck);
    }

    public static int convertToInt(String s, int defaultVal) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            // no action
        }
        return defaultVal;
    }

    /**
     * This method will return 0 in case of exception
     *
     * @param s int as string
     * @return int
     */
    public static int convertToInt(String s) {
        return convertToInt(s, 0);
    }

    public static int convertToInt(String s, int defaultVal, int min, int max) {
        int val = convertToInt(s, defaultVal);
        return (val >= min && val <= max) ? val : defaultVal;
    }

    public static int validateInt(int val, int defaultVal, int min, int max) {
        return (val >= min && val <= max) ? val : defaultVal;
    }

    public static long convertToLong(String s, long defaultVal) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            // no action
        }
        return defaultVal;
    }

    public static long convertToLong(String s, long defaultVal, long min, long max) {
        long val = convertToLong(s, defaultVal);
        return (val >= min && val <= max) ? val : defaultVal;
    }

    public static long validateLong(long val, long defaultVal, long min, long max) {
        return (val >= min && val <= max) ? val : defaultVal;
    }

    public static void main(String[] args) {
        System.out.println(1024 * 1024 * 55);
        System.out.println(getSizeString(1024 * 1024 * 55));
    }

    public static String getTimeDiffSecStr(long time) {
        return "[" + getTimeDiffSec(time) + " sec]";
    }

    public static String getTimeDiffSecMilliStr(long time) {
        return "[" + getTimeDiffSecMilli(time) + " sec]";
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
                logger.info("Output " + getProcessOutput(process, logger));
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
                logger.info(data);
            } else {
                logger.info("No process output");
            }
        }
    }

    public static String getProcessOutput(Process process, MyLogger logger) {

        String data = getStreamOutput(process.getInputStream(), logger);
        if (!Utils.hasValue(data)) {
            logger.warn("No data from process. Checking error stream.");
            data = getStreamOutput(process.getErrorStream(), logger);
        }

        return data;
    }

    public static String getStreamOutput(InputStream stream, MyLogger logger) {
        try (BufferedReader reader = new BufferedReader(new
                InputStreamReader(stream))) {

            String line;
            StringBuilder sb = new StringBuilder();
            do {
                line = reader.readLine();
                if (Utils.hasValue(line)) {
                    sb.append(line);
                }
            } while (line != null);

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
        return runProcess(new String[]{cmd}, logger);
    }

    public static Process runProcess(String[] cmds, MyLogger logger) throws AppException {
        cmds = checkAllArgs(cmds);
        if (logger != null) {
            logger.info("Running command " + Arrays.asList(cmds));
        }
        try {
            return Runtime.getRuntime().exec(cmds);
        } catch (IOException e) {
            if (logger != null) {
                logger.error(e);
            }
            throw new AppException(e.getMessage());
        }
    }

    private static String[] checkAllArgs(String[] cmds) {
        List<String> cmdList = new ArrayList<>();
        for (String cmd : cmds) {
            if (cmd.contains(SPACE)) {
                cmdList.addAll(Arrays.asList(cmd.split(SPACE)));
            } else {
                cmdList.add(cmd);
            }
        }
        return cmdList.toArray(new String[0]);
    }

    public static void saveProperties(Properties prop, String filePath, MyLogger logger) {
        logger.info("Saving properties at " + addBraces(filePath));
        try {
            prop.store(new FileOutputStream(filePath), null);
        } catch (IOException e) {
            logger.info("Error in saving properties.");
        }
    }

    public static List<String> listFiles(String dir, MyLogger logger) {
        List<String> list = new ArrayList<>();
        try {
            Stream<Path> paths = Files.list(Utils.createPath(dir));
            paths.forEach(p -> list.add(p.toAbsolutePath().toString()));
        } catch (IOException e) {
            if (logger != null) {
                logger.error("Unable to load files from " + Utils.addBraces(dir));
            }
        }
        return list;
    }

    public static List<String> readFile(String path, MyLogger logger) {
        try {
            return Files.readAllLines(createPath(path));
        } catch (IOException e) {
            if (logger != null) {
                logger.error(e.getMessage(), e);
            }
        }
        return new ArrayList<>();
    }

    public static boolean writeFile(String path, String data, MyLogger logger) {
        return writeFile(path, data, logger, new StandardOpenOption[]
                {StandardOpenOption.CREATE, StandardOpenOption.WRITE});
    }

    public static boolean writeFile(String path, String data, MyLogger logger, OpenOption... options) {
        boolean result = true;
        try {
            Path p = Files.write(createPath(path), data.getBytes(), options);
            if (logger != null) {
                logger.info("File successfully written at " + addBraces(p.toString()));
            }
        } catch (IOException e) {
            if (logger != null) {
                logger.error(e.getMessage(), e);
            } else {
                e.printStackTrace();
            }
            result = false;
        }
        return result;
    }

    public static Properties readPropertyFile(String path, MyLogger logger) {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(path));
            return prop;
        } catch (IOException e) {
            if (logger != null) {
                logger.error(e.getMessage(), e);
            }
        }
        return new Properties();
    }

    public static long getTimeDiffSec(long time) {
        return TimeUnit.MILLISECONDS.toSeconds(getTimeDiff(time));
    }

    public static String getTimeDiffSecMilli(long time) {
        long sec = TimeUnit.MILLISECONDS.toSeconds(getTimeDiff(time));
        long remain = time - TimeUnit.SECONDS.toMillis(sec);
        return String.format("%.3f", Double.parseDouble(sec + DOT + remain));
    }

    public static long getTimeDiff(long time) {
        return getNowMillis() - time;
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

    public static File getCurrentDirFile() {
        return getCurrentDirPath().toFile();
    }

    public static String getCurrentDir() {
        return getCurrentDirPath().toString();
    }

    public static Path getCurrentDirPath() {
        return FileSystems.getDefault().getPath(".").toAbsolutePath();
    }

    public static String getTime(boolean addSec, boolean ampm) {
        LocalTime time = LocalTime.now();
        String format = ampm ? "h:mma" : "H:mm";
        if (addSec) {
            format += ampm ? ":ssa" : ":ss";
        }
        return time.format(DateTimeFormatter.ofPattern(format));
    }

    public static String getTimeNoSec() {
        return getTime(false, true);
    }

    public static String getTimeGlobal() {
        return getTime(true, false);
    }

    // time in MM:SS format
    public static String getTimeMS(long ms) {
        long min = TimeUnit.MILLISECONDS.toMinutes(ms);
        ms -= TimeUnit.MINUTES.toMillis(min);
        long sec = TimeUnit.MILLISECONDS.toSeconds(ms);
        return min + Constants.COLON + (sec > 9 ? sec : "0" + sec);
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

    public static Object callMethod(Object obj, String methodName, Object[] args, MyLogger logger) {

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

        logger.debug("Calling method " + addBraces(methodName)
                + " on class " + addBraces(obj.getClass().getSimpleName())
                + " args " + argsDtl
        );
        try {
            if (args == null) {
                return obj.getClass().getMethod(methodName).invoke(obj);
            }
            return obj.getClass().getMethod(methodName, clz).invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("Error in calling method: " + methodName + " on class "
                    + obj.getClass().getSimpleName() + ". Details: ", e);
        }
        return null;
    }
}
