/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;


/**
 * is this used?
 * 
 * @author pmr
 * 
 */
public abstract class AbstractSVGTool extends AbstractTool {
	@SuppressWarnings("unused")
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
		if (element instanceof CMLAction) {
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