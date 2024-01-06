package xyz.auriium.mattlib2.foxe;

import java.util.Arrays;

public class BufferUtils {

    public static int[] add(int[] origin, int next) {
        int[] dat =  Arrays.copyOf(origin, origin.length+1);
        dat[origin.length] = next;
        return dat;
    }

}
