package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Node;

import org.xmlcml.cml.attribute.IdAttribute;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLArg;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLFragment;
import org.xmlcml.cml.element.CMLFragmentList;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.euclid.EuclidConstants;

/** processes conventional molecule into fragment.
 * 
 * @author pm286
 *
 */
public class FragmentConverter extends AbstractTool {

	private CMLMolecule molecule;
	private String molId;
	
	/**
	 * constructor
	 * @param molecule
	 */
	public FragmentConverter(CMLMolecule molecule) {
		this.molecule = molecule;
	}
	/** processes conventional molecule into fragment.
	 * @param tool TODO
	 */
	public CMLFragment convertToFragment() {
		// <molecule role="fragment" id="acet"
		// xmlns="http://www.xml-cml.org/schema"
		// xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		// <!-- acetate -->
		// <arg parameterName="idx"/>
		// <arg parentAttribute="id">acet_{$idx}</arg>
		molId = molecule.getId();
		if (molId == null) {
			throw new CMLRuntimeException("molecule must have id");
		}
		molecule.addAttribute(new Attribute("role", "fragment"));
		CMLArg arg = new CMLArg();
		arg.setParameterName(FragmentTool.IDX);
		molecule.appendChild(arg);
		arg = new CMLArg();
		arg.setParentAttribute("id");
		arg.appendChild(molId + S_UNDER + S_LCURLY + S_DOLLAR + FragmentTool.IDX + S_RCURLY);
		molecule.appendChild(arg);
	
		createAtomArguments();
		createBondArguments();
		createLengthArguments();
		createAngleArguments();
		createTorsionArguments();
	
		// deal with R
		List<Node> rGroups = CMLUtil.getQueryNodes(molecule,
				".//"+CMLAtom.NS+"[@elementType='R']", CMLConstants.X_CML);
		for (Node node : rGroups) {
			try {
				new AtomTool((CMLAtom) node).translateToCovalentRadius();
			} catch (CMLRuntimeException e) {
				// no coordinates, not an error
			}
		}
		CMLFragment fragment = new CMLFragment();
		fragment.setId(molecule.getId());
		new FragmentTool(fragment).setMolecule(molecule);
		molecule.removeAttribute(IdAttribute.NAME);
		return fragment;
	}

	private void createAtomArguments() {
		// <atomArray>
		// <atom id="a1" elementType="C" hydrogenCount="3" x3="0.0" y3="0.0"
		// z3="0.0">
		// <arg parentAttribute="id">acet_{$idx}_a1</arg>
		// </atom>
		List<CMLAtom> atoms = molecule.getAtoms();
		for (CMLAtom atom : atoms) {
			String atomId = atom.getId();
			CMLArg arg = new CMLArg();
			arg.setParentAttribute("id");
			arg.appendChild(createMolIdArg() + atomId);
			atom.appendChild(arg);
		}
	}

	private void createBondArguments() {
		//		 ...
		//		 <atom id="r1" elementType="R" x3="2.83" y3="-1.0" z3="0.0">
		//		 <arg parentAttribute="id">acet_{$idx}_r1</arg>
		//		 </atom>
		//		 </atomArray>
		//		 <bondArray>
		//		 <bond atomRefs2="a1 a2" order="1"
		//		 ><arg parentAttribute="id">acet_{$idx}_a1_acet_{$idx}_a2</arg
		//		 ><arg parentAttribute="atomRefs2">acet_{$idx}_a1 acet_{$idx}_a2</arg>
		//		 </bond>
		//		 ...
		//		 <bond atomRefs2="a4 r1" order="1"
		//		 ><arg parentAttribute="id">acet_{$idx}_a4_acet_{$idx}_r1</arg
		//		 ><arg parentAttribute="atomRefs2">acet_{$idx}_a4 acet_{$idx}_r1</arg>
		//		 </bond>
		//		 </bondArray>
		//		 <name><arg substitute=S_PERIOD>acet_{$idx}</arg></name>
		List<CMLBond> bonds = molecule.getBonds();
		for (CMLBond bond : bonds) {
			// String bondId = bond.getId();
			String[] atomRefs2 = bond.getAtomRefs2();
			CMLArg arg = new CMLArg();
			arg.setParentAttribute("id");
			arg.appendChild(
					createMolIdArg()+atomRefs2[0]+EuclidConstants.S_UNDER+
					createMolIdArg()+atomRefs2[1]
			);
			bond.appendChild(arg);
			arg = new CMLArg();
			arg.setParentAttribute("atomRefs2");
			arg.appendChild(
					createMolIdArg()+atomRefs2[0]+EuclidConstants.S_SPACE+
					createMolIdArg()+atomRefs2[1]
			);
			bond.appendChild(arg);
		}
	}

