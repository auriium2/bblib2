package xyz.auriium.mattlib2;

import tech.units.indriya.unit.AlternateUnit;
import tech.units.indriya.unit.Units;

import javax.measure.Unit;
import javax.measure.quantity.Angle;

/**
 * using interface as
 */
public class BucketUnits {

    public static final Unit<Angle> DEGREE = AlternateUnit.of(Units.RADIAN.divide(Math.PI * 2d).multiply(360), "deg","degree");
    public static final Unit<Angle> ROTATION = AlternateUnit.of(Units.RADIAN.divide(Math.PI * 2d), "rot", "rotation");

}
