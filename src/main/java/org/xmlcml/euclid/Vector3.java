package org.xmlcml.euclid;

import org.xmlcml.euclid.Axis.Axis3;

/**
 * 3-dimensional vector
 *
 * A vector has thre components giving it a length and a direction (whose sign
 * is important),but no position. Vectors are often normalised to unit length.
 * <P>
 * Vectors and points are very closely related and some people use them
 * interchangeably. A Point3 <i>has</I> a position and cannot be normalised. In
 * very many routines, however, Vectors and Points can either be used
 * interchangeably, or there are equivalent routines or they can be converted
 * using cross-constructors. (They cannot be interconverted through casts).
 * <P>
 * The default vector is 0.0, 0.0, 0.0. Some operations on this will result in
 * ZerolengthVector Exceptions.
 *
 * @author (C) P. Murray-Rust, 1996
 */

public class Vector3 implements EuclidConstants {

    /**
     * length of vector is zero
     */
    final static int ZERO_VECT = 0;

    /**
     * length of vector is unknown
     */
    final static int UNK_VECT = 1;

    /**
     * length is unit vector
     */
    final static int UNIT_VECT = 2;

    /**
     * length of vector not zero or unity
     */
    final static int OK_VECT = 3;

    /**
     * zero-length vector
     */
    public final static Vector3 ZEROV = new Vector3(0.0, 0.0, 0.0);

    /**
     * X axis
     */
    public final static Vector3 XV = new Vector3(1.0, 0.0, 0.0);

    /**
     * Y axis
     */
    public final static Vector3 YV = new Vector3(0.0, 1.0, 0.0);

    /**
     * Z axis
     */
    public final static Vector3 ZV = new Vector3(0.0, 0.0, 1.0);

    /**
     * Vector3 includes protected members which keep track of the sort of vector
     * At present these can be: null vector, unit vector and unknown. These
     * serve to reduce repetition in normalising already normalised vectors
     */

    /**
     * vector status
     */
    // int vecstatus = UNK_VECT;
    /**
     * vector length IF status not UNK_VECT, else -1
     */
    // double veclength;
    /**
     * vector components
     */
    double[] flarray = new double[3];

    /**
     * null constructor
     */
    public Vector3() {
    }

    /**
     * construct from vector components.
     *
     * @param x
     *            component
     * @param y
     *            component
     * @param z
     *            component
     */
    public Vector3(double x, double y, double z) {
        this();
        flarray[0] = x;
        flarray[1] = y;
        flarray[2] = z;

    }

    /**
     * construct from vector components.
     *
     * @param array
     *            components
     * @throws EuclidRuntimeException
     */
    public Vector3(double[] array) throws EuclidRuntimeException {
        this();
        Util.check(array, 3);
        System.arraycopy(array, 0, flarray, 0, 3);
    }

    /**
     * axial unit vector constructor. unit vectors along X, Y, Z axes
     *
     * @param axis
     *            to use
     */
    public Vector3(Axis3 axis) {
        this();
        Real.zeroArray(3, flarray);
        flarray[axis.value] = 1.0;
    }

    /**
     * copy constructor:
     *
     * @param v
     *            vector to copy
     */
    public Vector3(Vector3 v) {
        this();
        System.arraycopy(v.flarray, 0, flarray, 0, 3);
    }

    /**
     * copy constructor from RealArray.
     *
     * @param f the array (of length 3)
     * @throws EuclidRuntimeException
     */
    public Vector3(RealArray f) throws EuclidRuntimeException {
        this();
        RealArray.check(f, 3);
        System.arraycopy(f.getArray(), 0, flarray, 0, 3);
    }

    /**
     * make a vector from a point vector is from origin to point
     *
     * @param p
     *            the point
     */
    public Vector3(Point3 p) {
        this();
        System.arraycopy(p.flarray, 0, flarray, 0, 3);
    }

    /**
     * copy constructor: synonym for copy constructor
     *
     * @param v
     *            vector to copy
     * @return vector
     */
    public Vector3 clone(Vector3 v) {
        System.arraycopy(v.flarray, 0, flarray, 0, 3);
        return this;
    }

    /**
     * from Point3 vector is from origin to point
     *
     * @param p
     *            the point
     * @return vector
     */
    public Vector3 clone(Point3 p) {
        System.arraycopy(p.flarray, 0, flarray, 0, 3);
        return this;
    }