	private void createLengthArguments() {
		//		 <arg parameterName="phi"/>
		//		 <arg parameterName="psi"/>
		//		 ...
		//		 <length atomRefs4="a1 a2 a3 r2">
		//		 <arg parentAttribute="atomRefs2">gly_{$idx}_a1 gly_{$idx}_a2</arg>
		//		 <arg substitute=S_PERIOD>{$psi}</arg>
		//		 </torsion>
		CMLElements<CMLLength> lengths = molecule.getLengthElements();
		for (CMLLength length : lengths) {
			// id
			String lengthId = length.getId();
			CMLArg arg = new CMLArg();
			arg.setParentAttribute("id");
			arg.appendChild(createMolIdArg() + lengthId);
			length.appendChild(arg);
			// atomRefs4
			String[] atomRefs2 = length.getAtomRefs2();
			arg = new CMLArg();
			arg.setParentAttribute("atomRefs2");
			arg.appendChild(
					createMolIdArg() + atomRefs2[0] +EuclidConstants.S_SPACE+
					createMolIdArg() + atomRefs2[1]
			);
			length.appendChild(arg);
			addArg(length, lengthId);
		}
	}

	private void createTorsionArguments() {
		//		 <arg parameterName="phi"/>
		//		 <arg parameterName="psi"/>
		//		 ...
		//		 <torsion atomRefs4="a1 a2 a3 r2">
		//		 <arg parentAttribute="atomRefs4">gly_{$idx}_a1 gly_{$idx}_a2 gly_{$idx}_a3 gly_{$idx}_r2</arg>
		//		 <arg substitute=S_PERIOD>{$psi}</arg>
		//		 </torsion>
		CMLElements<CMLTorsion> torsions = molecule.getTorsionElements();
		for (CMLTorsion torsion : torsions) {
			// id
			String torsionId = torsion.getId();
			CMLArg arg = new CMLArg();
			arg.setParentAttribute("id");
			arg.appendChild(createMolIdArg() + torsionId);
			torsion.appendChild(arg);
			// atomRefs4
			String[] atomRefs4 = torsion.getAtomRefs4();
			arg = new CMLArg();
			arg.setParentAttribute("atomRefs4");
			arg.appendChild(
					createMolIdArg() + atomRefs4[0] +EuclidConstants.S_SPACE+
					createMolIdArg() + atomRefs4[1] +EuclidConstants.S_SPACE+
					createMolIdArg() + atomRefs4[2] +EuclidConstants.S_SPACE+
					createMolIdArg() + atomRefs4[3]
			);
			torsion.appendChild(arg);
			addArg(torsion, torsionId);
		}
	}

	private void createAngleArguments() {
		//		 <arg parameterName="phi"/>
		//		 <arg parameterName="psi"/>
		//		 ...
		//		 <angle atomRefs4="a1 a2 a3 r2">
		//		 <arg parentAttribute="atomRefs4">gly_{$idx}_a1 gly_{$idx}_a2 gly_{$idx}_a3 gly_{$idx}_r2</arg>
		//		 <arg substitute=S_PERIOD>{$psi}</arg>
		//		 </angle>
		CMLElements<CMLAngle> angles = molecule.getAngleElements();
		for (CMLAngle angle : angles) {
			// id
			String angleId = angle.getId();
			CMLArg arg = new CMLArg();
			arg.setParentAttribute("id");
			arg.appendChild(createMolIdArg() + angleId);
			angle.appendChild(arg);
			// atomRefs4
			String[] atomRefs3 = angle.getAtomRefs3();
			arg = new CMLArg();
			arg.setParentAttribute("atomRefs3");
			arg.appendChild(
					createMolIdArg() + atomRefs3[0] +EuclidConstants.S_SPACE+
					createMolIdArg() + atomRefs3[1] +EuclidConstants.S_SPACE+
					createMolIdArg() + atomRefs3[2]
			);
			angle.appendChild(arg);
			addArg(angle, angleId);
		}
	}
	
	private void addArg(CMLElement element, String id) {
		// substitute
		CMLArg arg = new CMLArg();
		arg.setSubstitute(EuclidConstants.S_PERIOD);
		arg.appendChild(createIdArg(id));
		element.appendChild(arg);
		// append args to molecule
		arg = new CMLArg();
		arg.setParameterName(id);
		molecule.appendChild(arg);
	}


	// at present gly_{$idx}_
	private String createMolIdArg() {
		return molId +
		S_UNDER + createIdArg(FragmentTool.IDX) + S_UNDER;
	}

	// at present {$id}
	private static String createIdArg(String id) {
		return S_LCURLY + S_DOLLAR + id + S_RCURLY;
	}
	
    /** convenience method.
     * 
     * @param moleculeList list to convert
     * @return list for fragments
     */
    public static List<CMLFragment> convertMolecules(List<CMLMolecule> moleculeList) {
    	List<CMLFragment> fragmentList = new ArrayList<CMLFragment>();
    	for (CMLMolecule molecule : moleculeList) {
    		FragmentConverter fragmentConverter = new FragmentConverter(molecule);
    		CMLFragment fragment =fragmentConverter.convertToFragment();
    		fragmentList.add(fragment);
    	}
    	return fragmentList;
    }
    
    /** convenience method.
     * 
     * @param moleculeList list to convert
     * @return list for fragments
     */
    public static CMLFragmentList convertMolecules(CMLMoleculeList moleculeList) {
    	CMLFragmentList fragmentList = new CMLFragmentList();
    	CMLElements<CMLMolecule> molecules = moleculeList.getMoleculeElements();
    	for (CMLMolecule molecule : molecules) {
    		FragmentConverter fragmentConverter = new FragmentConverter(molecule);
    		CMLFragment fragment =fragmentConverter.convertToFragment();
    		fragmentList.addIndexable(fragment);
    	}
    	return fragmentList;
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
