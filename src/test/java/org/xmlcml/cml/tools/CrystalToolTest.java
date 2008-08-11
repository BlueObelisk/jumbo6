package org.xmlcml.cml.tools;

import static org.xmlcml.cml.base.CMLConstants.CML_XMLNS;
import static org.xmlcml.cml.tools.AbstractToolTest.TOOLS_EXAMPLES;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_UNDER;
import static org.xmlcml.euclid.EuclidConstants.U_S;
import static org.xmlcml.util.TestUtils.assertEqualsCanonically;
import static org.xmlcml.util.TestUtils.neverThrow;
import static org.xmlcml.util.TestUtils.parseValidString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.sf.jniinchi.INCHI_RET;
import nu.xom.Element;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLLog;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLLog.Severity;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLCrystal;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLSymmetry;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.inchi.InChIGenerator;
import org.xmlcml.cml.inchi.InChIGeneratorFactory;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.test.StringTestBase;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.Type;
/**
 * test CrystalTool.
 *
 * @author pmr
 *
 */
public class CrystalToolTest  {

    /** */
    public final static String CIF_EXAMPLES = TOOLS_EXAMPLES+U_S+"cif";

    CrystalTool tool1 = null;

    String mol1S = S_EMPTY
            + "<molecule "
            + CML_XMLNS
            + ">"
            + " <atomArray>"
            + "  <atom id='a1' xFract='0.1'  yFract='0.2'  zFract='0.3' elementType='O'/>"
            + "  <atom id='a2' xFract='0.15' yFract='0.25' zFract='0.35' elementType='H'/>"
            + "  <atom id='a3' xFract='0.15' yFract='0.15' zFract='0.25' elementType='H'/>"
            + " </atomArray>" + "</molecule>" + S_EMPTY;

    CMLMolecule mol1;

    String crystal1S = S_EMPTY + "<crystal " + CML_XMLNS + ">"
            + "  <cellParameter type='length'>10. 11. 12.</cellParameter>"
            + "  <cellParameter type='angle'>90. 90. 90.</cellParameter>"
            + "</crystal>" + S_EMPTY;

