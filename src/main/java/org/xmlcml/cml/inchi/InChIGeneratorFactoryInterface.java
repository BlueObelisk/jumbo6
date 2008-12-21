package org.xmlcml.cml.inchi;

import org.xmlcml.cml.element.CMLMolecule;

public interface InChIGeneratorFactoryInterface {
	InChIGeneratorInterface getInChIGenerator(CMLMolecule molecule);
}
