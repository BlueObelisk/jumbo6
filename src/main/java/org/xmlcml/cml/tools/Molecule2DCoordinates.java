package org.xmlcml.cml.tools;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import nu.xom.Document;
import nu.xom.Nodes;

import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class Molecule2DCoordinates extends AbstractTool {
	final static Logger logger = Logger.getLogger(RingNucleus.class.getName());
	
	private CMLMolecule molecule;
	private Molecule2DParameters drawParameters = new Molecule2DParameters();
	private RingNucleusSet ringNucleusSet;
	private ChainSet chainSet;
	private ConnectionTableTool connectionTable;
	private Map<Sprout, Chain> sproutMap;
	private Map<Sprout, RingNucleus> sproutNucleusMap;
	
	/**
	 */
	public Molecule2DCoordinates() {
	}
	
	/**
	 * @param molecule
	 */
	public Molecule2DCoordinates(CMLMolecule molecule) {
		this.setMolecule(molecule);
	}
	
	/**
	 */
	public void create2DCoordinates() {
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
	public Molecule2DParameters getDrawParameters() {
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
		System.out.println("java "+new Molecule2DCoordinates().getClass().getName()+" [options]" );
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
		GraphicsManager svgObject = new GraphicsManager();
		CMLMolecule mol = null;
		Molecule2DCoordinates molecule2DCoordinates = new Molecule2DCoordinates(mol);
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
				svgObject.setOutfile(args[++i]); i++;
			} else if ("-JAVA".equals(args[i])) {
				moleculeFrame = new MoleculeFrame(molecule2DCoordinates, svgObject); i++;
			} else {
				System.err.println("unknown arg: "+args[i++]);
			}
		}
//		svgFile = null;
//		smiles = null;
		if (cmlfile != null) {
			Document doc;
			try {
				doc = new CMLBuilder().build(new FileReader(cmlfile));
				Nodes nodes = doc.query("//*[local-name()='molecule']");
				mol = (nodes.size() > 0) ? (CMLMolecule) nodes.get(0) : null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (inline != null) {
			System.out.println(inline);
			InlineTool inlineTool = new InlineTool();
			mol = inlineTool.getMolecule();
//			mol.debug();
		} else if (smiles != null) {
			System.out.println(smiles);
			SMILESTool smilesTool = new SMILESTool();
			smilesTool.parseSMILES(smiles);
			mol = smilesTool.getMolecule();
//			mol.debug();
		}
		if (mol != null) {
			molecule2DCoordinates.setMolecule(mol);
			molecule2DCoordinates.create2DCoordinates();
			try {
				svgObject.setMolecule(mol);
				svgObject.createOrDisplayGraphics(MoleculeTool.getOrCreateMoleculeTool(mol), MoleculeDisplay.getDEFAULT());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (svgObject.getOutfile() != null) {
			try {
				svgObject.write();
//				new MoleculeTool(mol).defaultDisplay(svgObject);
				svgObject.createOrDisplayGraphics(MoleculeTool.getOrCreateMoleculeTool(mol), MoleculeDisplay.getDEFAULT());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (moleculeFrame != null) {
			moleculeFrame.getMoleculePanel().setSVGObject(svgObject);
			moleculeFrame.displayInFrame();
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