    CMLCrystal crystal1;

    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        mol1 = (CMLMolecule) parseValidString(mol1S);
        crystal1 = (CMLCrystal) parseValidString(crystal1S);
        tool1 = new CrystalTool(mol1, crystal1);
    }

    /**
     * Test method for
     * 'org.xmlcml.cml.tools.CrystalTool.calculateCartesiansAndBonds()'
     */
    @Test
    @Ignore
    public void testCalculateCartesiansAndBonds() {
        // calculates cartesians and generates the bonds but does not add
        // lengths
        tool1.calculateCartesiansAndBonds();
        List<CMLBond> bonds = mol1.getBonds();
        Assert.assertEquals("bond count", 2, bonds.size());
        StringTestBase.assertEquals("bond 0", new String[] { "a1", "a2" }, bonds.get(0)
                .getAtomRefs2());
        StringTestBase.assertEquals("bond 0", new String[] { "a1", "a3" }, bonds.get(1)
                .getAtomRefs2());
        // adds the length elements
        new GeometryTool(mol1).createValenceLengths(true, true);
        CMLElements<CMLLength> lengths = mol1.getLengthElements();
        Assert.assertEquals("lengths", 2, lengths.size());
        CMLLength length = lengths.get(0);
        StringTestBase.assertEquals("length 0 atoms", new String[] { "a1", "a2" },
                length.getAtomRefs2());
        Assert.assertEquals("length 0 value", 0.956, length.getXMLContent(),
                0.001);
        length = lengths.get(1);
        StringTestBase.assertEquals("length 0 atoms", new String[] { "a1", "a3" },
                length.getAtomRefs2());
        Assert.assertEquals("length 0 value", 0.955, length.getXMLContent(),
                0.001);
        // calculate and add angles
        new GeometryTool(mol1).createValenceAngles(true, true);
        CMLElements<CMLAngle> angles = mol1.getAngleElements();
        // FIXME
        Assert.assertEquals("angles", 1, angles.size());
        CMLAngle angle = angles.get(0);
        StringTestBase.assertEquals("length 0 atoms",
                new String[] { "a2", "a1", "a3" }, angle.getAtomRefs3());
        Assert.assertEquals("length 0 value", 116.875, angle.getXMLContent(),
                0.001);
    }

    /**
     * test public List<Contact> getSymmetryContactsToMolecule(RealRange
     * dist2Range);
     *
     */
    @Test
    @Ignore
    public void testGetSymmetryContactsToMolecule() {
        String molS = S_EMPTY
                + "<molecule id='m1' "
                + CML_XMLNS
                + ">"
                + "  <atomArray>"
                + "    <atom id='a1' elementType='C' xFract='0.07' yFract='0.02' zFract='0.01'/>"
                + "    <atom id='a2' elementType='O' xFract='0.13' yFract='0.21' zFract='0.02'/>"
                + "    <atom id='a3' elementType='N' xFract='0.85' yFract='0.23' zFract='0.03'/>"
                + "  </atomArray>" + "</molecule>";
        CMLMolecule mol = null;
        try {
            mol = (CMLMolecule) new CMLBuilder().parseString(molS);
        } catch (Exception e) {
            neverThrow(e);
        }
        CMLSymmetry symmetry = new CMLSymmetry(new String[] { "x, y, z", "-x, -y, -z" });
        CMLCrystal crystal = new CMLCrystal(new double[] { 10., 20., 30., 90.,
                90., 90. });
        CrystalTool crystalTool = new CrystalTool(mol, crystal, symmetry);
        RealRange dist2Range = new RealRange(0, 10);
        List<Contact> contactList = crystalTool
                .getSymmetryContactsToMolecule(dist2Range);
        // FIXME
        Assert.assertEquals("contact list", 1, contactList.size());
    }

    /**
     * test public List<Contact> getSymmetryContactsToMolecule(RealRange
     * dist2Range); this requires CIF2CMLLib
     */
    @Test
    @Ignore
    public void testGetSymmetryContactsInCIF1() {
//
//        String[] cifnames = {
//                "ac6175", // tetrachlorobenzene 
//                "cf6158", // boronic dimer, C-C bond 
//                "cf6161", // Ni phosphine OK
//                "cf6162", // trehalose OK
//                "cf6165", // N-C6H4-N OK
//                "ci6106", // Hg-Hg dimer 
//                "ci6110", // Ru2S2 OK
//                "na6153", // cobalt at origin OK
//                "na6156", // CuO6 
//                "tk6062", // S-C=C-S 
//                "ya6102", // Nickel etc. Maybe polymer
//                "xu6033", // benzene acid
//        };
//
//        int[] contactCount = { 
//                4, // "ac6175", // tetrachlorbenzene
//                1, // "cf6158", // boronic dimer, C-C bond 
//                3, // "cf6161", // Ni phosphine OK
//                2, // "cf6162", // trehalose OK
//                2, // "cf6165", // N-C6H4-N OK
//                4, // "ci6106", // Hg-Hg dimer 
//                3, // "ci6110a", // Ru2S2 OK
//                8, // "na6153", // cobalt at origin OK
//                3, // "na6156", // CuO6 
//                1, // "tk6062", // S-C=C-S 
//                26, // "ya6102", // Nickel etc. Maybe polymer
//                15, // "ya6102", // Nickel etc. Maybe polymer
//        };
//
//        String[][] ss = { 
//                new String[] {"a18", "a8", "1-x,-y,1-z" },
//                new String[] { "a5", "a5", "1-x,1-y,1-z" },
//                new String[] { "a1", "a2", "-x,-y,1-z" },
//                new String[] { "a1", "a1", "1-x,2-y,+z" },
//                new String[] { "a18", "a20", "-x,-y,-z" },
//                new String[] { "a1", "a3", "2-x,1-y,-z" },
//                new String[] { "a1", "a2", "-x,1-y,1-z" },
//                new String[] { "a1", "a1", "-x,-y,-z" },
//                new String[] { "a1", "a5", "-x,-y,1-z" },
//                new String[] { "a2", "a2", "-x,1-y,1-z" },
//                new String[] { "a1", "a1", "-x,+y,+z" },
//                new String[] { "a1", "a3", "+x,1/2-y,+z" }, };
//
//        double[] contactDist = new double[] { 
//                1.3998651087476337,
//                1.5104637357820034, 
//                2.225692433254234,
//                0.0, 
//                1.3839263461849156, 
//                2.290218121719922, 
//                2.363649253276249,
//                0.0, 
//                1.917472277949926, 
//                1.350378623134038,
//                0.0,
//                1.9442552151469323, };
//
//        LegacyConverterOld cifConverter = null;
//        try {
//            cifConverter = LegacyConverterFactoryOld
//                 .createLegacyConverter("org.xmlcml.cml.legacy.cif.CIFConverter");
//        } catch (Throwable t) {
//            throw new CMLRuntimeException("Cannot create legacyConverter: "+t);
//        }
////        CMLLog log = new CMLLog();
//        cifConverter.setControls("NO_GLOBAL", "SKIP_HEADER");
//
//        for (int i = 0; i < cifnames.length; i++) {
//            String cifname = cifnames[i];
//            // System.out.println("CIF: "+cifname);
//            try {
//                InputStream in = Util.getInputStreamFromResource(
//                        CIF_EXAMPLES + U_S+ cifname + "sup1.cif");
//                cifConverter.parseLegacy(in);
//                in.close();
//            } catch (Throwable e) {
//                System.err.println("SKIPPED" + e);
//                continue;
//            }
//            List<CMLCml> cmlList = cifConverter.getCMLCmlList();
//            for (CMLCml cml : cmlList) {
//                CMLCrystal crystal = CMLCrystal.getContainedCrystal(cml);
//                CMLSymmetry symmetry = CMLSymmetry.getContainedSymmetry(crystal);
//                CMLMolecule mol = getMolecule(cml);
//                mol.createCartesiansFromFractionals(crystal);
//                MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
//                Assert.assertNotNull("molecule not null", mol);
//
//                CrystalTool crystalTool = new CrystalTool(mol, crystal, symmetry);
//                RealRange dist2Range = new RealRange(0, 2.5 * 2.5);
//                List<Contact> contactList = moleculeTool.getSymmetryContacts(
//                        dist2Range, crystalTool);
//                Assert.assertEquals("contact count", contactCount[i], contactList
//                        .size());
//                if (contactCount[i] > 0) {
//                    Contact contact = contactList.get(0);
//                    Assert.assertEquals("contact atom 1 (" + i + S_RBRAK, ss[i][0],
//                            contact.getFromAtom().getId());
//                    Assert.assertEquals("contact atom 2 (" + i + S_RBRAK, ss[i][1],
//                            contact.getToAtom().getId());
//                    String cc = contact.getTransform3().getEuclidTransform3()
//                            .getCrystallographicString();
//                    Assert.assertEquals("contact operator (" + i + S_RBRAK, ss[i][2],
//                            cc);
//                    Assert.assertEquals("contact distance (" + i + S_RBRAK,
//                            contactDist[i], contact.getDistance(), EPS);
//                }
//
////                boolean addBonds = true;
//                CMLMolecule mergedMolecule = crystalTool.getMergedMolecule(mol,
//                        contactList);
//                String filename = Util.getTEMP_DIRECTORY() + File.separator
//                        + "cif" + File.separator + cifname + XML_SUFF;
//                try {
//                    File file = Util.createNewFile(filename);
//                    mergedMolecule.serialize(new FileOutputStream(file), 1);
// //                   log.add(file, "???");
//                } catch (IOException e) {
//                    throw new CMLRuntimeException("ERROR " + e);
//                }
//            }
//        }
    }

