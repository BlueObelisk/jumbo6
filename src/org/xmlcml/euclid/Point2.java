package org.xmlcml.euclid;


/**
 * 2-dimensional point class
 * 
 * 
 * Point2 represents a 2-dimensional point. It is one of a set of primitives
 * which can be combined to create and manipulate complex 2-dimensional objects.
 * Points can be transformed with rotation matrices or rotation-translation
 * matrices (Transform2), can be calculated from other primitives or can be used
 * to generate other primitives.
 * 
 * Default point is 0.0, 0.0, 0.0
 * 
 * @author <A HREF=mailto:@p.murray-rust@mail.cryst.bbk.ac.uk>Peter Murray-Rust</A>
 * 
 * @author (C) P. Murray-Rust, 1996
 */

public class Point2 implements EuclidConstants {

    /**
     * the coordinates of the point
     */
    protected double[] p2_array;

    /**
     * constructor.
     */
    public Point2() {
        init();
    }

    /**
     * formed from point components
     * 
     * @param x
     * @param y
     */
    public Point2(double x, double y) {
        this();
        p2_array[0] = x;
        p2_array[1] = y;
    }

    private void init() {
        p2_array = new double[2];
    }

    /**
     * copy constructor
     * 
     * @param p
     */
    public Point2(Point2 p) {
        this();
        System.arraycopy(p.p2_array, 0, p2_array, 0, 2);
    }

    /**
     * constructor from a double[] (or a RealArray)
     * 
     * @param f
     */
    public Point2(double[] f) {
        this();
        System.arraycopy(f, 0, p2_array, 0, 2);
    }

    /**
     * clone.
     * 
     * @param p
     * @return the new point
     */
    public Point2 clone(Point2 p) {
        init();
        System.arraycopy(p.p2_array, 0, p2_array, 0, 2);
        return this;
    }

    /**
     * overloaded assignment from a double[] (or a RealArray)
     * 
     * @param f
     * @return the cloned point
     */
    public Point2 clone(double[] f) {
        init();
        System.arraycopy(f, 0, p2_array, 0, 2);
        return this;
    }

    /**
     * get components as double[]
     * 
     * @return the array
     */
    public double[] getArray() {
        return p2_array;
    }

    /**
     * sets the point to the origin
     */
    public void clear() {
        p2_array[0] = p2_array[1] = 0.0;
    }

    /**
     * are two points identical. compares content of points with Real.isEqual()
     * 
     * @param p
     *            point to compare
     * @return equal if coordinates are equal within Real.epsilon
     */
    public boolean isEqualTo(Point2 p) {
        return Real.isEqual(2, p2_array, p.p2_array);
    }

    /**
     * New point by adding points as vectors. used for finding centroids, etc.
     * does NOT alter this
     * 
     * @param p
     *            to add
     * @return NEW point
     */
    public Point2 plus(Point2 p) {
        Point2 p1 = new Point2();

        p1.p2_array[0] = p2_array[0] + p.p2_array[0];
        p1.p2_array[1] = p2_array[1] + p.p2_array[1];

        return p1;
    }

    /**
     * Move this Point2.
     * 
     * @param pt
     */
    public void plusEquals(final Point2 pt) {
        p2_array[0] += pt.p2_array[0];
        p2_array[1] += pt.p2_array[1];
    }

    /**
     * Shift point from point does alter this
     * 
     * @param pt
     *            the Point2 to subtract from this
     */
    public void subtractEquals(final Point2 pt) {
        p2_array[0] -= pt.p2_array[0];
        p2_array[1] -= pt.p2_array[1];
    }

    /**
     * scale point does NOT alter this
     * 
     * @param f
     *            factor to multiply by
     * @return NEW point
     */
    public Point2 multiplyBy(double f) {
        Point2 p1 = new Point2();

        p1.p2_array[0] = p2_array[0] * f;
        p1.p2_array[1] = p2_array[1] * f;

        return p1;
    }

    /**
     * scale point
     * 
     * @param f
     *            scalefactor
     */
    public void multiplyEquals(final double f) {
        p2_array[0] *= f;
        p2_array[1] *= f;
    }

    /**
     * get negative point (-x, -y).
     */
    public void reflect() {
        p2_array[0] = -p2_array[0];
        p2_array[1] = -p2_array[1];
    }

    /**
     * scale point does NOT alter this
     * 
     * @param f
     *            factor to divide by
     * @return NEW point
     */
    public Point2 divideBy(double f) {
        Point2 p1 = new Point2();

        p1.p2_array[0] = p2_array[0] / f;
        p1.p2_array[1] = p2_array[1] / f;

        return p1;
    }

    /**
     * subscript operator counts from ZERO.
     * 
     * @param n
     * @return the value
     */
    public double elementAt(int n) {
        return p2_array[n];
    }

    /**
     * subscript operator counts from ZERO.
     * 
     * @param n
     * @param d
     */
    public void setElementAt(int n, double d) {
        p2_array[n] = d;
    }

    /**
     * distance of point from origin
     * 
     * @return the distance
     */
    public double getDistanceFromOrigin() {
        return Math.sqrt((p2_array[0] * p2_array[0])
                + (p2_array[1] * p2_array[1]));
    }

    /**
     * get distance.
     * 
     * @param p
     * @return distance
     */
    public double getDistanceFromPoint(final Point2 p) {
        return Math.sqrt(getSquaredDistanceFromPoint(p));
    }

    /**
     * Gets the squared Distance between this point and another
     * 
     * @param p2
     *            the other point to get the distance from
     * @return the squared distance
     */
    public double getSquaredDistanceFromPoint(final Point2 p2) {
        double d = p2_array[0] - p2.p2_array[0];
        double sqdDist = d * d;

        d = p2_array[1] - p2.p2_array[1];
        sqdDist += (d * d);

        return sqdDist;
    }

    /**
     * mid-point of two points
     * 
     * @param p2
     * @return the midpoint
     */
    public Point2 getMidPoint(Point2 p2) {
        Point2 p = new Point2();

        p.p2_array[0] = (p2_array[0] + p.p2_array[0]) * 0.50;
        p.p2_array[1] = (p2_array[1] + p.p2_array[1]) * 0.50;

        return p;
    }

    /**
     * is the point the origin.
     * 
     * @return true if origin
     */
    public boolean isOrigin() {
        for (int i = 0; i < 2; i++) {
            if (Real.isZero(p2_array[i]))
                return false;
        }
        return true;
    }

    /**
     * string.
     * 
     * @return string value
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(S_SPACE);
        for (int i = 0; i < 2; i++) {
            sb.append(p2_array[i]);
            sb.append(S_SPACE);
        }
        return sb.toString();
    }

}
