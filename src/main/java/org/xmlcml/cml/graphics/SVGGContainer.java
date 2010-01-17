package org.xmlcml.cml.graphics;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Nodes;

import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;

/** a number of SVGGs contained within an SVGG
 * each contained SVGG has an explicit bounding box
 * typical examples are molecules in a reactant list
 * 
 * @author pm286
 *
 */
public class SVGGContainer extends SVGGWithBox {

	public SVGGContainer() {
		
	}

	public void addSVGG(SVGGWithBox childSvg) {
		Transform2 childTransform = childSvg.getTransform2FromAttribute();
		if (childTransform == null) {
			childTransform = new Transform2();
		}
		List<SVGGWithBox> childSvgs = this.getSVGGWithBoxChildren();
		if (childSvgs.size() > 0) {
			SVGG lastSVGG = childSvgs.get(childSvgs.size()-1);
			Transform2 lastTransform = lastSVGG.getTransform2FromAttribute();
			Real2 delta = getOffset(lastSVGG, this.layout);
			childTransform = childTransform.concatenate(new Transform2(new Vector2(delta)));
		}
		SVGG g = new SVGG(childSvg);
		g.setTransform(childTransform);
		this.appendChild(g);
	}
	
	public List<SVGGWithBox> getSVGGWithBoxChildren() {
		// "./g"
		Nodes gNodes = this.query("./*[local-name()='"+SVGG.TAG+"']");
		List<SVGGWithBox> gList = new ArrayList<SVGGWithBox>(gNodes.size());
		for (int i = 0; i < gNodes.size(); i++) {
			gList.add(SVGGContainer.createSVGGWithBox((SVGG)gNodes.get(i)));
		}
		return gList;
	}

}