    /**
     * get the vector components
     *
     * @return vector of length 3
     */
    public double[] getArray() {
        return flarray;
    }

    /**
     * are two vectors equal lengths. uses Real.isEqual
     *
     * @param v
     * @return equal if length difference is within tolerance
     * @deprecated use explicit epsilon
     */
    public boolean isEqualTo(Vector3 v) {
        return Real.isEqual(getLength(), v.getLength());
    }

    /**
     * are two vectors equal lengths
     *
     * @param v
     * @return equal if length difference is within epsilon
     */
    public boolean isEqualTo(Vector3 v, double epsilon) {
        return Real.isEqual(getLength(), v.getLength(), epsilon);
    }

    /**
     * vector length > vector length
     *
     * @param v
     *            vector to compare
     * @return true if this > vector
     */
    public boolean longerThan(Vector3 v) {
        return (getLength() > v.getLength());
    }

    /**
     * scalar multiplication. create new vector vector = this*f does not alter this
     *
     * @param f
     *            multiplier for all components
     * @return scaled vector
     */
    public Vector3 multiplyBy(double f) {
        Vector3 v1 = new Vector3(this);
        for (int i = 0; i < 3; i++) {
            v1.flarray[i] *= f;
        }
        return v1;
    }

    /**
     * scalar multiplication. sets equal to this*f alters this
     *
     * @param f
     *            multiplier for all components
     */
    public void multiplyEquals(double f) {
        for (int i = 2; i >= 0; --i) {
            flarray[i] *= f;
        }
    }

    /**
     * vector addition. create new vector result = this + v3 does not alter this
     *
     * @param v3
     *            vector to add
     * @return resultant vector
     */
    public Vector3 plus(Vector3 v3) {
        Vector3 v1 = new Vector3();
        v1 = this;
        for (int i = 0; i < 3; i++) {
            v1.flarray[i] += v3.flarray[i];
        }
        return v1;
    }

    /**
     * vector addition. sets equal to this + v3 alters this
     *
     * @param v3 vector to subtract
     */
     public void plusEquals(Vector3 v3) {
         for (int i = 2; i >= 0; --i) {
             flarray[i] += v3.flarray[i];
         }
     }

    /**
     * vector subtraction. create new vector result = this - v3 does not alter
     * this
     *
     * @param v3
     *            vector to subtract
     * @return resultant vector
     */
    public Vector3 subtract(Vector3 v3) {
        Vector3 v1 = new Vector3();
        v1 = this;
        for (int i = 0; i < 3; i++) {
            v1.flarray[i] -= v3.flarray[i];
        }
        return v1;
    }

   /**
    * vector subtraction. sets equal to this - v3 alters this
    *
    * @param v3  vector to subtract
    */
    public void subtractEquals(Vector3 v3) {
        for (int i = 2; i >= 0; --i) {
            flarray[i] -= v3.flarray[i];
        }
    }

    /**
     * negative of vector. result = -this does not alter this
     *
     * @return resultant vector
     */
    public Vector3 negative() {
        Vector3 v1 = new Vector3(this);
        for (int i = 0; i < 3; i++) {
            v1.flarray[i] = -flarray[i];
        }
        return v1;
    }

    /**
     * negative of vector. result = - DOES alter this
     *
     * @return resultant vector
     */
    public Vector3 negativeEquals() {
        for (int i = 0; i < 3; i++) {
            flarray[i] = -flarray[i];
        }
        return this;
    }

    /**
     * get component. use raw array if you are sure checking is not required
     *
     * @param n
     *            the zero-based index
     * @return the n'th component
     * @throws EuclidRuntimeException
     */
    public double elementAt(int n) throws EuclidRuntimeException {
        Util.check(n, 0, 2);
        return flarray[n];
    }

    /**
     * set component.
     *
     * @param n
     *            the zero-based index
     * @param f
     *            component value
     * @throws EuclidRuntimeException
     */
    public void setElementAt(int n, double f) throws EuclidRuntimeException {
        Util.check(n, 0, 2);
        flarray[n] = f;
    }

    /**
     * are two vectors equal in all components. uses Real.isEqual
     *
     * @param v
     * @deprecated use epsilon
     * @return equal if components equal within tolerance
     */
    public boolean isIdenticalTo(Vector3 v) {
        return Real.isEqual(3, flarray, v.flarray, Real.EPS);
    }

