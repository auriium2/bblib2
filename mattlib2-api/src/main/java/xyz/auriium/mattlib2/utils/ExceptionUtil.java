package xyz.auriium.mattlib2.utils;

import yuukonstants.exception.ExplainedException;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.function.Supplier;

public class ExceptionUtil {

    static final Queue<RuntimeException> EXCEPTIONS = new ArrayDeque<>();
    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    static final String ANSI_BLACK = "\u001B[30m";



    public static <T> Supplier<T> wrapExceptionalSupplier(Supplier<T> runnable) {
        return () -> {
            try {
                return runnable.get();
            } catch (ExplainedException e) {
                System.out.println();
                System.out.println();
                System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + e.toOutput() + ANSI_RESET + ANSI_BLACK);
                System.out.println();
                System.out.println();
                System.out.println("-----");
                System.out.println();
                System.out.println();
                System.out.println("stacktrace 1: " + e.getStackTrace()[0]);
                System.out.println("stacktrace 2: " + e.getStackTrace()[0]);
                System.out.println();
                System.out.println();
            }

            return null;
        };
    }

    public static Runnable wrapExceptionalRunnable(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (ExplainedException e) {
                System.out.println();
                System.out.println();
                System.out.println(ANSI_RED_BACKGROUND + ANSI_BLACK + e.toOutput() + ANSI_RESET + ANSI_BLACK);
                System.out.println();
                System.out.println();
                System.out.println("-----");
                System.out.println();
                System.out.println();
                System.out.println("stacktrace 1: " + e.getStackTrace()[0]);
                System.out.println("stacktrace 2: " + e.getStackTrace()[0]);
                System.out.println();
                System.out.println();
            }
        };
    }



}
