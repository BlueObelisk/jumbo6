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
import org.xmlcml.cml.element.CMLBondSet;
import org.xmlcml.cml.element.CMLMolecule;

/**
 * tool to support a ring. not fully developed
 * 
 * @author pmr
 * 
 */
public class MoleculeLayout extends AbstractTool {
	final static Logger logger = Logger.getLogger(RingNucleus.class.getName());
	
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
	
	/**
	 */
	public void create2DCoordinates() {
		ensureMoleculeDisplay();
		connectionTable = new ConnectionTableTool(moleculeTool.getMolecule());
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
		}
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
	public MoleculeDisplay getDrawParameters() {
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
		System.out.println("java "+new MoleculeLayout().getClass().getName()+" [options]" );
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
		MoleculeDisplayList displayList = new MoleculeDisplayList();
		CMLMolecule mol = null;
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
				Nodes nodes = doc.query("//*[local-name()='molecule']");
				mol = (nodes.size() > 0) ? (CMLMolecule) nodes.get(0) : null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (inline != null) {
//			System.out.println(inline);
			InlineTool inlineTool = new InlineTool();
			mol = inlineTool.getMolecule();
		} else if (smiles != null) {
			SMILESTool smilesTool = new SMILESTool();
			smilesTool.parseSMILES(smiles);
			mol = smilesTool.getMolecule();
		}
		MoleculeTool moleculeTool = null;
		if (mol != null) {
			moleculeTool = MoleculeTool.getOrCreateTool(mol);
			MoleculeLayout moleculeLayout = new MoleculeLayout(moleculeTool);
			moleculeLayout.create2DCoordinates();
			try {
				displayList.setAndProcess(moleculeTool);
				displayList.createOrDisplayGraphics();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (displayList.getOutfile() != null && moleculeTool != null) {
			try {
				displayList.write();
				displayList.setAndProcess(moleculeTool);
				displayList.createOrDisplayGraphics();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (moleculeFrame != null) {
			moleculeFrame.getMoleculePanel().setDisplayList(displayList);
			moleculeFrame.displayInFrame();
		}
		
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

