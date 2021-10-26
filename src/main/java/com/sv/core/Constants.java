package com.sv.core;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Contains constants and enums
 */
public final class Constants {

    private Constants() {
    }

    public static final int ONE = 1;
    public static final int TEN = 10;
    public static final long SEC_1 = TimeUnit.SECONDS.toMillis(ONE);
    public static final long MIN_1 = TimeUnit.MINUTES.toMillis(ONE);
    public static final long MIN_10 = MIN_1 * TEN;

    public static final String FAILED = "Failed - ";
    public static final String CANCELLED = "Cancelled - ";
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String ELLIPSIS = "..";
    public static final String SLASH = "\\";
    public static final String F_SLASH = "/";
    public static final String COMMA = ",";
    public static final String STAR = "*";
    public static final String HASH = "#";
    public static final String COLON = ":";
    public static final String SEMI_COLON = ";";
    public static final String DOUBLE_SPACE = SPACE + SPACE;
    public static final String DASH = "-";
    public static final String DOT = ".";
    public static final String SP_DASH_SP = SPACE + DASH + SPACE;

    // Set of values that imply a true value.
    public static final Character[] SPECIAL_CHARS =
            {'\\', ':', '/', ',', '-', '_', ' '};

    // Set of values that imply a true value.
    public static final String[] trueValues = {"Y", "YES", "TRUE", "T"};

    // Set of values that imply a false value.
    public static final String[] falseValues = {"N", "NO", "FALSE", "F"};

    public enum CaseType {UPPER, LOWER, TITLE, INVERT}

}