    /**
     * are two vectors equal in all components. uses Real.isEqual
     *
     * @param v
     * @return equal if components equal within tolerance
     */
    public boolean isIdenticalTo(Vector3 v, double epsilon) {
        return Real.isEqual(3, flarray, v.flarray, epsilon);
    }

    /**
     * is vector of zero length. uses Real.isEqual
     *
     * @return if zero within tolerance

     *@deprecated use epsilon
     */
    public boolean isZero() {
        boolean b = Real.isZero(getLength());
        return b;
    }

    /**
     * is vector of zero length. uses Real.isEqual
     * @param epsilon
     * @return if zero within tolerance
     */
    public boolean isZero(double epsilon) {
        boolean b = Real.isZero(getLength(), epsilon);
        return b;
    }

    /**
     * create transformed vector. does not alter this.
     *
     * @param t
     *            transform
     * @return tranformed vector
     */
    public Vector3 transform(Transform3 t) {
        Transform3.checkNotNull(t);
        Vector3 vout = new Vector3();
        double[] pv = vout.flarray;
        double[][] pt = t.getMatrix();
        // just use the 3x3 submatrix and ignore translation
        for (int i = 0; i < 3; i++) {
            double[] p = this.flarray;
            for (int j = 0; j < 3; j++) {
                pv[i] += pt[i][j] * p[j];
            }
        }
        return vout;
    }

    /**
     * create cross product. result = this x v3 does not alter this.
     *
     * @param v3
     *            vector to multiply
     * @return cross product
     */
    public Vector3 cross(Vector3 v3) {
        Vector3 v1 = new Vector3();
        int i, j, k;
        for (i = 0, j = 1, k = 2; i < 3; i++) {
            v1.flarray[i] = flarray[j] * v3.flarray[k] - flarray[k]
                    * v3.flarray[j];
            j = (j + 1) % 3;
            k = (k + 1) % 3;
        }
        return v1;
    }

    /** sets vector components to nearest integer value.
     * useful for crystallography and other grids
     * uses Math.round();
     * ALTERS THIS
     * @return vector with integer components
     */
    public Vector3 round() {
        for (int i = 0; i < 3; i++) {
            flarray[i] = Math.round(flarray[i]);
        }
        return this;
    }

    /** normalize vector.
     *  alters this
     *  this = this.normalize()
     *   if zero vector takes no action (maybe this is bad...)
     *
     * @return vector of unit length
     */
    public Vector3 normalize() {
        double veclength = this.getLength();
        if (veclength < EPS) {
            throw new EuclidRuntimeException("cannot normalize zero-length vector");
        }
        for (int i = 0; i < 3; i++) {
            flarray[i] /= veclength;
        }
        return this;
    }

    /** normalize vector. alters this this = this.normalize() if zero vector
     * takes no action (maybe this is bad...)
     * @deprecated (use normalize())
     * @return vector of unit length
     */
    public Vector3 normalise() {
        return this.normalize();
    }

    /**
     * get normalized vector.
     *  does not alter this
     *   result = this.normalize()
     *  if zero vector takes no action (maybe this is bad...)
     *
     * @return vector of unit length
     */
    public Vector3 getUnitVector() {
        Vector3 v = new Vector3(this);
        v.normalize();
        return v;
    }

    /**
     * return vector length. does not alter this result = this.length()
     *
     * @return vector length
     */
    public double getLength() {
        double sum = 0.0;
        for (int i = 0; i < 3; i++) {
            sum += flarray[i] * flarray[i];
        }
        return Math.sqrt(sum);
    }

    /**
     * create dot product. result = this . v3 does not alter this.
     *
     * @param v3
     *            vector to multiply
     * @return dot product
     */
    public double dot(Vector3 v3) {
        double sum = 0.0;
        for (int i = 0; i < 3; i++) {
            sum += this.flarray[i] * v3.flarray[i];
        }
        return sum;
    }

    /**
     * dot product - protected
     *
     * @param v3
     * @return the dotproduct
     */
    protected double dot(double[] v3) {
        double sum = 0.0;
        for (int i = 0; i < 3; i++) {
            sum += this.flarray[i] * v3[i];
        }
        return sum;
    }

