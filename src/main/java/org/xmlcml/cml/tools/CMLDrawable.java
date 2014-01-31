package org.xmlcml.cml.tools;

import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;

public interface CMLDrawable {

	SVGG createGraphicsElement();

	void output(SVGElement g);

}
