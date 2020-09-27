package com.sv.core;

import java.io.*;
import java.time.LocalDateTime;

/**
 * File Logger
 */
public class MyLogger {

    private Writer logWriter = null;
    private static MyLogger logger = null;

    /**
     * Singleton instance with class name
     *
     * @param clazz Class object
     * @return File logger instance
     */
    public static MyLogger createLogger(Class<?> clazz) {
        String className = clazz.getSimpleName();
        char[] carr = className.toCharArray();
        StringBuilder sb = new StringBuilder();
        int idx = 0;
        for (char c : carr) {
            if (Character.isUpperCase(c)) {
                if (idx > 0) {
                    sb.append(Utils.DASH);
                }
                sb.append(Character.toLowerCase(c));
                idx++;
            } else {
                sb.append(c);
            }
        }
        sb.append(".log");
        return createLogger(sb.toString());
    }

    /**
     * Singleton instance with class name
     *
     * @param logFilename name of file
     * @return File logger instance
     */
    public static MyLogger createLogger(String logFilename) {
        if (logger == null) {
            logger = new MyLogger();
            try {
                logger.createLogFile(Utils.hasValue(logFilename) ? logFilename : "test.log");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logger;
    }

    private MyLogger() {
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
        return "[" + LocalDateTime.now() + "]: ";
    }
}
