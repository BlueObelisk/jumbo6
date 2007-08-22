package org.xmlcml.cml.graphics;

import java.io.IOException;

import org.xmlcml.cml.tools.MoleculeDisplay;
import org.xmlcml.cml.tools.MoleculeTool;

/**
 * 
 * @author pm286
 *
 */
public interface CMLDrawable {
	
	/**
	 * @return element
	 */
	SVGG createGraphicsElement();

	/**
	 * @param moleculeTool
	 * @param moleculeDisplay
	 * @throws IOException
	 */
	void createOrDisplayGraphics(MoleculeTool moleculeTool, MoleculeDisplay moleculeDisplay)
	 throws IOException;

	/**
	 * @param g
	 * @throws IOException
	 */
	void output(GraphicsElement g) throws IOException ;
}
