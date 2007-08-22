package org.xmlcml.cml.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class MoleculeDraw extends AbstractTool {
	final static Logger logger = Logger.getLogger(RingNucleus.class.getName());
	
	private CMLMolecule molecule;
	private MoleculeDrawParameters drawParameters = new MoleculeDrawParameters();
	private RingNucleusSet ringNucleusSet;
	private ChainSet chainSet;
	private ConnectionTableTool connectionTable;
	private Map<Sprout, Chain> sproutMap;
	private Map<Sprout, RingNucleus> sproutNucleusMap;
	
	/**
	 */
	public MoleculeDraw() {
	}
	
	/**
	 * @param molecule
	 */
	public MoleculeDraw(CMLMolecule molecule) {
		this.setMolecule(molecule);
	}
	
	/**
	 */
	public void layout() {
		if (drawParameters == null) {
			throw new CMLRuntimeException("must set drawParameters");
		}
		connectionTable = new ConnectionTableTool(molecule);
		ringNucleusSet = connectionTable.getRingNucleusSet();
		ringNucleusSet.setMoleculeDraw(this);
		ringNucleusSet.setDrawParameters(drawParameters);
		chainSet = new ChainSet(this);
		sproutMap = new HashMap<Sprout, Chain>();
		sproutNucleusMap = new HashMap<Sprout, RingNucleus>();
		if (ringNucleusSet.size() == 0) {
			chainSet.findOrCreateAndAddChain(new CMLBondSet(molecule));
			chainSet.layout(this);
		} else {
			// get coordinates for ring nuclei
			Iterator<RingNucleus> nucleusIterator = ringNucleusSet.iterator();
			for (;nucleusIterator.hasNext();) {
				RingNucleus nucleus = nucleusIterator.next();
				nucleus.findSprouts(drawParameters.isOmitHydrogens());
			}
			// find chains starting at sprouts
			nucleusIterator = ringNucleusSet.iterator();
			for (;nucleusIterator.hasNext();) {
				RingNucleus nucleus = nucleusIterator.next();
				for (Sprout sprout : nucleus.getSproutList(drawParameters.isOmitHydrogens())) {
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
	 * @return the drawParameters
	 */
	public MoleculeDrawParameters getDrawParameters() {
		return drawParameters;
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
	public CMLMolecule getMolecule() {
		return molecule;
	}

	/**
	 * @param molecule the molecule to set
	 */
	public void setMolecule(CMLMolecule molecule) {
		this.molecule = molecule;
	}
	private static void usage() {
		System.out.println("java "+new MoleculeDraw().getClass().getName()+" [options]" );
		System.out.println("... -SMILES smiles    // read smiles");
		System.out.println("... -SVG    svgFile   // write to file");
		System.out.println("... -JAVA             // display in Swing Panel");
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
		SVGObject svgObject = new SVGObject();
		MoleculePanel moleculePanel = null;
		String smiles = null;
		while (i < args.length) {
			if ("-SMILES".equals(args[i])) {
				smiles = args[++i]; i++;
			} else if ("-SVG".equals(args[i])) {
				svgObject.setOutfile(args[++i]); i++;
			} else if ("-JAVA".equals(args[i])) {
				moleculePanel = new MoleculePanel(); i++;
			} else {
				System.err.println("unknown arg: "+args[i++]);
			}
		}
		CMLMolecule mol = null;
//		svgFile = null;
//		smiles = null;
		if (smiles != null) {
			System.out.println(smiles);
			SMILESTool smilesTool = new SMILESTool();
			smilesTool.parseSMILES(smiles);
			mol = smilesTool.getMolecule();
//			mol.debug();
		}
		if (mol != null) {
			MoleculeDraw moleculeDraw = new MoleculeDraw(mol);
			moleculeDraw.layout();
			try {
				svgObject.setMolecule(mol);
				svgObject.createOrDisplayGraphics(new MoleculeTool(mol), MoleculeDisplay.getDEFAULT());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (svgObject.getOutfile() != null) {
			try {
				svgObject.write();
//				new MoleculeTool(mol).defaultDisplay(svgObject);
				svgObject.createOrDisplayGraphics(new MoleculeTool(mol), MoleculeDisplay.getDEFAULT());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (moleculePanel != null) {
			moleculePanel.setSVGObject(svgObject);
//			new MoleculeTool(mol).defaultDisplay(moleculePanel);
		}
		
	}

	/**
	 * @return the sproutNucleusMap
	 */
	public Map<Sprout, RingNucleus> getSproutNucleusMap() {
		return sproutNucleusMap;
	}
}

