package com.sv.core;

import com.sv.core.exception.AppException;
import com.sv.core.logger.MyLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ValueRange;
import java.util.*;
import java.util.List;
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

    public enum WinDirection {
        left(KeyEvent.VK_LEFT),
        right(KeyEvent.VK_RIGHT),
        up(KeyEvent.VK_UP),
        down(KeyEvent.VK_DOWN);

        int val;

        WinDirection(int v) {
            this.val = v;
        }
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

    public enum FilenameReplacer {
        COLON(":", ".");

        String ch, rep;

        FilenameReplacer(String ch, String rep) {
            this.ch = ch;
            this.rep = rep;
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

    /**
     * Check if val param is present in array.  Search is not case-sensitive
     *
     * @param arr String array
     * @param val string to search
     * @return boolean
     */
    public static boolean isInArray(String[] arr, String val) {
        String finalVal = val.trim();
        return Arrays.stream(arr).anyMatch(a -> a.equalsIgnoreCase(finalVal));
    }

    public static boolean isInArrayMatchStart(String[] arr, String val) {
        String finalVal = val.trim().toLowerCase();
        return Arrays.stream(arr).anyMatch(a -> a.toLowerCase().startsWith(finalVal));
    }

    public static boolean isInArrayMatchStart(String[] arr, String val, boolean matchCase) {
        String finalVal = val.trim();
        if (matchCase) {
            return Arrays.stream(arr).anyMatch(a -> a.startsWith(finalVal));
        }
        return isInArrayMatchStart(arr, val);
    }
    public static boolean isInArray(String[] arr, String val, boolean matchCase) {
        if (matchCase) {
            return Arrays.asList(arr).contains(val);
        }
        return isInArray(arr, val);
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
            throw new Exception("ERROR: Can't convert a null/empty string value to a boolean.");
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
     */
    public static boolean getBoolean(String value, boolean defaultBool) {
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

    public static void sleep100Milli() {
        sleep(100);
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

    public static boolean deleteFile(String path) {
        Path p = createPath(path);
        try {
            return Files.deleteIfExists(p);
        } catch (IOException e) {
            //no action
        }
        return false;
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

    public static String formatTime(long sec) {
        long min = TimeUnit.SECONDS.toMinutes(sec);
        long rsec = sec - TimeUnit.MINUTES.toSeconds(min);
        String s = min + COLON;
        return (rsec < 10) ? s + "0" + rsec : s + rsec;
    }

    public static String formatTimeHMS(long sec) {
        long h = 0, m = 0, rsec = sec;
        if (sec > 60) {
            m = TimeUnit.SECONDS.toMinutes(sec);
            if (m > 60) {
                h = TimeUnit.SECONDS.toHours(sec);
                m = m - TimeUnit.HOURS.toMinutes(h);
                sec = sec - TimeUnit.HOURS.toSeconds(h);
                rsec = sec - TimeUnit.MINUTES.toSeconds(m);
            } else {
                rsec = sec - TimeUnit.MINUTES.toSeconds(m);
            }
        }
        String tm = h > 0 ? h + "h:" : "";
        tm += m > 0 ? (m >= 10 ? m : "0" + m) + "m:" : "";
        tm += rsec > 0 ? (rsec >= 10 ? rsec : "0" + rsec) + "s" : "";
        return tm;
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
        long t = System.currentTimeMillis();
        sleep(2345);
        System.out.println(getTimeDiffSecMilliStr(t));
    }

    public static int getIdxInArr(String[] arr, String check) {
        int r = -1;
        int l = arr.length;
        for (int i = 0; i < l; i++) {
            if (arr[i].equals(check)) {
                r = i;
                break;
            }
        }
        return r;
    }

    public static int getValueFromRange(int min, int max, int def, int valToCheck) {
        ValueRange range = ValueRange.of(min, max);
        if (!range.isValidIntValue(valToCheck)) {
            valToCheck = def;
        }
        return valToCheck;
    }

    public static boolean isInRange(int min, int max, int valToCheck) {
        ValueRange range = ValueRange.of(min, max);
        return range.isValidIntValue(valToCheck);
    }

    public static Class getClassForName(String className, MyLogger logger) {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error("Unable to get class name for " + addBraces(className));
        }
        return clazz;
    }

    public static Object createObjFor(String className, Class[] clazzParams, Object[] params, MyLogger logger) {
        Object obj = null;
        try {
            Class clazz = getClassForName(className, logger);
            obj = clazz.getConstructor(clazzParams).newInstance(params);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            logger.error("Unable to create instance for [" + className + "], class params are "
                    + Arrays.deepToString(clazzParams)
                    + ", params " + Arrays.deepToString(params) + "]");
        }
        return obj;
    }

    public static String getTimeDiffSecStr(long time) {
        return "[" + getTimeDiffSec(time) + " sec]";
    }

    public static String getTimeDiffSecMilliStr(long time) {
        return getTimeDiffSecMilliStr(time, true);
    }

    public static String getTimeDiffSecMilliStr(long time, boolean addBraces) {
        return (addBraces ? "[" : "") +
                getTimeDiffSecMilli(time) + " sec" +
                (addBraces ? "]" : "");
    }

    public static long getNowMillis() {
        return System.currentTimeMillis();
    }

    public static long getTimeDiffMin(long millis) {
        return TimeUnit.MILLISECONDS.toMinutes(getTimeDiff(millis));
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

    /**
     * Returns absolute paths of files inside folder
     *
     * @param dir    directory path
     * @param logger Obj
     * @return list
     */
    public static List<String> listFiles(String dir, MyLogger logger) {
        List<String> list = new ArrayList<>();
        try {
            Stream<Path> paths = Files.list(Utils.createPath(dir));
            paths.forEach(p -> {
                if (p.toFile().isFile()) {
                    list.add(p.toAbsolutePath().toString());
                }
            });
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

    public static boolean writeFile(String path, List<String> lines, MyLogger logger) {
        StringBuilder sb = new StringBuilder();
        lines.forEach(l -> sb.append(l).append(System.lineSeparator()));
        return writeFile(path, sb.toString(), logger);
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

    public static long getTimeDiffSec(long millis) {
        return TimeUnit.MILLISECONDS.toSeconds(getTimeDiff(millis));
    }

    public static String getTimeDiffSecMilli(long millis) {
        long sec = TimeUnit.MILLISECONDS.toSeconds(getTimeDiff(millis));
        long remain = millis - TimeUnit.SECONDS.toMillis(sec);
        return String.format("%.3f", Double.parseDouble(sec + DOT + remain));
    }

    public static long getTimeDiff(long millis) {
        return getNowMillis() - millis;
    }

    /**
     * Returns local date time in format <pre>dd-MMM-yyyy h:mm:ssa</pre>
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

    public static String getDateDDMMMYYYY() {
        return LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
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

    public static Integer[] createIntArr(int from, int to) {
        if (to >= from) {
            Integer[] arr = new Integer[to - from + 1];
            for (int ix = 0, i = from; i <= to; i++, ix++) {
                arr[ix] = i;
            }
            return arr;
        }
        return new Integer[0];
    }

    public static String getCurrentDir() {
        String ps = getCurrentDirPath().toString();
        if (ps.contains(F_SLASH) && !ps.endsWith(F_SLASH)) {
            ps = ps + F_SLASH;
        } else if (ps.contains(SLASH) && !ps.endsWith(SLASH)) {
            ps = ps + SLASH;
        }
        return ps;
    }

    public static String removeDotFromEndOfPath(String ps) {
        if (ps.endsWith(DOT)) {
            ps = ps.substring(0, ps.length() - DOT.length());
        }
        return ps;
    }

    public static Path getCurrentDirPath() {
        return FileSystems.getDefault().getPath("").toAbsolutePath();
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

    /**
     * This will take screen shot of primary screen
     * and save at given path with name with date time
     *
     * @param path   folder name to save
     * @param logger MyLogger object
     * @return true if success
     */
    public static boolean takeScreenshot(String path, MyLogger logger) {
        boolean result = true;
        String format = "png";
        String loc = path + SLASH + "screenshot-" + formatForFilename(getFormattedDate()) + DOT + format;
        BufferedImage image = null;
        try {
            image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(image, format, new File(loc));
        } catch (AWTException | IOException e) {
            logger.error("Error in taking screenshot and saving at path [" + addBraces(path));
            result = false;
        }
        return result;
    }

    public static void setWindowPosition(WinDirection winDirection, MyLogger logger) {
        try {
            Robot r = new Robot();
            r.keyPress(KeyEvent.VK_WINDOWS);
            r.keyPress(winDirection.val);
            //Release Windows button
            r.keyRelease(winDirection.val);
            r.keyRelease(KeyEvent.VK_WINDOWS);
        } catch (Exception e) {
            logger.error("Unable to move window to [" + winDirection + "]", e);
        }
    }

    public static String formatForFilename(String data) {
        for (FilenameReplacer f : FilenameReplacer.values()) {
            if (data.contains(f.ch)) {
                data = data.replaceAll(f.ch, f.rep);
            }
        }
        return data;
    }

    public static String getUnicodeStr(String codeAsStr) {
        return getUnicodeStr(convertToInt(codeAsStr));
    }

    public static String getUnicodeStr(int code) {
        return new String(Character.toChars(code));
    }

    public static String convertToTitleCase(String str) {
        String[] titleCaseChars = new String[]{"_", " ", "-"};
        for (String ch : titleCaseChars) {
            String[] arr = str.split(ch);
            StringBuilder ans = new StringBuilder();
            if (arr.length == 1) {
                str = arr[0];
                if (hasValue(str)) {
                    str = Character.toUpperCase(str.charAt(0)) + str.substring(1);
                }
            } else {
                for (String a : arr) {
                    if (hasValue(a)) {
                        ans.append(Character.toUpperCase(a.charAt(0))).append(a.substring(1));
                    }
                    ans.append(ch);
                }
                str = ans.toString();
            }
        }
        return str;
    }

    public static Object callMethod(Object obj, String methodName, Object[] args, MyLogger logger) {
        try {
            return callMethodWithException(obj, methodName, args, logger);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("Error in calling method: " + methodName + " on class "
                    + obj.getClass().getSimpleName() + ". Details: ", e);
        }
        return null;
    }

    public static Object callMethodWithException(Object obj, String methodName, Object[] args, MyLogger logger)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

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

        if (args == null) {
            return obj.getClass().getMethod(methodName).invoke(obj);
        }
        return obj.getClass().getMethod(methodName, clz).invoke(obj, args);
    }
}
