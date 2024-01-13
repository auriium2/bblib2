package xyz.auriium.mattlib2.utils;

import java.util.Arrays;

public class BufferUtils {

    public static int[] add(int[] origin, int next) {
        int[] dat =  Arrays.copyOf(origin, origin.length+1);
        dat[origin.length] = next;
        return dat;
    }

    public static <T> T[] add(T[] origin, T next) {
        T[] dat =  Arrays.copyOf(origin, origin.length+1);
        dat[origin.length] = next;
        return dat;
    }

}
