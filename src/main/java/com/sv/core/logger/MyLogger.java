package com.sv.core.logger;

import com.sv.core.Constants;
import com.sv.core.Utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * File Logger
 */
public class MyLogger {

    private Writer logWriter = null;
    private static MyLogger logger = null;
    private boolean debug, simpleClassName;
    private static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd.MMM.yyyy'T'HH:mm:ss.SSSSSS");
    private String[] clazzAllowed = {"com.sv"};
    private String[] methodsToSkip = {"lambda$"};

    public enum MsgType {
        INFO, WARN, ERROR
    }

    public void reset() {
        logWriter = null;
        logger = null;
    }

    /**
     * Singleton instance with class name
     *
     * @param clazz        Class object
     * @param debugEnabled boolean if debug is enabled
     * @return File logger instance
     */
    public static MyLogger createLogger(Class<?> clazz, boolean debugEnabled) {
        return createLogger(clazz, Constants.EMPTY, debugEnabled);
    }

    /**
     * Singleton instance with class name
     *
     * @param clazz        Class object
     * @param appendToName append string to log file name if not empty
     * @param debugEnabled boolean if debug is enabled
     * @return File logger instance
     */
    public static MyLogger createLogger(Class<?> clazz, String appendToName, boolean debugEnabled) {
        String className = clazz.getSimpleName();
        char[] carr = className.toCharArray();
        StringBuilder sb = new StringBuilder();
        int idx = 0;
        for (char c : carr) {
            if (Character.isUpperCase(c)) {
                if (idx > 0) {
                    sb.append(Constants.DASH);
                }
                sb.append(Character.toLowerCase(c));
                idx++;
            } else {
                sb.append(c);
            }
        }
        if (Utils.hasValue(appendToName)) {
            sb.append(Constants.DASH).append(appendToName.toLowerCase());
        }
        sb.append(".log");
        return createLogger(sb.toString(), debugEnabled);
    }

    public static MyLogger createLogger(Class<?> clazz) {
        return createLogger(clazz, false);
    }

    public static MyLogger createLogger(Class<?> clazz, String appendToName) {
        return createLogger(clazz, appendToName, false);
    }

    public static MyLogger createLogger(String logFilename) {
        return createLogger(logFilename, false);
    }

    /**
     * Singleton instance with class name
     *
     * @param logFilename  name of file
     * @param debugEnabled boolean if debug is enabled
     * @return File logger instance
     */
    public static MyLogger createLogger(String logFilename, boolean debugEnabled) {
        return createLogger(logFilename, false, false);
    }

    public static MyLogger createLogger(String logFilename, boolean debugEnabled, boolean simpleClassName) {
        if (logger == null) {
            logger = new MyLogger();
            try {
                logger.createLogFile(Utils.hasValue(logFilename) ? logFilename : "test.log");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.setDebug(debugEnabled);
        logger.setSimpleClassName(simpleClassName);
        logger.info("Log file set as " + Utils.addBraces(logFilename));
        return logger;
    }

    private MyLogger() {
    }

    /**
     * Sets new debug.
     *
     * @param debugEnabled New value of debug.
     */
    public void setDebug(boolean debugEnabled) {
        this.debug = debugEnabled;
    }

    /**
     * Gets debug.
     *
     * @return Value of debug.
     */
    public boolean isDebug() {
        return debug;
    }

    public boolean isSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(boolean simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    /**
     * Closes the logger
     */
    public void dispose() {
        try {
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void debug(String message) {
        if (debug) {
            log("DEBUG", message);
        }
    }

    public void info(String message) {
        log("INFO", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    public void error(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        error(sw.toString());
    }

    public void error(String msg, Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        error(msg + sw.toString());
    }

    /**
     * Writes the debug statement in log file.
     * If log file could not be initialized
     * thn output would be redirected to console.
     *
     * @param level   - log level
     * @param message - debug statement
     */
    public void log(String level, String message) {
        String callerClass = "";
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        if (ste.length > 4) {
            StackTraceElement se = getPrimarySTEToLog(ste[3], ste[4]);
            String cn = simpleClassName ? simpleName(se.getClassName()) : se.getClassName();
            callerClass = Utils.addBraces(cn + Constants.HASH + se.getMethodName());
            /*System.out.println(ste.length);
            int limit = Math.min(ste.length, 7);
            for (int i = 0; i < limit; i++) {
                System.out.println(i + "--" + ste[i].getClassName() + "#" + ste[i].getMethodName() + "--" + message);
            }*/
        }

        try {
            if (logWriter != null) {
                synchronized (logWriter) {
                    logWriter.write(getTime() + callerClass + Utils.addBraces(level) + Constants.SPACE + message + System.lineSeparator());
                    logWriter.flush();
                }
            } else {
                System.out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StackTraceElement getPrimarySTEToLog(StackTraceElement se3, StackTraceElement se4) {

        return se3;

        // TODO: check this
        /*StackTraceElement se = se3.getClassName().equalsIgnoreCase(se4.getClassName()) ?
                se3 : se4;
        String cn = se.getClassName();
        String mn = se.getMethodName();
        boolean allowed = false;
        for (String s : clazzAllowed) {
            if (cn.startsWith(s)) {
                allowed = true;
                break;
            }
        }
        if (allowed) {
            for (String s : methodsToSkip) {
                if (mn.contains(s)) {
                    se = se3;
                    break;
                }
            }
        } else {
            se = se3;
        }
        return se;*/
    }

    private String simpleName(String className) {
        if (className.contains(Constants.DOT)) {
            className = className.substring(className.lastIndexOf(Constants.DOT) + Constants.DOT.length());
        }
        return className;
    }

    private void createLogFile(String logFile) throws IOException {
        if (logWriter == null) {
            try {
                logWriter = new BufferedWriter(new FileWriter(logFile));
            } catch (IOException e) {
                logWriter = null;
                throw new IOException(e.getMessage());
            } catch (Exception e) {
                logWriter = null;
            }
        }
    }

    private String getTime() {
        return "[" + LocalDateTime.now().format(formatter) + "]";
    }
}
