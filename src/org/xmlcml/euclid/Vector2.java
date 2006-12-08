package org.xmlcml.euclid;

/**
 * a 2-D vector relationship with Complex and Polar not fully worked out. It may
 * simply be a matter of style which is used.
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class Vector2 extends Real2 {

    /**
     * constructor.
     * 
     * @param r
     *            coordinates
     */
    public Vector2(Real2 r) {
        super(r);
    }

    /**
     * constructor.
     * 
     * @param x
     * @param y
     */
    public Vector2(double x, double y) {
        this(new Real2(x, y));
    }

    /**
     * I *think* I have written this so that the angle is positive as this
     * rotates anticlockwise to v.
     * 
     * @param v
     * @return angle
     */
    public Angle getAngleMadeWith(Vector2 v) {
        double theta0 = Math.atan2(v.x, v.y);
        double theta1 = Math.atan2(this.x, this.y);
        return new Angle(theta0 - theta1);
    }

}