//    private CMLMolecule getMolecule(CMLElement cml) {
//        Nodes moleculeNodes = cml.query(CMLMolecule.NS, CML_XPATH);
//        if (moleculeNodes.size() != 1) {
//            throw new CMLRuntimeException("NO MOLECULE FOUND");
//        }
//        return (CMLMolecule) moleculeNodes.get(0);
//    }

    /**
     * test structure generation;
     */
    @Test
    @Ignore
    public void testAnalyzeCIFs() {
        System.err.println("NOT YET FINALISED - IGNORE TEST");
        analyzeCIFs(S_EMPTY);
    }
    
    private static void analyzeCIFs(String user) {
        if (user == null || user.trim().equals(S_EMPTY)) {
            // set these as required

//       user = "PMR";
//       user = "PMR1";
//       user = "PMR2";
//      user = "PMR_ACTA4";
//       user = "PMR_INORG";
//       user = "PMR_LEY";
//       user = "PMR_JED";
//       user = "PMR_JOC";
//       user = "PMR_COD";
//       user = "PMR_COD8";
//       user = "PMR_COD9";
//        user = "PMR_RSC";
//      String user = "PMR_FAIL";
       user = "NED";
//         user = "HEATH_TEST";
//            user = null;
        }

//        //CMLLog log = new CMLLog();
//        if (user == null) return;
//        //log.add("USER "+user);
//
//        List<String> cifPathList = getCifPathList(user);
//        
//        LegacyConverterOld cifConverter = null;
//        try {
//            cifConverter = LegacyConverterFactoryOld.createLegacyConverter(
//                "org.xmlcml.cml.legacy.cif.CIFConverter");
//        } catch (Throwable t) {
//            throw new CMLRuntimeException("Cannot create legacyConverter "+t);
//        }
//        cifConverter.setControls("NO_GLOBAL", "SKIP_ERRORS", "SKIP_HEADER");
//        int count = 0;
//        int MAX = 10000000;
//        StringBuffer sb = new StringBuffer();
//        for (String filename : cifPathList) {
//            if (count++ >= MAX) break;
//            int iCif = filename.indexOf(".cif");
//            if (iCif == -1) {
//                continue;
//            }
//            String cifname = filename.substring(0, iCif);
//            cifname = cifname.substring(cifname.lastIndexOf(File.separator)+1);
//            System.out.println("======================="+cifname+"=======================");
//            //log.add("======================="+cifname+"=======================");
//            boolean skip =
////                true ||
//                    "br6199sup1".equals(cifname) ||
//                    "bt6695sup1".equals(cifname) ||
//                    "cf6436sup1".equals(cifname) ||
//                    "cv6537sup1".equals(cifname) ||
//                    "hb6196sup1".equals(cifname) ||
//                    "lh6473sup1".equals(cifname) ||
//                    "om6250sup1".equals(cifname) ||
//                    "su6212sup1".equals(cifname) ||
//                    "su6219sup1".equals(cifname) ||
//                    "wm6077sup1".equals(cifname) ||
//                    "wn6362sup1".equals(cifname) ||
//                    
//                    false;
//            if (!skip && false) {
//                System.err.println("SKIPPED: "+cifname);
//                continue;
//            }
//            
//            //String calculatedCheckCif = calculateCheckcif(filename);
//    		//String ccPath = filename+".calculated.checkcif.html";
//    		//IOUtils.writeText(calculatedCheckCif, ccPath);
//            
//            try {
//                cifConverter.parseLegacy(new FileReader(filename));
//            } catch (Exception e) {
//            	e.printStackTrace();
//                //log.add(e, " in "+cifname);
//                continue;
//            }
//            if (cifname.endsWith("sup1")) {
//                cifname = cifname.substring(0, cifname.length()-4);
//            }
//            List<CMLCml> cmlList = cifConverter.getCMLCmlList();
//            System.out.println(cmlList.size());
//            int mol = 0;
//            for (CMLCml cml : cmlList) {
//                mol++;
//                try {
//                    CMLMolecule molecule = getMolecule(cml);
//                    //molecule.setLog(log);
//                    MoleculeTool moleculeTool = MoleculeTool.getOrCreateMoleculeTool(molecule);
//                    
//                    moleculeTool.createCartesiansFromFractionals();
//                    molecule.setId(cifname+((molecule.getId() == null) ? S_EMPTY : S_UNDER+molecule.getId()));
//                    try {
//                    	DisorderToolControls dm = new DisorderToolControls(ProcessControl.LOOSE);
//						DisorderTool dt = new DisorderTool(molecule, dm);
//						dt.resolveDisorder();
//					} catch (Exception e) {
//						e.printStackTrace();
//						System.err.println("Problem processing disorder: "+e.getMessage());
//						sb.append(filename+": - "+e.getMessage()+"\n");
//						continue;
//					}
//					// at this point no bonds have been calculated
//					Nodes moiFormNodes = cml.query(".//"+CMLFormula.NS+"[@dictRef='iucr:_chemical_formula_moiety']", CML_XPATH);
//					CMLFormula moietyFormula = null;
//					if (moiFormNodes.size() > 0) {
//						moietyFormula = (CMLFormula)moiFormNodes.get(0);
//					} 
//					CrystalTool crystalTool = new CrystalTool(molecule);
//					CMLMolecule mergedMolecule = null;
//					mergedMolecule = crystalTool.calculateCrystallochemicalUnit(new RealRange(0, 2.8 * 2.8));
//
//					/*
//					// only want to move organic species the second time round to make sure all organic species
//					// present are complete.
//					// 1. must remove metals atoms and bonds
//					// 2. partition into molecules (? maybe don't need this step)
//					// 3. calc crystallochemical unit
//					Map<List<CMLAtom>, List<CMLBond>> metalAtomBondMap = MoleculeTool.removeMetalAtomsAndBonds(mergedMolecule, false);
//					new ConnectionTableTool(mergedMolecule).partitionIntoMolecules();
//					CrystalTool cryst2 = new CrystalTool(mergedMolecule);
//					CMLMolecule merged2 = null;
//					merged2 = cryst2.calculateCrystallochemicalUnit(new RealRange(0, 2.8 * 2.8));
//					new ConnectionTableTool(merged2).flattenMolecules();
//					MoleculeTool.addMetalAtomsAndBonds(merged2, metalAtomBondMap);
//					*/
//
//					cml.appendChild(mergedMolecule);
//					molecule.detach();
//
//					for (CMLMolecule subMol : mergedMolecule.getDescendantsOrMolecule()) {
//						if (!DisorderTool.isDisordered(subMol)) {
//							ValencyTool subMolTool = new ValencyTool(subMol);
//							subMolTool.adjustBondOrdersAndChargesToValency(moietyFormula);
//						} else {
//							System.out.println("molecule is disordered");
//						}
//					}
//                    //addSpaceGroup(cml);
//                    for (CMLMolecule subMol : mergedMolecule.getDescendantsOrMolecule()) {
//                    	StereochemistryTool st = new StereochemistryTool(subMol);
//                    	//st.add3DStereo();
//                    }
//                    // full cif
//                    writeCML(getCrystalName(user, cifname+S_UNDER+mol), cml, "full cif");
//                    // ring nuclei
//                    //outputRingNuclei(user, cifname, mol, mergedMolecule, log);
//                    // atom centered species
//                	@SuppressWarnings("unused")
//                    String outfile = getOutfile(user, "atom", cifname+"_ATOM_", mol, 0, 0);
//                    //outputAtomCenteredSpecies(outfile, moleculeTool, log);
//                    
//                    // clusters
//                    // outputClusters(user, cifname, mergedMolecule, log);
//                    // ligands
//                    //outputLigands(user, cifname, mergedMolecule, log);
//                } catch (CMLRuntimeException e) {
//                    e.printStackTrace();
//                    System.err.println("Cannot process CIF: "+e);
//                    continue;
//                }
//            }
//        }
//        String dir = getDir(user);
//        try {
//            File file = new File("E:/disordered-cifs.txt");
//            FileWriter out = new FileWriter(file);
//            out.write(sb.toString());
//            out.close();
//        } catch (IOException e) {
//            System.err.println("Unhandled exception:" + e.toString());
//        }
//        try {
//            FileWriter fw = new FileWriter(dir+File.separator+"log.xml");
//            //log.writeXML(fw);
//            fw.close();
//        } catch (Exception e) {
//            throw new CMLRuntimeException("ERROR "+e);
//        }
    }
    
