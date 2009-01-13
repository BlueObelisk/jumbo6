package org.xmlcml.cml.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Serializer;

import org.xmlcml.cml.attribute.IdAttribute;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
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
import org.xmlcml.euclid.Util;

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
	 * @return fragment
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
			throw new RuntimeException("molecule must have id");
		}
		molecule.addAttribute(new Attribute("role", "fragment"));
		CMLArg arg = new CMLArg();
		arg.setParameterName(FragmentTool.IDX);
		molecule.appendChild(arg);
		arg = new CMLArg();
		arg.setParentAttribute("id");
		arg.appendChild(molId + CMLConstants.S_UNDER + CMLConstants.S_LCURLY + CMLConstants.S_DOLLAR + FragmentTool.IDX + CMLConstants.S_RCURLY);
		molecule.appendChild(arg);
	
		createAtomArguments();
		createBondArguments();
		createLengthArguments();
		createAngleArguments();
		createTorsionArguments();
	
		// deal with R
		List<Node> rGroups = CMLUtil.getQueryNodes(molecule,
				".//"+CMLAtom.NS+"[@elementType='R']", CMLConstants.CML_XPATH);
		for (Node node : rGroups) {
			try {
				AtomTool.getOrCreateTool((CMLAtom) node).translateToCovalentRadius();
			} catch (RuntimeException e) {
				// no coordinates, not an error
			}
		}
		CMLFragment fragment = new CMLFragment();
		fragment.setId(molecule.getId());
		FragmentTool.getOrCreateTool(fragment).setMolecule(molecule);
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
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		List<CMLLength> lengths = moleculeTool.getLengthElements();
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
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		List<CMLTorsion> torsions = moleculeTool.getTorsionElements();
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
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		List<CMLAngle> angles = moleculeTool.getAngleElements();
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
		S_UNDER + createIdArg(FragmentTool.IDX) + CMLConstants.S_UNDER;
	}

	// at present {$id}
	private static String createIdArg(String id) {
		return CMLConstants.S_LCURLY + CMLConstants.S_DOLLAR + id + CMLConstants.S_RCURLY;
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
    	FragmentListTool fragmentListTool = FragmentListTool.getOrCreateTool(fragmentList);
    	CMLElements<CMLMolecule> molecules = moleculeList.getMoleculeElements();
    	for (CMLMolecule molecule : molecules) {
    		FragmentConverter fragmentConverter = new FragmentConverter(molecule);
    		CMLFragment fragment =fragmentConverter.convertToFragment();
    		fragmentListTool.addFragment(fragment);
    	}
    	return fragmentListTool.getFragmentList();
    }
    
	/**Call with to set up polinfo fragments:
	 * -indir src/test/java/org/xmlcml/cml/tools/examples/molecules/polyinfomol -outdir src/test/java/org/xmlcml/cml/tools/examples/molecules/polyinfo/
	 * @param args
	 */
	public static void main(String[] args) {
		
		CommandlineFragmentConverter converter = new CommandlineFragmentConverter(args);	
		
		if (converter.indir != null) {
			File[] files = new File(converter.indir).listFiles();
			for (File file : files) {
				try {
					CMLMolecule molecule = (CMLMolecule) new CMLBuilder().build(new FileInputStream(file)).getRootElement();
					FragmentConverter fragmentConverter = new FragmentConverter(molecule);
					CMLFragment fragment = fragmentConverter.convertToFragment();
					if(converter.outdir!=null){converter.setOutputFile(converter.outdir+"/"+file.getName());}
					else{System.err.println("indir but no outdir");
					System.exit(-1);
					}
					converter.writeFragment(fragment);
				} catch (Exception e) {
					System.err.println("Cannot find/parse file: "+file.getAbsolutePath());
				}
			}
		}
		else if(converter.infile !=null){
			File file = new File(converter.infile);
			try {
				CMLMolecule molecule = (CMLMolecule) new CMLBuilder().build(new FileInputStream(file)).getRootElement();
				FragmentConverter fragmentConverter = new FragmentConverter(molecule);
				CMLFragment fragment = fragmentConverter.convertToFragment();
				if(converter.outfile!=null){converter.setOutputFile(converter.outfile);}
				else{System.err.println("infile but no outfile");
				System.exit(-1);
				}
				converter.writeFragment(fragment);
			} catch (Exception e) {
				System.err.println("Cannot find/parse file: "+file.getAbsolutePath());
			}
			
		}
	}

	
	private static class CommandlineFragmentConverter {
		String infile = null;
		String outfile = null;
		String indir = null;
		String outdir = null;
		File output=null;
		
		/**
		 * @param args
		 */
		public CommandlineFragmentConverter(String[] args){
			if (args.length==0 || args.length%2==1){
				usage();
				System.exit(-1);
			}
			for(int i=0;i<args.length;i++){
				if(args[i].equalsIgnoreCase("-in")) {
					infile = args[++i];
				} else if(args[i].equalsIgnoreCase("-indir")) {
					indir = args[++i];
				} else if(args[i].equalsIgnoreCase("-out")) {
					outfile = args[++i];
				} else if(args[i].equalsIgnoreCase("-outdir")) {
					outdir = args[++i];
				} else {
					Util.println("Unknown arg: "+args[i]);
				}
			}
		}
		
		private void usage(){
	    	Util.println("Usage: java "+FragmentConverter.class.getName()+" <options>");
	    	Util.println("  -in (filename) //Input File");
	    	Util.println("  -out (filename) //Output File");
	    	Util.println("  -indir (dirname) //Whole directory to be input");
	    	Util.println("  -outdir (dirname) //Whole directory to be output");
	    }
		
		private void setOutputFile(String file){
			output = new File(file);
		}
		private void writeFragment(CMLFragment fragment){
			OutputStream out=null;
			Document doc = new Document(fragment);
			try{
			out = new BufferedOutputStream(new FileOutputStream(output));
			Serializer serializer = new Serializer(out, "ISO-8859-1");
		      serializer.setIndent(4);
		      serializer.write(doc);
			}
			catch (IOException ex){
				System.err.println("Failed outputting "+ex); 
			}
			finally{
				if (out != null) {
					Util.println("Writing fragment "+output.getAbsolutePath());
		    		try {
						out.close();
					} catch (IOException e) {
						System.err.println("Error while closing outputstrem");
						e.printStackTrace();
					}
		    	}
				
			}
	    	
	    }
	}
	
}
