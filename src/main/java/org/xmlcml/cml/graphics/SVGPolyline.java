package org.xmlcml.cml.graphics;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGPolyline extends SVGPoly {

	final static String TAG ="polyline";

	/** constructor
	 */
	public SVGPolyline() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGPolyline(SVGLine line) {
        this();
        CMLElement.copyAttributesFromTo(line, this);
        CMLElement.deleteAttribute(this, "x1");
        CMLElement.deleteAttribute(this, "y1");
        CMLElement.deleteAttribute(this, "x2");
        CMLElement.deleteAttribute(this, "y2");
        this.real2Array = new Real2Array();
        this.real2Array.add(line.getXY(0));
        this.real2Array.add(line.getXY(1));
        this.setReal2Array(real2Array);
	}
	
	/** constructor
	 */
	public SVGPolyline(SVGElement element) {
        super((SVGElement) element);
	}
	
	/** constructor
	 */
	public SVGPolyline(Element element) {
        super((SVGElement) element);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGPolyline(Real2Array real2Array) {
		this();
		setReal2Array(real2Array);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGPolyline(this);
    }

	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	/** pass polyline or convert line.
	 * 
	 * @param element
	 * @return
	 */
	public static SVGPolyline getOrCreatePolyline(SVGElement element) {
		SVGPolyline polyline = null;
		if (element instanceof SVGLine) {
			polyline = new SVGPolyline((SVGLine) element);
			
		} else if (element instanceof SVGPolyline) {
			polyline = (SVGPolyline) element;
		}
		return polyline;
	}

	public static List<SVGPolyline> binaryMergePolylines(List<SVGPolyline> polylineList, double eps) {
		List<SVGPolyline> newList = new ArrayList<SVGPolyline>();
		int size = polylineList.size();
		int niter = size / 2;
		for (int i = 0; i < niter*2; i += 2) {
			SVGPolyline line0 = polylineList.get(i);
			SVGPolyline line1 = polylineList.get(i + 1);
			SVGPolyline newLine = createMergedLine(line0, line1, eps);
			newList.add(newLine);
		}
		if (size %2 != 0) {
			newList.add(polylineList.get(size-1));
		}
		return newList;
	}

	/** appends poly1 to poly0.
	 * does not duplicate common element
	 * copy semantics
	 * @param poly0 not changed
	 * @param poly1 not changed
	 * @param eps
	 * @return
	 */
	public static SVGPolyline createMergedLine(SVGPolyline poly0, SVGPolyline poly1, double eps) {
		SVGPolyline newPoly = null;
		Real2 last0 = poly0.getLast();
		Real2 first1 = poly1.getFirst();
		if (last0.isEqualTo(first1, eps)) {
			newPoly = new SVGPolyline(poly0);
			Real2Array r20 = newPoly.getReal2Array();
			Real2Array r21 = poly1.getReal2Array();
			for (int i = 1; i < r21.size(); i++) {
				r20.add(new Real2(r21.get(i)));
			}
			newPoly.setReal2Array(r20);
		}
		return newPoly;
	}
}
