package org.xmlcml.cml.graphics;

import java.io.IOException;

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
	 * @throws IOException
	 */
	void createOrDisplayGraphics() throws IOException;

	/**
	 * @param g
	 * @throws IOException
	 */
	void output(GraphicsElement g) throws IOException ;
}
