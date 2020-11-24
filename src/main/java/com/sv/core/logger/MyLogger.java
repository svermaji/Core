package com.sv.core.logger;

import com.sv.core.Constants;
import com.sv.core.Utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * File Logger
 */
public class MyLogger {

    private Writer logWriter = null;
    private static MyLogger logger = null;
    private boolean debug;
    private static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss.SSSSSS");

    public enum MsgType {
        INFO, WARN, ERROR
    }

    /**
     * Singleton instance with class name
     *
     * @param clazz        Class object
     * @param debugEnabled boolean if debug is enabled
     * @return File logger instance
     */
    public static MyLogger createLogger(Class<?> clazz, boolean debugEnabled) {
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
        sb.append(".log");
        return createLogger(sb.toString(), debugEnabled);
    }


    public static MyLogger createLogger(Class<?> clazz) {
        return createLogger(clazz, false);
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
        if (logger == null) {
            logger = new MyLogger();
            try {
                logger.createLogFile(Utils.hasValue(logFilename) ? logFilename : "test.log");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.setDebug(debugEnabled);
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
            log("DEBUG: " + message);
        }
    }

    public void warn(String message) {
        log("WARN: " + message);
    }

    public void error(String message) {
        log("ERROR: " + message);
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
     * @param message - debug statement
     */
    public void log(String message) {
        try {
            if (logWriter != null) {
                synchronized (logWriter) {
                    logWriter.write(getTime() + message + System.lineSeparator());
                    logWriter.flush();
                }
            } else {
                System.out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return "[" + LocalDateTime.now().format(formatter) + "]: ";
    }
}
