package org.xmlcml.cml.tools;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.euclid.Util;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class MoleculeLayout extends AbstractTool {
	final static Logger LOG = Logger.getLogger(MoleculeLayout.class);
	
//	private CMLMolecule molecule;
	private MoleculeTool moleculeTool;
	private MoleculeDisplay moleculeDisplay;
	private RingNucleusSet ringNucleusSet;
	private ChainSet chainSet;
	private ConnectionTableTool connectionTable;
	private Map<Sprout, Chain> sproutMap;
	private Map<Sprout, RingNucleus> sproutNucleusMap;
	
	/**
	 */
	public MoleculeLayout() {
	}
	
	/**
	 * @param moleculeTool
	 */
	public MoleculeLayout(MoleculeTool moleculeTool) {
		this.setMoleculeTool(moleculeTool);
	}
	
	/** uses current molecule
	 */
	public void create2DCoordinates() {
		this.create2DCoordinates(this.moleculeTool.getMolecule());
	}
	/**
	 */
	private void create2DCoordinates(CMLMolecule molecule) {
		ensureMoleculeDisplay();
		if (molecule.getMoleculeCount() > 0) {
			for (CMLMolecule subMolecule : molecule.getMoleculeElements()) {
				this.create2DCoordinates(subMolecule);
			}
		} else {
			moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			connectionTable = new ConnectionTableTool(molecule);
			ringNucleusSet = connectionTable.getRingNucleusSet();
			ringNucleusSet.setMoleculeDraw(this);
			ringNucleusSet.setMoleculeLayout(this);
			chainSet = new ChainSet(this);
			sproutMap = new HashMap<Sprout, Chain>();
			sproutNucleusMap = new HashMap<Sprout, RingNucleus>();
			if (ringNucleusSet.size() == 0) {
				chainSet.findOrCreateAndAddChain(new CMLBondSet(moleculeTool.getMolecule()));
				chainSet.layout(this);
			} else {
				// get coordinates for ring nuclei
				Iterator<RingNucleus> nucleusIterator = ringNucleusSet.iterator();
				for (;nucleusIterator.hasNext();) {
					RingNucleus nucleus = nucleusIterator.next();
					nucleus.findSprouts(moleculeDisplay.isOmitHydrogens());
				}
				// find chains starting at sprouts
				nucleusIterator = ringNucleusSet.iterator();
				for (;nucleusIterator.hasNext();) {
					RingNucleus nucleus = nucleusIterator.next();
					for (Sprout sprout : nucleus.getSproutList(moleculeDisplay.isOmitHydrogens())) {
						Chain chain = chainSet.findOrCreateAndAddChain(sprout, ringNucleusSet);
						sproutMap.put(sprout, chain);
						sproutNucleusMap.put(sprout, nucleus);
						chain.addSprout(sprout);
					}
				}
				nucleusIterator = ringNucleusSet.iterator();
				RingNucleus nucleusWithMostRemoteSprouts = null;
				for (;nucleusIterator.hasNext();) {
					RingNucleus nucleus = nucleusIterator.next();
					if (nucleusWithMostRemoteSprouts == null || 
						nucleus.getRemoteSproutList().size() >
					    nucleusWithMostRemoteSprouts.getRemoteSproutList().size()) {
						nucleusWithMostRemoteSprouts = nucleus;
					}
				}
				// now make decisions on what is central to diagram
				nucleusWithMostRemoteSprouts.layout(null);
//				if (1 == 2) {
					tweakOverlappingAtoms();
//				}
			}
			GeometryTool geometryTool = new GeometryTool(moleculeTool.getMolecule());
			geometryTool.addCalculatedCoordinatesForHydrogens(CoordinateType.TWOD, HydrogenControl.USE_EXPLICIT_HYDROGENS);
			molecule.debug("AFTER H");
		}
	}
	
	private void tweakOverlappingAtoms() {
		CMLMolecule molecule = this.moleculeTool.getMolecule();
		double meanBond = moleculeTool.getAverageBondLength(CoordinateType.TWOD);
		LOG.debug("mean bond length is WRONG: "+meanBond);
		List<CMLAtom> atoms1 = molecule.getAtoms();
		List<CMLAtom> atoms2 = molecule.getAtoms();
		for (CMLAtom atom1 : atoms1) {
			for (CMLAtom atom2 : atoms2) {
				double dist = atom1.getDistance2(atom2);
				if (!(atom1.equals(atom2)) && !Double.isNaN(dist) && dist < 0.15 * meanBond) {
					LOG.debug("dist "+atom1.getId()+" -> "+atom2.getId()+": "+dist);
					tweak(atom1, atom2);
				}
			}
		}
	}
	
	private void tweak(CMLAtom atom1, CMLAtom atom2) {
		if (canMove(atom1)) {
			return;
		}
		canMove(atom2);
	}
	
	private boolean canMove(CMLAtom atom) {
		boolean moved = false;
		List<CMLAtom> ligands = atom.getLigandAtoms();
		if (ligands.size() == 1) {
			org.xmlcml.euclid.Real2 vector = atom.getVector2(ligands.get(0));
			vector = vector.multiplyBy(0.3);
			atom.setXY2(atom.getXY2().plus(vector));
			moved = true;
		}
		return moved;
	}
	
	private void ensureMoleculeDisplay() {
		if (moleculeDisplay == null) {
			moleculeDisplay = MoleculeDisplay.getDEFAULT();
		}
	}
	
	/**
	 * @return the chainSet
	 */
	public ChainSet getChainSet() {
		return chainSet;
	}

	/**
	 * @return the connectionTable
	 */
	public ConnectionTableTool getConnectionTable() {
		return connectionTable;
	}

	/**
	 * @param moleculeDisplay
	 */
	public void setDrawParameters(MoleculeDisplay moleculeDisplay) {
		this.moleculeDisplay = moleculeDisplay;
	}
	
	/**
	 * @return the moleculeDisplay
	 */
	public AbstractDisplay getDrawParameters() {
		ensureDrawParameters();
		return moleculeDisplay;
	}
	
	private void ensureDrawParameters() {
		if (moleculeDisplay == null) {
			moleculeDisplay = new MoleculeDisplay();
		}
	}
	 
	/**
	 * @return the ringNucleusSet
	 */
	public RingNucleusSet getRingNucleusSet() {
		return ringNucleusSet;
	}

	/**
	 * @return the sproutMap
	 */
	public Map<Sprout, Chain> getSproutMap() {
		return sproutMap;
	}

	/**
	 * @return the molecule
	 */
	public AbstractTool getMoleculeTool() {
		return moleculeTool;
	}

	/**
	 * @param moleculeTool the moleculeTool to set
	 */
	public void setMoleculeTool(MoleculeTool moleculeTool) {
		this.moleculeTool = moleculeTool;
	}
	private static void usage() {
		Util.println("java "+new MoleculeLayout().getClass().getName()+" [options]" );
		Util.println("... -CML cml    // read cml");
		Util.println("... -SMILES smiles    // read smiles");
		Util.println("... -SVG    svgFile   // write to file");
		Util.println("... -JAVA             // display in Swing Panel");
	}

	/** main
	 * mainly for testing
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			usage();
			System.exit(0);
		}
		int i = 0;
		MoleculeDisplayList displayList = new MoleculeDisplayList();
		CMLMolecule mol = null;
		CMLMoleculeList moleculeList = null;
		MoleculeFrame moleculeFrame = null;
		String smiles = null;
		String cmlfile = null;
		String inline = null;
		while (i < args.length) {
			if ("-SMILES".equals(args[i])) {
				smiles = args[++i]; i++;
			} else if ("-INLINE".equals(args[i])) {
				inline = args[++i]; i++;
			} else if ("-CML".equals(args[i])) {
				cmlfile = args[++i]; i++;
			} else if ("-SVG".equals(args[i])) {
				displayList.setOutfile(args[++i]); i++;
			} else if ("-JAVA".equals(args[i])) {
				moleculeFrame = new MoleculeFrame(); i++;
			} else {
				System.err.println("unknown arg: "+args[i++]);
			}
		}
		if (cmlfile != null) {
			Document doc;
			try {
				doc = new CMLBuilder().build(new FileReader(cmlfile));
				doc = CMLBuilder.ensureCML(doc);
				Nodes nodes = null;
				nodes = doc.query("//*[local-name()='moleculeList']");
				if (nodes.size() == 1) {
					moleculeList = (CMLMoleculeList) nodes.get(0);
				} else {
					nodes = doc.query("//*[local-name()='molecule']");
					if (nodes.size() > 0) {
						mol = (CMLMolecule) nodes.get(0);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (inline != null) {
			InlineTool inlineTool = new InlineTool();
			mol = inlineTool.getMolecule();
		} else if (smiles != null) {
			SMILESTool smilesTool = new SMILESTool();
			smilesTool.parseSMILES(smiles);
			mol = smilesTool.getMolecule();
		}
		MoleculeTool moleculeTool = null;
		if (moleculeList != null) {
			for (CMLMolecule molecule : moleculeList.getMoleculeElements()) {
				moleculeTool = drawMoleculesToDisplayList(displayList, molecule);
				writeDisplayList(displayList, moleculeTool);
			}
//			LOG.debug("WARNING: only first molecule in list drawn");
//			moleculeTool = drawMoleculesToDisplayList(displayList, moleculeList.getMoleculeElements().get(0));
//			writeDisplayList(displayList, moleculeTool);
		} else if (mol != null) {
			moleculeTool = drawMoleculesToDisplayList(displayList, mol);
			writeDisplayList(displayList, moleculeTool);
		}
		if (moleculeFrame != null) {
			moleculeFrame.getMoleculePanel().setDisplayList(displayList);
			moleculeFrame.displayInFrame();
		}
	}

	private static void writeDisplayList(MoleculeDisplayList displayList,
			MoleculeTool moleculeTool) {
		if (displayList.getOutfile() != null && moleculeTool != null) {
			try {
				displayList.write();
				displayList.setAndProcess(moleculeTool);
				displayList.createOrDisplayGraphics();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static MoleculeTool drawMoleculesToDisplayList(
			MoleculeDisplayList displayList, CMLMolecule molecule) {
		MoleculeTool moleculeTool = null;
//		displayList.debugSVG();
		boolean omitHydrogen = true;
		if (molecule != null) {
			moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			if (!molecule.hasCoordinates(CoordinateType.TWOD, omitHydrogen)) {
				MoleculeLayout moleculeLayout = new MoleculeLayout(moleculeTool);
				moleculeLayout.create2DCoordinates(molecule);
				
			}
			try {
				displayList.setAndProcess(moleculeTool);
//				displayList.debugSVG();
				displayList.createOrDisplayGraphics();
//				displayList.debugSVG();
				LOG.debug("=====================================");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return moleculeTool;
	}

	/**
	 * @return the sproutNucleusMap
	 */
	public Map<Sprout, RingNucleus> getSproutNucleusMap() {
		return sproutNucleusMap;
	}

	/**
	 * @return the moleculeDisplay
	 */
	public MoleculeDisplay getMoleculeDisplay() {
		return moleculeDisplay;
	}

	/**
	 * @param moleculeDisplay the moleculeDisplay to set
	 */
	public void setMoleculeDisplay(MoleculeDisplay moleculeDisplay) {
		this.moleculeDisplay = moleculeDisplay;
	}
}

