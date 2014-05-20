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

package org.xmlcml.cml.tools;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Document;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Util;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class MoleculeLayout extends AbstractTool {
	final static Logger LOG = Logger.getLogger(MoleculeLayout.class);

//	private static final Angle MINANGLE = new Angle(1.5*Math.PI);
	private static final Angle TARGET_ANGLE = new Angle(Math.PI);

//	private static final Angle MINANGLE2 = new Angle(1.8*Math.PI/2.);
	private static final Angle TARGET_ANGLE2 = new Angle(2*Math.PI/2.);

//	private static final Angle MINANGLE3 = new Angle(2*Math.PI/3.);
	private static final Angle TARGET_ANGLE3 = new Angle(2*Math.PI/3.);

//	private static final Angle MINANGLE4 = new Angle(2*Math.PI/4.);
	private static final Angle TARGET_ANGLE4 = new Angle(2*Math.PI/4.);

	private static int MAX_CYCLES = 50;
	private MoleculeTool moleculeTool;
	private MoleculeDisplay moleculeDisplay;
	private RingNucleusSet ringNucleusSet;
	private ChainSet chainSet;
	private ConnectionTableTool connectionTableTool;
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
		try {
			this.create2DCoordinates(this.moleculeTool.getMolecule());
		} catch (Exception e) {
			LOG.error("Cannot create coordinates "+e);
		}
	}
	/**
	 */
	void create2DCoordinates(CMLMolecule molecule) {
		ensureMoleculeDisplay();
		
		if (molecule.getMoleculeCount() > 0) {
			for (CMLMolecule subMolecule : molecule.getMoleculeElements()) {
				this.create2DCoordinates(subMolecule);
			}
		} else {
			// FIXME debug
			moleculeDisplay.setDisplayGroups(true);
			// make copy so as not to corrupt molecule
			// skip this as the copy is messy
//			if (false && moleculeDisplay.isDisplayGroups()) {
//				molecule = new CMLMolecule(molecule);
//			}
			moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			connectionTableTool = new ConnectionTableTool(molecule);
			if (moleculeDisplay.isDisplayGroups()) {
				connectionTableTool.contractNAlkylGroups();
				List<List<BondTool>> bondToolListList = connectionTableTool.identifyGroupsOnAcyclicBonds();
				ConnectionTableTool.outputGroups(bondToolListList);
				ConnectionTableTool.pruneGroupsAndReLabel(bondToolListList);
			}
			ringNucleusSet = connectionTableTool.getRingNucleusSet();
			ringNucleusSet.setMoleculeDraw(this);
			ringNucleusSet.setMoleculeLayout(this);
//			ringNucleusSet.debug();
			chainSet = new ChainSet(this);
			sproutMap = new HashMap<Sprout, Chain>();
			sproutNucleusMap = new HashMap<Sprout, RingNucleus>();
			int maxCycles = MAX_CYCLES;
			if (ringNucleusSet.size() == 0) {
				chainSet.findOrCreateAndAddChain(new CMLBondSet(moleculeTool.getMolecule()));
				chainSet.layout(this);
			} else {
				getCoordinatesForRingNuclei();
				findChainsStartingAtSprouts();
				RingNucleus nucleusWithMostRemoteSprouts = getNucleusWithMostRemoteSprouts();
				// now make decisions on what is central to diagram
				nucleusWithMostRemoteSprouts.layout(null, maxCycles);
				tweakOverlappingAtoms();
			}
			// not yet active.
			adjustUnusualAnglesInAcyclicNodes();
			GeometryTool geometryTool = new GeometryTool(moleculeTool.getMolecule());
			geometryTool.addCalculatedCoordinatesForHydrogens(CoordinateType.TWOD, HydrogenControl.USE_EXPLICIT_HYDROGENS);
		}
	}

	private void adjustUnusualAnglesInAcyclicNodes() {
		List<CMLAtom> acyclicAtoms = connectionTableTool.getFullyAcyclicAtoms();
		for (CMLAtom acyclicAtom : acyclicAtoms) {
			adjustValenceAnglesOnAcyclicAtom(acyclicAtom);
		}
		adjustAlternationInMethyleneLikeChains(acyclicAtoms);
	}

	private void adjustAlternationInMethyleneLikeChains(List<CMLAtom> acylicAtoms) {
		Set<CMLAtom> acyclicAtomSet = new HashSet<CMLAtom>();
		for (CMLAtom atom : acylicAtoms) {
			acyclicAtomSet.add(atom);
		}
//		List<ChainAtom> chainAtoms = createAcyclicChains(acylicAtoms, acyclicAtomSet);
	}

	private List<ChainAtom> createAcyclicChains(List<CMLAtom> acylicAtoms, Set<CMLAtom> acyclicAtomSet) {
		List<ChainAtom> chainAtomList = new ArrayList<ChainAtom>();
		for (CMLAtom atom : acylicAtoms) {
			ChainAtom chainAtom = ChainAtom.createAtom(atom, acyclicAtomSet);
			if (chainAtom != null) {
				chainAtomList.add(chainAtom);
			}
		}
		return chainAtomList;
	}

	private void adjustValenceAnglesOnAcyclicAtom(CMLAtom atom) {
		AtomTool atomTool = AtomTool.getOrCreateTool(atom);
		List<CMLBond> ligandBonds = atomTool.getNonHydrogenLigandBondList();
		if (ligandBonds.size() == 2) {
			adjustValenceAnglesOnAcyclicAtom2(atom);
		} else if (ligandBonds.size() == 3) {
			adjustValenceAnglesOnAcyclicAtom3(atom);
		} else if (ligandBonds.size() == 4) {
			adjustValenceAnglesOnAcyclicAtom4(atom);
		}
	}

	public static void adjustValenceAnglesOnAcyclicAtom4(CMLAtom atom) {
		if (atom != null) {
			List<CMLBond> bonds = getLigandBondsSortedBySizeOfDownstream4(atom);
			adjustValenceAnglesOnAcyclicAtom(atom, bonds.get(0), bonds.get(1), TARGET_ANGLE4);
			adjustValenceAnglesOnAcyclicAtom(atom, bonds.get(0), bonds.get(2), TARGET_ANGLE2);
			adjustValenceAnglesOnAcyclicAtom(atom, bonds.get(0), bonds.get(3), TARGET_ANGLE4.multiplyBy(-1.0));
		}
	}

	private static List<CMLBond> getLigandBondsSortedBySizeOfDownstream4(CMLAtom atom) {
		List<CMLBond> ligands = AtomTool.getOrCreateTool(atom).getNonHydrogenLigandBondList();
		List<CMLBond> bondsSortedByDownstream = new ArrayList<CMLBond>(4);
		for (int i = 0; i < 4; i++) {
			bondsSortedByDownstream.add(null);
		}
		int serialMaxDownstream = getSerialOfMaxDownstreamAtoms(atom, ligands);
		bondsSortedByDownstream.set(0, ligands.get(serialMaxDownstream));
		ligands.remove(serialMaxDownstream);
		serialMaxDownstream = getSerialOfMaxDownstreamAtoms(atom, ligands);
		bondsSortedByDownstream.set(2, ligands.get(serialMaxDownstream));
		ligands.remove(serialMaxDownstream);
		bondsSortedByDownstream.set(1, ligands.get(0));
		bondsSortedByDownstream.set(3, ligands.get(1));
		return bondsSortedByDownstream;
	}

	private static int getSerialOfMaxDownstreamAtoms(CMLAtom atom,
			List<CMLBond> bonds) {
		int serialMaxDownstream = -99;
		int maxDownstream = -99;
		for (int i = 0; i < bonds.size(); i++) {
			CMLBond bond = bonds.get(i);
			BondTool bondTool = BondTool.getOrCreateTool(bond);
			CMLAtomSet downstreamAtoms = bondTool.getDownstreamAtoms(bond.getOtherAtom(atom));
			if (serialMaxDownstream < 0 || maxDownstream < downstreamAtoms.size()) {
				serialMaxDownstream = i;
				maxDownstream = downstreamAtoms.size();
			}
		}
		return serialMaxDownstream;
	}

	static void adjustValenceAnglesOnAcyclicAtom3(CMLAtom atom) {
		if (atom != null) {
			List<CMLBond> bonds = AtomTool.getOrCreateTool(atom).getNonHydrogenLigandBondList();
			adjustValenceAnglesOnAcyclicAtom(atom, bonds.get(0), bonds.get(1), TARGET_ANGLE3);
			adjustValenceAnglesOnAcyclicAtom(atom, bonds.get(0), bonds.get(2),  TARGET_ANGLE3.multiplyBy(-1.0));
		}
	}

	public static void adjustValenceAnglesOnAcyclicAtom2(CMLAtom atom) {
		List<CMLBond> ligandBonds = AtomTool.getOrCreateTool(atom).getNonHydrogenLigandBondList();
		adjustValenceAnglesOnAcyclicAtom(atom, ligandBonds.get(0), ligandBonds.get(1), TARGET_ANGLE2);
	}

	private static void adjustValenceAnglesOnAcyclicAtom(CMLAtom atom, CMLBond staticBond, CMLBond movingBond,
			Angle targetAngle) {
		Angle angle = MoleculeTool.getCalculatedAngle2D(
				staticBond.getOtherAtom(atom), atom, movingBond.getOtherAtom(atom));
		if (angle != null) {
			Angle delta = angle.subtract(targetAngle);
			BondTool movingBondTool = BondTool.getOrCreateTool(movingBond);
			CMLAtomSet atomSet = movingBondTool.getDownstreamAtoms(atom);
			Transform2 t2 = Transform2.getRotationAboutPoint(delta, atom.getXY2());
			atomSet.transform(t2);
		}
	}


	private RingNucleus getNucleusWithMostRemoteSprouts() {
		Iterator<RingNucleus> nucleusIterator = ringNucleusSet.iterator();
		RingNucleus nucleusWithMostRemoteSprouts = null;
		for (;nucleusIterator.hasNext();) {
			RingNucleus nucleus = nucleusIterator.next();
			if (nucleusWithMostRemoteSprouts == null || 
				nucleus.getRemoteSproutList().size() >
			    nucleusWithMostRemoteSprouts.getRemoteSproutList().size()) {
				nucleusWithMostRemoteSprouts = nucleus;
			}
		}
		return nucleusWithMostRemoteSprouts;
	}

	private void getCoordinatesForRingNuclei() {
		Iterator<RingNucleus> nucleusIterator = ringNucleusSet.iterator();
		for (;nucleusIterator.hasNext();) {
			RingNucleus nucleus = nucleusIterator.next();
			nucleus.findSprouts(moleculeDisplay.isOmitHydrogens());
		}
	}

	private void findChainsStartingAtSprouts() {
		Iterator<RingNucleus> nucleusIterator;
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
	}
	
	private void tweakOverlappingAtoms() {
		CMLMolecule molecule = this.moleculeTool.getMolecule();
		double meanBond = moleculeTool.getAverageBondLength(CoordinateType.TWOD);
		List<CMLAtom> atoms1 = molecule.getAtoms();
		List<CMLAtom> atoms2 = molecule.getAtoms();
		for (CMLAtom atom1 : atoms1) {
			for (CMLAtom atom2 : atoms2) {
				double dist = atom1.getDistance2(atom2);
				if (!(atom1.equals(atom2)) && !Double.isNaN(dist) && dist < 0.15 * meanBond) {
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
		return connectionTableTool;
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
	public AbstractDisplay getAbstractDisplay() {
		ensureAbstractDisplay();
		return moleculeDisplay;
	}
	
	private void ensureAbstractDisplay() {
		if (moleculeDisplay == null) {
			moleculeDisplay = new MoleculeDisplay(MoleculeDisplay.getDEFAULT());
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
				LOG.error("unknown arg: "+args[i++]);
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
//		boolean omitHydrogen = true;
		boolean omitHydrogen = false;
		if (molecule != null) {
			moleculeTool = MoleculeTool.getOrCreateTool(molecule);
			if (!molecule.hasCoordinates(CoordinateType.TWOD, omitHydrogen)) {
				MoleculeLayout moleculeLayout = new MoleculeLayout(moleculeTool);
				moleculeLayout.create2DCoordinates(molecule);
				
			}
			try {
				displayList.setAndProcess(moleculeTool);
				displayList.createOrDisplayGraphics();
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

