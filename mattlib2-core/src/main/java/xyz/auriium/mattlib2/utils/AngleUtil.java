package xyz.auriium.mattlib2.utils;


public class AngleUtil {

    //TODO this doesn't conform to the reference frame stuff
    public static double normalizeRotations(double numberOnInfiniteSet_rotations) {
        double huge = numberOnInfiniteSet_rotations % 1;
        if (huge < 0) {
            huge +=1;
        }

        return huge;
    }

}