//    private void writeCML(String filename, CMLCml cml, String message) {
//    	try {
//            File file = new File(filename);
//            cml.serialize(new FileOutputStream(file), 1);
//            //log.add(file, message);
//        } catch (IOException e) {
//            throw new CMLRuntimeException("ERROR "+e);
//        }
//    }
    
    private static void writeXML(String filename, CMLMolecule molecule, String message) {
        try {
            File file = new File(filename);
            molecule.serialize(new FileOutputStream(file), 1);
            //log.add(file, message);
        } catch (IOException e) {
            throw new CMLRuntimeException("ERROR "+e);
        }
    }

    
    private static void generateInChI(CMLMolecule molecule, CMLLog log) {
    	try {
	    	// Generate factory - throws CMLException if native code does not load
	    	InChIGeneratorFactory factory = new InChIGeneratorFactory();
	    	// Get InChIGenerator
	    	InChIGenerator igen = factory.getInChIGenerator(molecule);
	    	
	    	INCHI_RET ret = igen.getReturnStatus();
	    	if (!INCHI_RET.OKAY.equals(ret)) {
	    		log.add(Severity.WARNING, igen.getMessage());
	    	}
	    	
	    	String inchi = igen.getInchi();
	    	log.add("INCHI: "+inchi);
    	 } catch (Throwable e) {
    		 e.printStackTrace();
             //log.add(e, "BUG ");
         }
    }

    @SuppressWarnings("all") // generics    
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
            throw new CMLRuntimeException("ERROR "+e);
        }
    }

	@SuppressWarnings("unused")
    private static void outputRingNuclei(String user, String cifname, int mol, 
            CMLMolecule mergedMolecule, CMLLog log) {
        List<CMLMolecule> subMoleculeList =
            MoleculeTool.getOrCreateTool(mergedMolecule).getMoleculeList();
        int subMol = 0;
        for (CMLMolecule subMolecule : subMoleculeList) {
            generateInChI(subMolecule, log);
            
            MoleculeTool subMoleculeTool = MoleculeTool.getOrCreateTool(subMolecule);
            List<CMLMolecule> ringNucleusList =
                subMoleculeTool.getRingNucleiMolecules();
            subMol++;
            int nuc = 0;
            for (CMLMolecule ringNucleus : ringNucleusList) {
                nuc++;
                writeXML(getOutfile(user, "nuc", cifname, mol, subMol, nuc),
                        ringNucleus, "nucleus");
                
                String outfile = getOutfile(user, "nuc", cifname+"_SP_", mol, subMol, nuc);
                CMLAtomSet nucleus = new CMLAtomSet(subMolecule, ringNucleus.getAtomSet().getAtomIDs());
                GeometryTool geometryTool = new GeometryTool(ringNucleus);
                // lengths 
                boolean calculate = true;
                boolean add = false;
                List<CMLLength> lengthList = 
                    geometryTool.createValenceLengths(calculate, add);
                writeGeometry(lengthList, outfile+".len.html", ringNucleus);
                // angles
                List<CMLAngle> angleList = 
                    geometryTool.createValenceAngles(calculate, add);
                writeGeometry(angleList, outfile+".ang.html", ringNucleus);
                // torsion
                List<CMLTorsion> torsionList = 
                    geometryTool.createValenceTorsions(calculate, add);
                writeGeometry(torsionList, outfile+".tor.html", ringNucleus);
    
                // sprout
                CMLMolecule sprout = subMoleculeTool.sprout(nucleus);
                writeXML(outfile, sprout, "sprout");
            }
        }
    }

