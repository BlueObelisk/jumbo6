package org.xmlcml.cml.tools;

import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.util.TstUtils.assertEqualsCanonically;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import nu.xom.Element;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLLog;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.inchi.InChIGeneratorTool;
import org.xmlcml.cml.test.CMLAssert;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.Type;
import org.xmlcml.util.TstUtils;

/**
 * test CrystalTool.
 * 
 * @author pmr
 * 
 */
public class CrystalToolTest {

	/** */
	public final static String CIF_EXAMPLES = CMLAssert.TOOLS_EXAMPLES +CMLConstants.U_S + "cif";

	CrystalTool tool1 = null;

	String mol1S = CMLConstants.S_EMPTY
			+ "<molecule "
			+ CMLConstants.CML_XMLNS
			+ ">"
			+ " <atomArray>"
			+ "  <atom id='a1' xFract='0.1'  yFract='0.2'  zFract='0.3' elementType='O'/>"
			+ "  <atom id='a2' xFract='0.15' yFract='0.25' zFract='0.35' elementType='H'/>"
			+ "  <atom id='a3' xFract='0.15' yFract='0.15' zFract='0.25' elementType='H'/>"
			+ " </atomArray>" + "</molecule>" + CMLConstants.S_EMPTY;

	CMLMolecule mol1;

	String crystal1S = CMLConstants.S_EMPTY + "<crystal " + CMLConstants.CML_XMLNS + ">"
			+ "  <cellParameter type='length'>10. 11. 12.</cellParameter>"
			+ "  <cellParameter type='angle'>90. 90. 90.</cellParameter>"
			+ "</crystal>" + CMLConstants.S_EMPTY;

