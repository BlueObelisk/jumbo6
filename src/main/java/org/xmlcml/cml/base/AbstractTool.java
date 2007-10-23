package org.xmlcml.cml.base;

import java.io.IOException;

import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.euclid.Transform2;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * is this used?
 * 
 * @author pmr
 * 
 */
public abstract class AbstractTool implements CMLConstants {
	
    /** returns a "g" element
     * will require to be added to an svg element
     * ALWAYS SUBCLASSED - this throws a non-existent exception
     * @param drawable
	 * @throws IOException
     * @return null if problem
     */
    public SVGElement createGraphicsElement(CMLDrawable drawable) throws IOException {
    	throw new RuntimeException("Must be overridden in "+this.getClass());
    }

	protected SVGElement createSVGElement(CMLDrawable drawable, double scale, double[] offsets) {
		SVGElement g = drawable.createGraphicsElement();
		g.setTransform(new Transform2 (
			new double[]{
			scale, 0., offsets[0],
			0.,-scale, offsets[1],
			0.,    0.,   1.}
		));
		return g;
	}

}