//	@SuppressWarnings("unused")
//    private static void outputAtomCenteredSpecies(
//            String outfile, MoleculeTool moleculeTool) {
//        CMLMolecule molecule = moleculeTool.getMolecule();
//        List<CMLAtom> atoms = molecule.getAtoms();
//        int atomCount = 0;
//        for (CMLAtom atom : atoms) {
//            ChemicalElement element = atom.getChemicalElement();
//            if (element.isChemicalElementType(
//                Type.TRANSITION_METAL)) {
//                atomCount++;
//                List<CMLAtom> singleAtomList = new ArrayList<CMLAtom>();
//                singleAtomList.add(atom);
//                CMLAtomSet singleAtomSet = CMLAtomSet.createFromAtoms(singleAtomList);
//                CMLMolecule atomSprout = moleculeTool.sprout(singleAtomSet);
//                writeXML(outfile, atomSprout, "atom sprout");
//                
//                GeometryTool geometryTool = new GeometryTool(atomSprout);
//                // lengths 
//                boolean calculate = true;
//                boolean add = false;
//                List<CMLLength> lengthList = 
//                    geometryTool.createValenceLengths(calculate, add);
//                writeGeometry(lengthList, outfile+".len.html", atomSprout);
//                // angles
//                List<CMLAngle> angleList = 
//                    geometryTool.createValenceAngles(calculate, add);
//                writeGeometry(angleList, outfile+".ang.html", atomSprout);
//            }
//        }
//    }

	@SuppressWarnings("unused")
    private static void outputClusters(
        String user, String cifname, CMLMolecule molecule, CMLLog log) {
        List<Type> typeList = new ArrayList<Type>();
        typeList.add(ChemicalElement.Type.TRANSITION_METAL);
        CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
        for (CMLMolecule mol : molecules) {
        	MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(mol);
        	List<CMLMolecule> clusterList = 
        		moleculeTool.createClusters(typeList);
        	log.add("CLUSTER"+clusterList.size());
        	if (clusterList.size() > 0) {
        		int clust = 0;
        		for (CMLMolecule cluster : clusterList) {

        			log.add("ATOMS "+cluster.getAtomCount());
        			if (cluster.getAtomCount() > 1) {
        				String[] atomIds = cluster.getAtomSet().getAtomIDs();
        				CMLAtomSet clAtomSet = new CMLAtomSet(mol, atomIds);
        				CMLMolecule sproutCluster = MoleculeTool.getOrCreateTool(mol).sprout(clAtomSet);
        				if (sproutCluster != null) {
        					writeXML(getOutfile(user, "clust", cifname+"_clust", 0, 0, ++clust),
        							sproutCluster, "nucleus");
        				} else {
        					System.err.println("No atoms in cluster!"+cifname+S_UNDER+(clust-1));
        				}
        			}
        		}
        	}
        }
    }

	@SuppressWarnings("unused")
    private static void outputLigands(
        String user, String cifname, CMLMolecule molecule, CMLLog log) {
        List<Type> typeList = new ArrayList<Type>();
        typeList.add(ChemicalElement.Type.TRANSITION_METAL);
        MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
        List<CMLMolecule> ligandList = 
            moleculeTool.createLigands(typeList);
//        System.out.println("LIGANDS"+ligandList.size());
        log.add("LIGANDS"+ligandList.size());
        int ligCount = 0;
        for (CMLMolecule ligand : ligandList) {
            if (ligand.getAtomCount() >= 3) {
                writeXML(getOutfile(user, "ligand", cifname+"_lig", 0, 0, ++ligCount),
                    ligand, "ligand");
            }
        }
    }
    
    private static String getDir(String user) {
        String s = null;
        if (user.equals("NED")) {
            s = "E:\\cif-wrongformula";
        } else if (user.equals("PMR")) {
//            s = ACTALARGEEXAMPLESDIR;
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
//            s = "C:\\pmr\\cifdom1\\examples\\heath\\test1";
        }
        return s;
    }