	CMLCrystal crystal1;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		mol1 = (CMLMolecule) TstUtils.parseValidString(mol1S);
		crystal1 = (CMLCrystal) TstUtils.parseValidString(crystal1S);
		tool1 = new CrystalTool(mol1, crystal1);
	}

	/**
	 * Test method for
	 * 'org.xmlcml.cml.tools.CrystalTool.calculateCartesiansAndBonds()'
	 */
	@Test
	@Ignore ("reason not yet recorded")
	public void testCalculateCartesiansAndBonds() {
		// calculates cartesians and generates the bonds but does not add
		// lengths
		tool1.calculateCartesiansAndBonds();
		List<CMLBond> bonds = mol1.getBonds();
		Assert.assertEquals("bond count", 2, bonds.size());
		CMLAssert.assertEquals("bond 0", new String[] { "a1", "a2" }, bonds.get(0)
				.getAtomRefs2());
		CMLAssert.assertEquals("bond 0", new String[] { "a1", "a3" }, bonds.get(1)
				.getAtomRefs2());
		// adds the length elements
		new GeometryTool(mol1).createValenceLengths(true, true);
		List<CMLLength> lengths = MoleculeTool.getOrCreateTool(mol1)
				.getLengthElements();
		Assert.assertEquals("lengths", 2, lengths.size());
		CMLLength length = lengths.get(0);
		CMLAssert.assertEquals("length 0 atoms", new String[] { "a1", "a2" }, length
				.getAtomRefs2());
		Assert.assertEquals("length 0 value", 0.956, length.getXMLContent(),
				0.001);
		length = lengths.get(1);
		CMLAssert.assertEquals("length 0 atoms", new String[] { "a1", "a3" }, length
				.getAtomRefs2());
		Assert.assertEquals("length 0 value", 0.955, length.getXMLContent(),
				0.001);
		// calculate and add angles
		new GeometryTool(mol1).createValenceAngles(true, true);
		MoleculeTool moleculeTool1 = MoleculeTool.getOrCreateTool(mol1);
		List<CMLAngle> angles = moleculeTool1.getAngleElements();
		// FIXME
		Assert.assertEquals("angles", 1, angles.size());
		CMLAngle angle = angles.get(0);
		CMLAssert.assertEquals("length 0 atoms", new String[] { "a2", "a1", "a3" }, angle
				.getAtomRefs3());
		Assert.assertEquals("length 0 value", 116.875, angle.getXMLContent(),
				0.001);
	}

	/**
	 * test public List<Contact> getSymmetryContactsToMolecule(RealRange
	 * dist2Range);
	 * 
	 */
	@Test
	@Ignore ("reason not yet recorded")
	public void testGetSymmetryContactsToMolecule() {
		String molS = CMLConstants.S_EMPTY
				+ "<molecule id='m1' "
				+ CMLConstants.CML_XMLNS
				+ ">"
				+ "  <atomArray>"
				+ "    <atom id='a1' elementType='C' xFract='0.07' yFract='0.02' zFract='0.01'/>"
				+ "    <atom id='a2' elementType='O' xFract='0.13' yFract='0.21' zFract='0.02'/>"
				+ "    <atom id='a3' elementType='N' xFract='0.85' yFract='0.23' zFract='0.03'/>"
				+ "  </atomArray>" + "</molecule>";
		CMLMolecule mol = (CMLMolecule) TstUtils.parseValidString(molS);
		CMLSymmetry symmetry = new CMLSymmetry(new String[] { "x, y, z",
				"-x, -y, -z" });
		CMLCrystal crystal = new CMLCrystal(new double[] { 10., 20., 30., 90.,
				90., 90. });
		CrystalTool crystalTool = new CrystalTool(mol, crystal, symmetry);
		RealRange dist2Range = new RealRange(0, 10);
		List<Contact> contactList = crystalTool
				.getSymmetryContactsToMolecule(dist2Range);
		// FIXME
		Assert.assertEquals("contact list", 1, contactList.size());
	}

	private static void writeXML(String filename, CMLMolecule molecule,
			String message) {
		try {
			File file = new File(filename);
			molecule.serialize(new FileOutputStream(file), 1);
			// log.add(file, message);
		} catch (IOException e) {
			throw new RuntimeException("ERROR " + e);
		}
	}


	@SuppressWarnings("all")
	// generics
	static void writeGeometry(List list, String filename, CMLMolecule molecule) {
		try {
			FileWriter fw = new FileWriter(filename);
			if (list.size() == 0) {
				fw.write("<b>NO GEOMETRY</b>\n");
			} else if (list.get(0) instanceof CMLLength) {
				CMLLength.outputHTML(fw, list, molecule);
			} else if (list.get(0) instanceof CMLAngle) {
				CMLAngle.outputHTML(fw, list, molecule);
			} else if (list.get(0) instanceof CMLTorsion) {
				CMLTorsion.outputHTML(fw, list, molecule);
			}
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException("ERROR " + e);
		}
	}

	@SuppressWarnings("unused")
	private static void outputRingNuclei(String user, String cifname, int mol,
			CMLMolecule mergedMolecule, CMLLog log) {
		List<CMLMolecule> subMoleculeList = MoleculeTool.getOrCreateTool(
				mergedMolecule).getMoleculeList();
		int subMol = 0;
		for (CMLMolecule subMolecule : subMoleculeList) {
			InChIGeneratorTool.generateInChI(subMolecule);

			MoleculeTool subMoleculeTool = MoleculeTool
					.getOrCreateTool(subMolecule);
			List<CMLMolecule> ringNucleusList = subMoleculeTool
					.getRingNucleiMolecules();
			subMol++;
			int nuc = 0;
			for (CMLMolecule ringNucleus : ringNucleusList) {
				nuc++;
				writeXML(getOutfile(user, "nuc", cifname, mol, subMol, nuc),
						ringNucleus, "nucleus");

				String outfile = getOutfile(user, "nuc", cifname + "_SP_", mol,
						subMol, nuc);
				CMLAtomSet nucleus = new CMLAtomSet(subMolecule, MoleculeTool
						.getOrCreateTool(ringNucleus).getAtomSet().getAtomIDs());
				GeometryTool geometryTool = new GeometryTool(ringNucleus);
				// lengths
				boolean calculate = true;
				boolean add = false;
				List<CMLLength> lengthList = geometryTool.createValenceLengths(
						calculate, add);
				writeGeometry(lengthList, outfile + ".len.html", ringNucleus);
				// angles
				List<CMLAngle> angleList = geometryTool.createValenceAngles(
						calculate, add);
				writeGeometry(angleList, outfile + ".ang.html", ringNucleus);
				// torsion
				List<CMLTorsion> torsionList = geometryTool
						.createValenceTorsions(calculate, add);
				writeGeometry(torsionList, outfile + ".tor.html", ringNucleus);

				// sprout
				CMLMolecule sprout = subMoleculeTool.sprout(nucleus);
				writeXML(outfile, sprout, "sprout");
			}
		}
	}

	@SuppressWarnings("unused")
	private static void outputAtomCenteredSpecies(String outfile,
			MoleculeTool moleculeTool) {
		CMLMolecule molecule = moleculeTool.getMolecule();
		List<CMLAtom> atoms = molecule.getAtoms();
		int atomCount = 0;
		for (CMLAtom atom : atoms) {
			ChemicalElement element = atom.getChemicalElement();
			if (element.isChemicalElementType(Type.TRANSITION_METAL)) {
				atomCount++;
				List<CMLAtom> singleAtomList = new ArrayList<CMLAtom>();
				singleAtomList.add(atom);
				CMLAtomSet singleAtomSet = CMLAtomSet
						.createFromAtoms(singleAtomList);
				CMLMolecule atomSprout = moleculeTool.sprout(singleAtomSet);
				writeXML(outfile, atomSprout, "atom sprout");

				GeometryTool geometryTool = new GeometryTool(atomSprout);
				// lengths
				boolean calculate = true;
				boolean add = false;
				List<CMLLength> lengthList = geometryTool.createValenceLengths(
						calculate, add);
				writeGeometry(lengthList, outfile + ".len.html", atomSprout);
				// angles
				List<CMLAngle> angleList = geometryTool.createValenceAngles(
						calculate, add);
				writeGeometry(angleList, outfile + ".ang.html", atomSprout);
			}
		}
	}

	@SuppressWarnings("unused")
	private static void outputClusters(String user, String cifname,
			CMLMolecule molecule, CMLLog log) {
		List<Type> typeList = new ArrayList<Type>();
		typeList.add(ChemicalElement.Type.TRANSITION_METAL);
		CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
		for (CMLMolecule mol : molecules) {
			MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
			List<CMLMolecule> clusterList = moleculeTool
					.createClusters(typeList);
			log.add("CLUSTER" + clusterList.size());
			if (clusterList.size() > 0) {
				int clust = 0;
				for (CMLMolecule cluster : clusterList) {

					log.add("ATOMS " + cluster.getAtomCount());
					if (cluster.getAtomCount() > 1) {
						String[] atomIds = MoleculeTool
								.getOrCreateTool(cluster).getAtomSet()
								.getAtomIDs();
						CMLAtomSet clAtomSet = new CMLAtomSet(mol, atomIds);
						CMLMolecule sproutCluster = MoleculeTool
								.getOrCreateTool(mol).sprout(clAtomSet);
						if (sproutCluster != null) {
							writeXML(getOutfile(user, "clust", cifname
									+ "_clust", 0, 0, ++clust), sproutCluster,
									"nucleus");
						} else {
							System.err.println("No atoms in cluster!" + cifname
									+ CMLConstants.S_UNDER + (clust - 1));
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private static void outputLigands(String user, String cifname,
			CMLMolecule molecule, CMLLog log) {
		List<Type> typeList = new ArrayList<Type>();
		typeList.add(ChemicalElement.Type.TRANSITION_METAL);
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		List<CMLMolecule> ligandList = moleculeTool.createLigands(typeList);
		// LOG.debug("LIGANDS"+ligandList.size());
		log.add("LIGANDS" + ligandList.size());
		int ligCount = 0;
		for (CMLMolecule ligand : ligandList) {
			if (ligand.getAtomCount() >= 3) {
				writeXML(getOutfile(user, "ligand", cifname + "_lig", 0, 0,
						++ligCount), ligand, "ligand");
			}
		}
	}

	private static String getDir(String user) {
		String s = null;
		if (user.equals("NED")) {
			s = "E:\\cif-wrongformula";
		} else if (user.equals("PMR")) {
			// s = ACTALARGEEXAMPLESDIR;
		} else if (user.equals("PMR1")) {
			s = "C:\\acta\\2005\\06";
		} else if (user.equals("PMR2")) {
			s = "C:\\acta\\2002\\05\\cif";
		} else if (user.equals("PMR_ACTA4")) {
			s = "C:\\ActaEcifs-09-09-05\\2005\\08\\cif";
		} else if (user.equals("PMR_COD")) {
			s = "C:\\pmr\\cifdom1\\examples\\cod";
		} else if (user.equals("PMR_COD8")) {
			s = "C:\\pmr\\cifdom1\\examples\\cod\\8";
		} else if (user.equals("PMR_COD9")) {
			s = "C:\\pmr\\cifdom1\\examples\\cod\\9";
		} else if (user.equals("PMR_FAIL")) {
			s = "C:\\pmr\\cifdom1\\examples\\failures";
		} else if (user.equals("PMR_INORG")) {
			s = "C:\\pmr\\cifdom1\\examples\\inorg";
		} else if (user.equals("PMR_JED")) {
			s = "C:\\pmr\\cifdom1\\examples\\camb";
		} else if (user.equals("PMR_JOC")) {
			s = "C:\\pmr\\cifdom1\\examples\\joc";
		} else if (user.equals("PMR_LEY")) {
			s = "C:\\pmr\\cifdom1\\examples\\ley";
		} else if (user.equals("PMR_RSC")) {
			s = "C:\\pmr\\cifdom1\\examples\\rsc";
		} else if (user.equals("HEATH_TEST")) {
			s = "C:\\pmr\\cifdom1\\examples\\heath\\test";
			// s = "C:\\pmr\\cifdom1\\examples\\heath\\test1";
		}
		return s;
	}

	// private List<String> getCifPathList(String user) {
	// LOG.debug(user);
	// String dir = getDir(user);
	// if (dir == null) {
	// throw new RuntimeException("Unknown user: "+user);
	// }
	// List<String> cifPathList = new ArrayList<String>();
	// File[] fileList = new File(dir).listFiles();
	// if (fileList == null) {
	// throw new RuntimeException("CIF TEST NOT CONFIGURED - IGNORE");
	// }
	//
	// for (File file : fileList) {
	// // LOG.debug(file.getAbsolutePath());
	// if (file.toString().startsWith(S_PERIOD)) {
	// LOG.debug("File starts with .");
	// continue;
	// }
	// if (user.equals("NED")) {
	// } else if (user.startsWith("PMR")) {
	// if(!file.toString().endsWith(".cif")) {
	// continue;
	// }
	// }
	//
	// String fileName = file.getName();
	// if (user.equals("NED")) {
	// cifPathList.add(file.getAbsolutePath());
	// } else if (user.startsWith("HEATH")) {
	// cifPathList.add(dir + File.separator + fileName);
	// } else if (
	// user.startsWith("PMR")) {
	// cifPathList.add(dir + File.separator + fileName);
	// }
	// }
	// // LOG.debug("Found "+cifPathList.size()+" files.");
	// return cifPathList;
	// }

	// private String getCrystalName(String user, String cifname) {
	// String s = getDir(user);
	// if (user.startsWith("PMR")) {
	// s += File.separator + "xml" + File.separator + cifname + ".cml.xml";
	// } else if (user.equals("NED")) {
	// @SuppressWarnings("unused")
	// int idx = cifname.lastIndexOf(S_UNDER);
	// s += File.separator + cifname
	// + ".cml.xml";
	// } else if (user.startsWith("HEATH")) {
	// s += File.separator + "xml" + File.separator + cifname + ".cml.xml";
	// }
	// return s;
	// }

	private static String getOutfile(String user, String fragType,
			String cifname, int mol, int subMol, int serial) {
		String s = getDir(user);
		File dir = new File(s + File.separator + fragType);
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (user.startsWith("PMR") || user.startsWith("HEATH")
				|| user.startsWith("NED")) {
			s = dir + File.separator + cifname;
			if (mol > 0) {
				s += CMLConstants.S_UNDER + mol;
			}
			if (subMol > 0) {
				s += CMLConstants.S_UNDER + subMol;
			}
			s += CMLConstants.S_UNDER + serial + ".cml.xml";
		}
		return s;
	}

	/*
	 * private String calculateCheckcif(String cifPath/, int timeout) {
	 * PostMethod filePost = null; String checkcif = ""; try { File f = new
	 * File(cifPath); filePost = new PostMethod(
	 * "http://dynhost1.iucr.org/cgi-bin/checkcif.pl"); Part[] parts = { new
	 * FilePart("file", f), new StringPart("runtype", "fullpublication"), new
	 * StringPart("UPLOAD", "Send CIF for checking") };
	 * filePost.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new
	 * DefaultHttpMethodRetryHandler(5, false)); filePost.setRequestEntity(new
	 * MultipartRequestEntity(parts, filePost.getParams())); HttpClient client =
	 * new HttpClient();
	 * 
	 * //HttpClientParams hcp = new HttpClientParams();
	 * //hcp.setSoTimeout(timeout); //hcp.setConnectionManagerTimeout(timeout);
	 * 
	 * //client.setParams(hcp); int statusCode = client.executeMethod(filePost);
	 * if (statusCode != HttpStatus.SC_OK) {
	 * System.err.println("Could not connect to the IUCr Checkcif service."); }
	 * InputStream in = filePost.getResponseBodyAsStream(); checkcif =
	 * IOUtils.stream2String(in); } catch (IOException e) {
	 * System.err.println("Error calculating checkcif."); } finally { if
	 * (filePost != null) filePost.releaseConnection(); } return checkcif; }
	 */

	// private void addSpaceGroup(CMLCml cml) {
	// Nodes hmGroupNodes =
	// cml.query(".//cml:scalar[@dictRef='iucr:_symmetry_space_group_name_H-M']"
	// , CMLConstants.CML_XPATH);
	// if (hmGroupNodes.size() > 0) {
	// String hmGroup = hmGroupNodes.get(0).getValue();
	// Elements crystals = cml.getChildCMLElements(CMLCrystal.TAG);
	// if (crystals.size() > 0) {
	// CMLCrystal crystal = (CMLCrystal)crystals.get(0);
	// Elements symmetrys = crystal.getChildCMLElements("symmetry");
	// if (symmetrys.size() > 0) {
	// symmetrys.get(0).addAttribute(new Attribute("spaceGroup", hmGroup));
	// }
	// }
	// }
	// }
	
    /**
     * 
     */
    @Test
    public void testAnnotateSpaceGroupMultiplicities() {
    	String moleculeS = "" +
    			"<molecule "+CMLConstants.CML_XMLNS+">" +
    					"<atomArray>" +
    					"<atom id='a1' elementType='Cu' xFract='0' yFract='0' zFract='0'/>" +
    					"<atom id='a2' elementType='O' xFract='0' yFract='0' zFract='0.3'/>" +
    					"<atom id='a3' elementType='N' xFract='0' yFract='0.2' zFract='0.3'/>" +
    					"<atom id='a4' elementType='H' xFract='0.1' yFract='0.2' zFract='0.3'/>" +
    					"</atomArray>" +
    					"<crystal/>" +  // because CrystalTool expects a crystal
				"</molecule>";
    	CMLMolecule molecule = null;
    	String symmetryS = "" +
		"<symmetry "+CMLConstants.CML_XMLNS+">" +
		"<transform3>1 0 0 0   0 1 0 0  0 0 1 0   0 0 0 1</transform3>" +
		"<transform3>1 0 0 0   0 1 0 0  0 0 -1 0   0 0 0 1</transform3>" +
		"<transform3>1 0 0 0   0 -1 0 0  0 0 1 0   0 0 0 1</transform3>" +
		"<transform3>1 0 0 0   0 -1 0 0  0 0 -1 0   0 0 0 1</transform3>" +
		"<transform3>-1 0 0 0   0 1 0 0  0 0 1 0   0 0 0 1</transform3>" +
		"<transform3>-1 0 0 0   0 1 0 0  0 0 -1 0   0 0 0 1</transform3>" +
		"<transform3>-1 0 0 0   0 -1 0 0  0 0 1 0   0 0 0 1</transform3>" +
		"<transform3>-1 0 0 0   0 -1 0 0  0 0 -1 0   0 0 0 1</transform3>" +
		"</symmetry>";
    	CMLSymmetry symmetry = null;
		String expectedS = "" +
		"<molecule xmlns='http://www.xml-cml.org/schema'>"+
		  "<atomArray>"+
		  " <atom id='a1' elementType='Cu' xFract='0.0' yFract='0.0' zFract='0.0'>"+
		  "  <scalar dataType='xsd:integer' dictRef='cml:mult'>8</scalar>"+
		  "</atom>"+
		  "<atom id='a2' elementType='O' xFract='0.0' yFract='0.0' zFract='0.3'>"+
		  "  <scalar dataType='xsd:integer' dictRef='cml:mult'>4</scalar>"+
		  "</atom>"+
		  "<atom id='a3' elementType='N' xFract='0.0' yFract='0.2' zFract='0.3'>"+
		  "  <scalar dataType='xsd:integer' dictRef='cml:mult'>2</scalar>"+
		  "</atom>"+
		  "<atom id='a4' elementType='H' xFract='0.1' yFract='0.2' zFract='0.3'/>"+
		  "</atomArray>"+
		  "<crystal/>"+
		"</molecule>";
		Element expected = null;
    	try {
			molecule = (CMLMolecule) new CMLBuilder().parseString(moleculeS);
			symmetry = (CMLSymmetry) new CMLBuilder().parseString(symmetryS);
			expected = new CMLBuilder().parseString(expectedS);
		} catch (Exception e) {
			Assert.fail("BUG "+e);
		}
		CrystalTool crystalTool = new CrystalTool(molecule, symmetry);		
		crystalTool.annotateSpaceGroupMultiplicities();
		assertEqualsCanonically("symmetry", expected, molecule, true);
	}

}