    /**
     * calculate unsigned angle between vectors result = angle between this and
     * v2 uses acos(this.dot.v2) so angle is unsigned does not alter this.
     *
     * @param v2
     *            vector to multiply
     * @return angle (null if vectors zero length
     */
    public Angle getAngleMadeWith(Vector3 v2) {
        Angle a = null;
        if (!this.isZero(Real.EPS) && !v2.isZero(Real.EPS)) {
            Vector3 v1a = getUnitVector();
            Vector3 v2a = v2.getUnitVector();
            double tmp = v1a.dot(v2a);
            if (tmp < -1.0) {
                tmp = -1.0;
            } else if (tmp > 1.0) {
                tmp = 1.0;
            }
            a = new Angle(Math.acos(tmp));
        }
        return a;
    }

    /**
     * calculate scalar triple product between vectors. result = this.(v2 x v3)
     * does not alter this.
     *
     * @param v2
     *            vector to multiply
     * @param v3
     *            vector to multiply
     * @return stp
     */
    public double getScalarTripleProduct(Vector3 v2, Vector3 v3) {
        return this.dot(v2.cross(v3));

    }

    /**
     * projection of this onto vector. does not alter this. result = vector.norm() *
     * (this.norm() dot vector.norm())
     *
     * @param v
     *            vector to project onto
     * @exception EuclidRuntimeException
     *                vector or <TT>this</TT> is zero length
     * @return projected vector
     */
    public Vector3 projectOnto(Vector3 v) throws EuclidRuntimeException {
        if (this.isZero(Real.EPS) || v.isZero(Real.EPS)) {
            throw new EuclidRuntimeException("zero length vector");
        }
        Vector3 projection = new Vector3();
        Vector3 unit3 = v.getUnitVector();
        Vector3 unit2 = getUnitVector();
        double dot = unit2.dot(unit3);
        projection = unit3.multiplyBy(getLength() * dot);
        return projection;
    }

    /**
     * are two vectors colinear. also returns true if one or more vectors are
     * zero
     *
     * @param v
     *            vector to test with this
     * @return true if cross product isZero()
     */
    public boolean isColinearVector(Vector3 v) {
        return this.cross(v).isZero(Real.EPS);
    }

    /**
     * get any vector not colinear with this. returns any axis which is not
     * colinear with this
     *
     * @return either XV or YV (even if this is zero)
     */
    public Vector3 getNonColinearVector() {
        return isColinearVector(XV) ? YV : XV;
    }

    /**
     * get any vector perpendicular to this. useful for creating cartesian axes
     * containing this
     *
     * @return vector perpendicular to this (zero if this is zero)
     */
    public Vector3 getPerpendicularVector() {
        return this.isZero(Real.EPS) ? ZEROV : this.getNonColinearVector().cross(this);
    }

    /**
     * Rotate 3 (non-colinear) vectors  onto 3 other vectors 
     * 
     * @param vector1
     * @param vector2
     * @return transform 
     */
    public static Transform3 rotateLatticeVectors(Vector3[] vector1, Vector3[] vector2) {
    	if (vector1 == null || vector1.length != 3) {
    		throw new RuntimeException("vector must be length 3");
    	}
    	if (vector2== null || vector2.length != 3) {
    		throw new RuntimeException("vector must be length 3");
    	}
    	// calculate median vectors
    	Vector3 v1 = vector1[0].plus(vector1[1].plus(vector1[2]));
    	Vector3 v2 = vector2[0].plus(vector2[1].plus(vector2[2]));
    	Vector3 v1x = new Vector3(0.0, v1.flarray[1], v1.flarray[2]);
    	Vector3 v1y = new Vector3(v1.flarray[0], 0.0, v1.flarray[2]);
    	Vector3 v1z = new Vector3(v1.flarray[0], v1.flarray[1], 0.0);
    	Vector3 v2x = new Vector3(0.0, v2.flarray[1], v2.flarray[2]);
    	Vector3 v2y = new Vector3(v2.flarray[0], 0.0, v2.flarray[2]);
    	Vector3 v2z = new Vector3(v2.flarray[0], v2.flarray[1], 0.0);
    	Angle dThetaX = v1x.getAngleMadeWith(v2x);
    	Angle dThetaY = v1y.getAngleMadeWith(v2y);
    	Angle dThetaZ = v1z.getAngleMadeWith(v2z);
    	Transform3 t3 = new Transform3(dThetaX, dThetaY, dThetaZ);
    	return t3;
    }
    /**
     * get string representation.
     *
     * @return string representation
     */
    public String toString() {
        return S_LBRAK + flarray[0] + S_COMMA + flarray[1] + S_COMMA
                + flarray[2] + S_RBRAK;
    }

}
