package org.xmlcml.cml.tools;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLAction;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.graphics.CMLDrawable;
import org.xmlcml.cml.graphics.GraphicsElement;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Transform2;


/**
 * is this used?
 * 
 * @author pmr
 * 
 */
public abstract class AbstractSVGTool extends AbstractTool {
	private static Logger LOG = Logger.getLogger(AbstractSVGTool.class);
	
    protected SVGG g;
    protected boolean applyScale = false;
    protected Real2Range userBoundingBox;

    /** overridden in subclasses which can create SVG.
     * 
     * @param element
     * @return
     */
	public static AbstractSVGTool getOrCreateSVGTool(CMLElement element) {
		AbstractSVGTool abstractSVGTool = null;
		if (false) {
		} else if (element instanceof CMLAction) {
			abstractSVGTool = ActionTool.getOrCreateTool((CMLAction)element); 
		} else if (element instanceof CMLAtom) {
			abstractSVGTool = AtomTool.getOrCreateTool((CMLAtom)element); 
		} else if (element instanceof CMLBond) {
			abstractSVGTool = BondTool.getOrCreateTool((CMLBond)element); 
		} else if (element instanceof CMLCml) {
			abstractSVGTool = CMLXTool.getOrCreateTool((CMLCml)element); 
		} else if (element instanceof CMLMolecule) {
			abstractSVGTool = MoleculeTool.getOrCreateTool((CMLMolecule)element); 
		} else if (element instanceof CMLMoleculeList) {
			abstractSVGTool = MoleculeListTool.getOrCreateTool((CMLMoleculeList)element); 
		} else if (element instanceof CMLReaction) {
			abstractSVGTool = ReactionTool.getOrCreateTool((CMLReaction)element); 
		}
		return abstractSVGTool;
	}

	protected static void appendNonNullChild(SVGElement g, GraphicsElement childSvg) {
		if (childSvg != null) {
			g.appendChild(childSvg);
		}
	}

	/** returns a "g" element
     * will require to be added to an svg element
     * ALWAYS SUBCLASSED - this throws a non-existent exception
     * @param drawable
	 * @throws IOException
     * @return null if problem
     */
    public SVGElement createGraphicsElement(CMLDrawable drawable) {
    	throw new RuntimeException("Must be overridden in "+this.getClass());
    }

	/** returns a "g" element
     * will require to be added to an svg element
     * ALWAYS SUBCLASSED - this throws a non-existent exception
	 * @throws IOException
     * @return null if problem
     */
    public SVGElement createGraphicsElement() {
    	return this.createGraphicsElement(null);
    }

	protected SVGElement createSVGElement(CMLDrawable drawable, Transform2 transform2) {
		SVGElement g = (drawable == null) ? new SVGG() : drawable.createGraphicsElement();
		g.setTransform(transform2);
		return g;
	}

	/**
	 * @return the g
	 */
	public SVGElement getG() {
		return g;
	}

	public Real2Range getUserBoundingBox() {
		if (userBoundingBox == null) {
			calculateBoundingBox2D();
		}
		return userBoundingBox;
	}

	/** by default do nothing.
	 * normally overridden
	 * @return null
	 */
	protected Real2Range calculateBoundingBox2D() {
		Real2Range range = null;
		return range;
	}

	public void setUserBoundingBox(Real2Range rr) {
		userBoundingBox = rr;
	}

}