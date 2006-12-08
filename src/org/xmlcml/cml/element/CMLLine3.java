package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.euclid.EuclidException;
import org.xmlcml.euclid.Line3;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Util;
import org.xmlcml.euclid.Vector3;

/**
 * user-modifiable class supporting line3. * autogenerated from schema use as a
 * shell which can be edited
 * 
 */
public class CMLLine3 extends AbstractLine3 {

    /**
     * start of point in array.
     * 
     */
    public final static int POINT = 3;

    /**
     * start of vector in array.
     * 
     */
    public final static int VECTOR = 0;

    /**
     * default. has NO default values (point and vector are null)
     */
    public CMLLine3() {
    }

    /**
     * constructor.
     * 
     * @param old
     */
    public CMLLine3(CMLLine3 old) {
        super((AbstractLine3) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLLine3(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLLine3
     */
    public static CMLLine3 makeElementInContext(Element parent) {
        return new CMLLine3();

    }

    /**
     * check line is OK. must have 3 double components
     * 
     * @param parent
     *            element
     * @throws CMLRuntimeException
     *             parsing error
     */
    public void finishMakingElement(Element parent) throws CMLRuntimeException {
        if (this.getPoint3Attribute() == null) {
            throw new CMLRuntimeException("point3 is now mandatory");
        }
        if (this.getVector3Attribute() == null) {
            throw new CMLRuntimeException("vector3 is now mandatory");
        }
    }

    // =========================== additional constructors
    // ========================

    /**
     * formed from Euclid line.
     * 
     * @param l
     */
    public CMLLine3(Line3 l) {
        this();
        this.setPoint3(l.getPoint().getArray());
        this.setVector3(l.getVector().getArray());
    }

    /**
     * construct from point and vector. takes doc from p the line will not
     * necessarily retain the exact point and the vector need not be normalized
     * p and v are copied
     * 
     * @param p
     *            a point on the line
     * @param v
     *            non-zero vector through the point
     * @exception CMLException
     *                zero length vector
     */
    public CMLLine3(CMLPoint3 p, CMLVector3 v) throws CMLException {
        if (v.isZero()) {
            throw new CMLException("zero vector");
        }
        v.normalize();
        setPoint3(p.getXYZ3());
        setVector3(v.getXYZ3());
    }

    /**
     * construct a line from two Point3s. takes doc from p1 the line will not
     * necessarily retain the exact points
     * 
     * @param p1
     *            a point on the line
     * @param p2
     *            another point on the line
     * @exception CMLException
     *                points are coincident
     */
    public CMLLine3(CMLPoint3 p1, CMLPoint3 p2) throws CMLException {
        CMLVector3 v = p1.subtract(p2);
        if (v.isZero()) {
            throw new CMLException("coincident points");
        }
        this.setPoint3(p1.getXYZ3());
        this.setVector3(v.getXYZ3());
    }

    // ====================== housekeeping methods =====================

    /**
     * get euclid primitive.
     * 
     * @return line
     * @exception CMLRuntimeException
     */
    public Line3 getEuclidLine3() throws CMLRuntimeException {
        Line3 line = null;
        try {
            line = new Line3(new Point3(this.getPoint3()), new Vector3(this
                    .getVector3()));
        } catch (EuclidException e) {
            throw new CMLRuntimeException("bug " + e);
        }
        return line;
    }

    // ====================== subsidiary accessors =====================

    /**
     * set vector. will normalize copy of vector
     * 
     * @param v
     *            the vector
     */
    public void setVector3(CMLVector3 v) {
        if (v.isZero()) {
            throw new CMLRuntimeException("Cannot make line with zero vector");
        }
        CMLVector3 vv = new CMLVector3(v);
        vv.normalize();
        super.setVector3(vv.getXMLContent());
    }

    // ====================== functionality =====================

    /**
     * are two lines identical. must be coincident and parallel uses
     * vect.equals() and containsPoint
     * 
     * @param l2
     *            CMLLine3 to compare
     * @return equals
     */
    public boolean isEqualTo(CMLLine3 l2) {
        return Util.isEqual(this.getPoint3(), l2.getPoint3(), EPS)
                && Util.isEqual(this.getVector3(), l2.getVector3(), EPS);
    }

    /**
     * form coincident antiparallel line.
     * 
     * @return antiparallel line
     */
    public CMLLine3 subtract() {
        double[] vv = this.getVector3();
        for (int i = 0; i < vv.length; i++) {
            vv[i] = -vv[i];
        }
        this.setVector3(vv);
        return this;
    }

    /**
     * get transformed line. does not alter this
     * 
     * @param t
     *            transform
     * @return transformed line
     */
    public CMLLine3 transform(CMLTransform3 t) {
        Line3 linee = getEuclidLine3();
        Line3 l = linee.transform(t.getEuclidTransform3());
        return new CMLLine3(l);
    }

    /**
     * are two lines parallel. (not antiparallel) does not test coincidence
     * 
     * @param l2
     *            line to compare
     * @return true if parallel
     */
    public boolean isParallelTo(CMLLine3 l2) {
        double[] v = this.getVector3();
        double[] v2 = l2.getVector3();
        return Util.isEqual(v, v2, EPS);
    }

    /**
     * are two lines antiparallel. (not parallel) does not test coincidence
     * 
     * @param l2
     *            line to compare
     * @return true if antiparallel
     */
    public boolean isAntiparallelTo(CMLLine3 l2) {
        double[] v = this.getVector3();
        Vector3 vv;
        try {
            vv = new Vector3(v);
        } catch (EuclidException e) {
            throw new CMLRuntimeException("bug " + e);
        }
        vv = vv.multiplyBy(-1);
        return Util.isEqual(vv.getArray(), v, EPS);
    }

    /**
     * is a point on a line. tests for Real.isZero() distance from line
     * 
     * @param p
     *            point
     * @return true if within Real.isZero()
     */
    public boolean containsPoint(CMLPoint3 p) {
        double d = p.distanceFromLine(this);
        return (Real.isZero(d));
    }

    /**
     * point on line closest to another point.
     * 
     * @param p2
     *            reference point
     * @return point on line closest to p2
     */
    public CMLPoint3 getClosestPointTo(CMLPoint3 p2) {
        Line3 leucl3 = this.getEuclidLine3();
        Point3 pp = leucl3.getClosestPointTo(p2.getEuclidPoint3());
        return (pp == null) ? null : new CMLPoint3(pp.getArray());
    }

    /**
     * distance of a point from a line
     * 
     * @param p
     *            reference point
     * @return distance from line
     */
    public double getDistanceFromPoint(CMLPoint3 p) {
        Line3 leucl3 = this.getEuclidLine3();
        return leucl3.getDistanceFromPoint(p.getEuclidPoint3());
    }

    /**
     * point of intersection of line and plane calls
     * Plane3.getIntersectionWith(CMLPoint3)
     * 
     * @param pl
     *            plane intersecting line
     * @return point (null if line parallel to plane)
     */
    public CMLPoint3 getIntersectionWith(CMLPlane3 pl) {
        Line3 leucl3 = this.getEuclidLine3();
        Point3 pp = leucl3.getIntersectionWith(pl.getEuclidPlane3());
        return (pp == null) ? null : new CMLPoint3(pp.getArray());
    }

    /**
     * get string.
     * 
     * @return the string
     */
    public String getString() {
        String s = this.getEuclidLine3().toString();
        return s;
    }
}
