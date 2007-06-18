package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.Vector3;

/**
 * user-modifiable class supporting point3. * autogenerated from schema use as a
 * shell which can be edited NOTE: points must contain values (empty points not
 * allowed)
 *
 */
public class CMLPoint3 extends AbstractPoint3 {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    /** empty point.
     * required so as to create newInstance
     */
    public CMLPoint3() {
    }

    /**
     * constructor.
     *
     * @param old
     */
    public CMLPoint3(CMLPoint3 old) {
        super((AbstractPoint3) old);
    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLPoint3(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLPoint3
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLPoint3();
    }

    /**
     * check point is OK. must have 3 double components
     *
     * @param parent
     *            element
     * @throws CMLRuntimeException
     *             parsing error
     */
    public void finishMakingElement(Element parent) throws CMLRuntimeException {
        double[] array = this.getXMLContent();
        if (array == null) {
            throw new CMLRuntimeException("point must not be empty");
        } else if (array.length != 3) {
            throw new CMLRuntimeException("point must have 3 double components");
        }
    }

    // =========================== additional constructors
    // ========================

    /**
     * formed from components.
     *
     * @param array
     *            3-component
     * @throws CMLRuntimeException
     *             length not 3
     */
    public CMLPoint3(double[] array) throws CMLRuntimeException {
        try {
            this.setXYZ3(array);
        } catch (CMLException e) {
            throw new CMLRuntimeException("" + e);
        }
    }

    /**
     * formed from Euclid point.
     *
     * @param p
     */
    public CMLPoint3(Point3 p) {
        this(p.getArray());
    }

    /**
     * create from doubles.
     *
     * @param x
     * @param y
     * @param z
     */
    public CMLPoint3(double x, double y, double z) {
        this();
        this.setXMLContent(new double[] { x, y, z });
    }

    // ====================== housekeeping methods =====================

    /**
     * gets Point3. cannot be null (will throw CMLRutnime)
     *
     * @return the point - cannot be null
     * @exception CMLRuntimeException
     *                does not have 3 valid coordinates
     */
    public Point3 getEuclidPoint3() throws CMLRuntimeException {
        return new Point3(this.getXMLContent());
    }

    // ====================== subdiary accessors =====================

    /**
     * sets components.
     *
     * @param xyz3
     *            3 components
     * @throws CMLException
     *             xyz3 must be of length 3
     */
    public void setXYZ3(double[] xyz3) throws CMLException {
        if (xyz3.length != 3) {
            throw new CMLException("xyz3 must be of length 3");
        }
        this.setXMLContent(xyz3);
    }

    /**
     * gets components.
     *
     * @return 3-component array
     */
    public double[] getXYZ3() {
        return this.getXMLContent();
    }

    // ====================== functionality =====================

    /**
     * are two points identical. compares content of points with Real.isEqual()
     *
     * @param p
     *            point to compare
     * @return equal if coordinates are equal within Real.epsilon
     */
    public boolean isEqualTo(CMLPoint3 p) {
        Point3 peucl3 = getEuclidPoint3();
        return (peucl3 == null) ? null : peucl3.isEqualTo(p.getEuclidPoint3());
    }

    /**
     * are two points identical. compares content of points with Real.isEqual()
     *
     * @param p
     *            point to compare
     * @param eps
     *            tolerance for comparison on each coordinate
     * @return equal if coordinates are equal within Real.epsilon
     */
    public boolean isEqualTo(CMLPoint3 p, double eps) {
        double[] xyz = this.getXYZ3();
        double[] xyzP = p.getXYZ3();
        return Util.isEqual(xyz, xyzP, eps);
    }

    /**
     * are two crystallographic points identical. shifts x, y, z by +-1.0 if
     * necessary compares content of crystallographically normalised points with
     * Real.isEqual()
     *
     * @param p
     *            point to compare
     * @return equal if coordinates are equal within CRYSTALFRACTEPSILON
     */
    public boolean equalsCrystallographically(CMLPoint3 p) {
        Point3 peucl3 = getEuclidPoint3();
        return (peucl3 == null) ? false : peucl3.equalsCrystallographically(p
                .getEuclidPoint3());
    }

    /**
     * normalise crystallographically. shifts x, y, z so that values lie between
     * 0.0 (inclusive) and 0.1 (exclusive) modifies this
     *
     * @return point
     */
    public CMLPoint3 normaliseCrystallographically() {
        Point3 peucl3 = getEuclidPoint3();
        Point3 pp = (peucl3 == null) ? null : peucl3
                .normaliseCrystallographically();
        return (pp == null) ? null : new CMLPoint3(pp);
    }

    /**
     * is point invariant wrt symmetry operation.
     *
     * tolerance is decided by Real.isEqual()
     *
     * @param t3
     *            the transformation
     * @param translate
     *            allow crystallographic translations (+-1)
     * @return true if t3 transforms this onto itself
     */
    public boolean isInvariant(CMLTransform3 t3, boolean translate) {
        Point3 peucl3 = getEuclidPoint3();
        return (peucl3 == null) ? false : peucl3.isInvariant(t3
                .getEuclidTransform3(), translate);
    }

    /**
     * vector between two points.
     *
     * @param p2
     *            point to subtract
     * @return vector
     */
    public CMLVector3 subtract(CMLPoint3 p2) {
        Point3 peucl3 = getEuclidPoint3();
        Vector3 v = (peucl3 == null) ? null : peucl3.subtract(p2
                .getEuclidPoint3());
        return (peucl3 == null) ? null : CMLVector3.createCMLVector3(v);
    }

    /**
     * New point by adding points as vectors. used for finding centroids, etc.
     * does NOT alter this
     *
     * @param p
     *            to add
     * @return NEW point
     */
    public CMLPoint3 plus(CMLPoint3 p) {
        Point3 peucl3 = getEuclidPoint3();
        Point3 pp = (peucl3 == null) ? null : peucl3.plus(p.getEuclidPoint3());
        return (pp == null) ? null : new CMLPoint3(pp);
    }

    /**
     * Move this CMLPoint3. alters this
     *
     * @param pt
     *            point to shift by
     */
    public void plusEquals(final CMLPoint3 pt) {
        Point3 peucl3 = getEuclidPoint3();
        if (peucl3 != null) {
            peucl3.plusEquals(pt.getEuclidPoint3());
            this.setXMLContent(peucl3.getArray());
        }
    }

    /**
     * New point from point and vector. does NOT alter this
     *
     * @param v
     *            to add
     * @return NEW point
     */
    public CMLPoint3 plus(CMLVector3 v) {
        Point3 peucl3 = getEuclidPoint3();
        Point3 vv = (peucl3 == null) ? null : peucl3.plus(v.getEuclidVector3());
        return (vv == null) ? null : new CMLPoint3(vv);
    }

    /**
     * point from point and vector. alters this
     *
     * @param v
     *            to add
     */
    public void plusEquals(CMLVector3 v) {
        Point3 peucl3 = getEuclidPoint3();
        if (peucl3 != null) {
            peucl3.plusEquals(v.getEuclidVector3());
            this.setXMLContent(peucl3.getArray());
        }
    }

    /**
     * New point from point minus vector. does NOT alter this
     *
     * @param v
     *            to subtract
     * @return NEW point
     */
    public CMLPoint3 subtract(CMLVector3 v) {
        Point3 pp = getEuclidPoint3().subtract(v.getEuclidVector3());
        return (pp == null) ? null : new CMLPoint3(pp);
    }

    /**
     * Shift point from point. does alter this
     *
     * @param pt
     *            the CMLPoint3 to subtract from this
     */
    public void subtractEquals(final CMLPoint3 pt) {
        Point3 peucl3 = getEuclidPoint3();
        peucl3.subtractEquals(pt.getEuclidPoint3());
        this.setXMLContent(peucl3.getArray());
    }

    /**
     * Shift point from CMLVector3. does alter this
     *
     * @param vec3
     *            the CMLVector3 to subtract from this
     */
    public void subtractEquals(final CMLVector3 vec3) {
        Point3 peucl3 = getEuclidPoint3();
        peucl3.subtractEquals(vec3.getEuclidVector3());
        this.setXMLContent(peucl3.getArray());
    }

    /**
     * scale point does NOT alter this
     *
     * @param f
     *            factor to multiply by
     * @return NEW point
     */
    public CMLPoint3 multiplyBy(double f) {
        Point3 pp = getEuclidPoint3().multiplyBy(f);
        return (pp == null) ? null : new CMLPoint3(pp);
    }

    /**
     * scale point. alters this
     *
     * @param f
     *            factor to multiply by
     */
    public void multiplyEquals(final double f) {
        Point3 peucl3 = getEuclidPoint3();
        peucl3.multiplyEquals(f);
        this.setXMLContent(peucl3.getArray());
    }

    /**
     * scale point. does NOT alter this
     *
     * @param f
     *            factor to divide by
     * @return NEW point (components are Infinity or NaN if divide by zero)
     * @throws CMLRuntimeException
     */
    public CMLPoint3 divideBy(double f) throws CMLRuntimeException {
        Point3 pp = getEuclidPoint3().divideBy(f);
        return (pp == null) ? null : new CMLPoint3(pp);
    }

    /**
     * subscript operator.
     *
     * @param n
     *            the index
     * @return the element
     * @throws CMLRuntimeException
     */

    public double elementAt(int n) throws CMLRuntimeException {
        Point3 peucl3 = getEuclidPoint3();
        try {
            return peucl3.elementAt(n);
        } catch (EuclidRuntimeException e) {
            throw new CMLRuntimeException("" + e);
        }
    }

    /**
     * sets element.
     *
     * @param n
     *            the index
     * @param d
     *            the value
     * @throws CMLException
     *             bad value of n
     */
    public void setElementAt(int n, double d) throws CMLException {
        Point3 peucl3 = getEuclidPoint3();
        try {
            peucl3.setElementAt(n, d);
        } catch (EuclidRuntimeException e) {
            throw new CMLException("" + e);
        }
        this.setXMLContent(peucl3.getArray());
    }

    /**
     * get transformed point. does NOT modify 'this'
     *
     * @param t
     *            the transform
     * @return new point
     */
    public CMLPoint3 transform(CMLTransform3 t) {
        Point3 pp = getEuclidPoint3().transform(t.getEuclidTransform3());
        return (pp == null) ? null : new CMLPoint3(pp);
    }

    /**
     * distance of point from origin.
     *
     * @return distance
     */
    public double getDistanceFromOrigin() {
        Point3 peucl3 = getEuclidPoint3();
        return (peucl3 == null) ? Double.NaN : peucl3.getDistanceFromOrigin();
    }

    /**
     * Gets the squared Distance between this point and another.
     *
     * @param p2
     *            the other point to get the distance from
     * @return the squared distance
     */
    public double getSquaredDistanceFromPoint(final CMLPoint3 p2) {
        Point3 peucl3 = getEuclidPoint3();
        return peucl3.getSquaredDistanceFromPoint(p2.getEuclidPoint3());
    }

    /**
     * distance of point from another point
     *
     * @param p2
     *            the other point to get the distance from
     * @return the distance
     */
    public double getDistanceFromPoint(CMLPoint3 p2) {
        Point3 peucl3 = getEuclidPoint3();
        return peucl3.getDistanceFromPoint(p2.getEuclidPoint3());
    }

    /**
     * distance of point from plane.
     *
     * @param pl
     *            the plane
     * @return the distance
     */
    public double distanceFromPlane(CMLPlane3 pl) {
        Point3 peucl3 = getEuclidPoint3();
        return (peucl3 == null) ? Double.NaN : peucl3.distanceFromPlane(pl
                .getEuclidPlane3());
    }

    /**
     * get closest point on line.
     *
     * @param l
     *            the line
     * @return the point where distance is shortest
     */
    public CMLPoint3 getClosestPointOnLine(CMLLine3 l) {
        Point3 pp = getEuclidPoint3().getClosestPointOnLine(l.getEuclidLine3());
        return (pp == null) ? null : new CMLPoint3(pp);
    }

    /**
     * is point on line.
     *
     * @param l
     *            the line
     * @return true if within Real.isEqual() of line
     */
    public boolean isOnLine(CMLLine3 l) {
        return getEuclidPoint3().isOnLine(l.getEuclidLine3());
    }

    /**
     * is point on plane.
     *
     * @param pl
     *            the plane
     * @return true if within Real.isEqual() of plane
     */
    public boolean isOnPlane(CMLPlane3 pl) {
        return getEuclidPoint3().isOnPlane(pl.getEuclidPlane3());
    }

    /**
     * distance from line. BROKEN - DO NOT USE
     *
     * @param l
     *            the line
     * @return the distance
     */
    public double distanceFromLine(CMLLine3 l) {
        double d = getEuclidPoint3().distanceFromLine(l.getEuclidLine3());
        return d;
    }

    /**
     * mid-point of two points.
     *
     * @param p2
     *            the other point
     * @return the midPoint
     */
    public CMLPoint3 getMidPoint(CMLPoint3 p2) {
        Point3 pp = getEuclidPoint3().getMidPoint(p2.getEuclidPoint3());
        return (pp == null) ? null : new CMLPoint3(pp);
    }

    /**
     * get angle. this-p2-p3
     *
     * @param p2
     *            the vertex point
     * @param p3
     *            the remote point
     * @return angle null if points are coincident
     */
    public Angle getAngle(CMLPoint3 p2, CMLPoint3 p3) {
        return Point3.getAngle(getEuclidPoint3(), p2.getEuclidPoint3(), p3
                .getEuclidPoint3());
    }

    /**
     * torsion angle. this-p2-p3-p4
     *
     * @param p2
     * @param p3
     * @param p4
     * @return angle unsigned radians
     * @throws CMLException
     */
    public double getTorsion(CMLPoint3 p2, CMLPoint3 p3, CMLPoint3 p4)
            throws CMLException {
        Point3 peucl3 = getEuclidPoint3();
        double torsion = Double.NaN;
        if (peucl3 != null) {
            torsion = Point3.getTorsion(peucl3, p2.getEuclidPoint3(),
            		p3.getEuclidPoint3(), p4.getEuclidPoint3()).getRadian();
        }
        return torsion;
    }

    /**
     * add point using internal coordinates. used for z-matrix like building
     * this-p2-p3-newPoint
     *
     * @param p2
     *            existing point
     * @param p3
     *            existing point
     * @param length
     *            p3-p4
     * @param angle
     *            p2-p3-p4 radians
     * @param torsion
     *            this-p2-p3-p4 radians
     * @exception CMLRuntimeException
     *                two points are coincident or three points are colinear
     * @return new point
     */
    public CMLPoint3 calculateFromInternalCoordinates(CMLPoint3 p2,
            CMLPoint3 p3, double length, double angle, double torsion) {
        Point3 peucl3 = getEuclidPoint3();
        Point3 pp = null;
        if (peucl3 != null) {
            try {
                pp = Point3.calculateFromInternalCoordinates(peucl3, p2
                        .getEuclidPoint3(), p3.getEuclidPoint3(), length,
                        new Angle(angle), new Angle(torsion));
            } catch (EuclidRuntimeException je) {
                throw new CMLRuntimeException("bug " + je);
            }
        }
        return (pp == null) ? null : new CMLPoint3(pp);
    }

    /**
     * is a point at Origin
     *
     * @return is this within Real.isEqual() of origin
     */
    public boolean isOrigin() {
        Point3 peucl3 = getEuclidPoint3();
        boolean ok = true;
        for (double d : peucl3.getArray()) {
            if (Math.abs(d) > EPS) {
                ok = false;
                break;
            }
        }
        return ok;
    }

    /**
     * to string.
     *
     * @return the string
     */
    public String getString() {
        return this.getEuclidPoint3().toString();
    }

}