//    private List<String> getCifPathList(String user) {
//    	System.out.println(user);
//        String dir = getDir(user);
//        if (dir == null) {
//            throw new CMLRuntimeException("Unknown user: "+user);
//        }
//        List<String> cifPathList = new ArrayList<String>();
//        File[] fileList = new File(dir).listFiles();
//        if (fileList == null) {
//        	throw new CMLRuntimeException("CIF TEST NOT CONFIGURED - IGNORE");
//        }
//
//        for (File file : fileList) {
////        	System.out.println(file.getAbsolutePath());
//            if (file.toString().startsWith(S_PERIOD)) {
//            	System.out.println("File starts with .");
//                continue;
//            }
//            if (user.equals("NED")) {
//            } else if (user.startsWith("PMR")) {
//            	if(!file.toString().endsWith(".cif")) {
//            		continue;
//            	}
//            }
//
//            String fileName = file.getName();
//            if (user.equals("NED")) {
//            	cifPathList.add(file.getAbsolutePath());
//            } else if (user.startsWith("HEATH")) {
//                cifPathList.add(dir + File.separator + fileName);
//            } else if (
//                    user.startsWith("PMR")) {
//                cifPathList.add(dir + File.separator + fileName);
//            }
//        }
////        System.out.println("Found "+cifPathList.size()+" files.");
//        return cifPathList;
//    }

