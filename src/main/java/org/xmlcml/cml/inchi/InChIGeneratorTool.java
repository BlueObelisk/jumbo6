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

package org.xmlcml.cml.inchi;

import nu.xom.Text;

import org.xmlcml.cml.element.CMLIdentifier;
import org.xmlcml.cml.element.CMLMolecule;

public class InChIGeneratorTool {
    public static final String CML_INCHI_CONVENTION = "iupac:inchi";

	/** genrates InChI by loading InChI classes if it van.
	 * tries to overcome the difficult of random load fails on InChI
	 * fails semi-gracefully if inchi classes cannot be loaded
	 * @param molecule
	 * @return
	 */
	public static String generateInChI(CMLMolecule molecule) {
		String inchi = null;
		InChIGeneratorInterface igen = null;
		try {
			// Generate factory - if native code does not load
			InChIGeneratorFactoryInterface iff = (InChIGeneratorFactoryInterface)
				Class.forName("org.xmlcml.cml.inchi.InChIGeneratorFactory").newInstance();
//			InChIGeneratorFactory factory = new InChIGeneratorFactory();
			// Get InChIGenerator
			igen = iff.getInChIGenerator(molecule);
//			InChIGenerator igen = factory.getInChIGenerator(molecule);

			if (!igen.isOK()) {
				throw new RuntimeException("Cannot convert Inchi: "+igen.getMessage());
			}
//			INCHI_RET ret = igen.getReturnStatus();
//			if (!INCHI_RET.OKAY.equals(ret)) {
//				throw new RuntimeException("Cannot convert Inchi: "+ret);
//			}

			inchi = igen.getInchi();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Inchi tools not loaded - ensure the seperate inchi project (https://cml.svn.sourceforge.net/svnroot/cml/inchi) is on the classpath at runtime");
		}
		catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot convert Inchi: "+igen.getMessage());
			// log.add(e, "BUG ");
		}
		return inchi;
	}

	/**
	 * Calculates the Inchi for the supplied molecule and appends it
	 * @param molecule
	 */
	public static void addInchiToMolecule(CMLMolecule molecule) {
		String inchi = generateInChI(molecule);
		if (inchi != null) {
	        CMLIdentifier identifier = new CMLIdentifier();
	        identifier.setConvention(CML_INCHI_CONVENTION);
	        identifier.appendChild(new Text(inchi));
	        molecule.appendChild(identifier);
		}
	}
}
