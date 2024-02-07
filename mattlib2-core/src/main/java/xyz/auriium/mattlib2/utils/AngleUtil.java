package xyz.auriium.mattlib2.utils;

public class AngleUtil {

    public static double normalizeRotations(double numberOnInfiniteSet_rotations) {
        double huge = numberOnInfiniteSet_rotations % 1;
        if (huge < 0) {
            huge +=1;
        }
        return huge;
    }

}