//    private String getCrystalName(String user, String cifname) {
//        String s = getDir(user);
//        if (user.startsWith("PMR")) {
//            s += File.separator + "xml" + File.separator + cifname + ".cml.xml";
//        } else if (user.equals("NED")) {
//            @SuppressWarnings("unused")
//            int idx = cifname.lastIndexOf(S_UNDER);
//            s += File.separator + cifname
//                    + ".cml.xml";
//        } else if (user.startsWith("HEATH")) {
//            s += File.separator + "xml" + File.separator + cifname + ".cml.xml";
//        }
//        return s;
//    }

    private static String getOutfile(String user, String fragType, 
        String cifname, int mol, int subMol, int serial) {
        String s = getDir(user);
        File dir = new File(s + File.separator + fragType);
        if (!dir.exists()) {
            dir.mkdir();
        }
        if (user.startsWith("PMR") ||
            user.startsWith("HEATH")||
            user.startsWith("NED")) {
            s = dir + File.separator + cifname;
            if (mol > 0) {
                s += S_UNDER + mol;
            }
            if (subMol > 0) {
                s+= S_UNDER + subMol;
            }
            s += S_UNDER + serial + ".cml.xml";
        }
        return s;
    }

    /** runs this as standalone.
     * 
     * @param args
     */
    public static void main(String[] args) {
        String user = (args.length == 0) ? S_EMPTY : args[0];
        CrystalToolTest.analyzeCIFs(user);
    }
    
    /*
    private String calculateCheckcif(String cifPath/*, int timeout) {
		PostMethod filePost = null;
		String checkcif = "";
		try {
			File f = new File(cifPath);
			filePost = new PostMethod(
			"http://dynhost1.iucr.org/cgi-bin/checkcif.pl");
			Part[] parts = { new FilePart("file", f),
					new StringPart("runtype", "fullpublication"),
					new StringPart("UPLOAD", "Send CIF for checking") };
			filePost.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
					new DefaultHttpMethodRetryHandler(5, false));
			filePost.setRequestEntity(new MultipartRequestEntity(parts,
					filePost.getParams()));
			HttpClient client = new HttpClient();
			
			//HttpClientParams hcp = new HttpClientParams();
			//hcp.setSoTimeout(timeout);
			//hcp.setConnectionManagerTimeout(timeout);
			
			//client.setParams(hcp);
			int statusCode = client.executeMethod(filePost);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Could not connect to the IUCr Checkcif service.");
			}
			InputStream in = filePost.getResponseBodyAsStream();
			checkcif = IOUtils.stream2String(in);
		} catch (IOException e) {
			System.err.println("Error calculating checkcif.");
		} finally {
			if (filePost != null)
				filePost.releaseConnection();
		}
		return checkcif;
	}
    */
    
//    private void addSpaceGroup(CMLCml cml) {
//		Nodes hmGroupNodes = cml.query(".//cml:scalar[@dictRef='iucr:_symmetry_space_group_name_H-M']", CML_XPATH);
//		if (hmGroupNodes.size() > 0) {
//			String hmGroup = hmGroupNodes.get(0).getValue();
//			Elements crystals = cml.getChildCMLElements(CMLCrystal.TAG);
//			if (crystals.size() > 0) {
//				CMLCrystal crystal = (CMLCrystal)crystals.get(0);
//				Elements symmetrys = crystal.getChildCMLElements("symmetry");
//				if (symmetrys.size() > 0) {
//					symmetrys.get(0).addAttribute(new Attribute("spaceGroup", hmGroup));
//				}
//			}
//		}
//	}
    
    /**
     * 
     */
    @Test
    public void testAnnotateSpaceGroupMultiplicities() {
    	String moleculeS = "" +
    			"<molecule "+CML_XMLNS+">" +
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
		"<symmetry "+CML_XMLNS+">" +
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
