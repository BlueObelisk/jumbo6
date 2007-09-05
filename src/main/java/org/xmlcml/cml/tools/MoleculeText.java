package org.xmlcml.cml.tools;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;

/** 
 * a textfiled for direct entry of molecule structure
 * @author pm286
 *
 */
public class MoleculeText extends JPanel implements CMLConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6703519834315672155L;
	private Molecule2DCoordinates molecule2DCoordinates;
	private JTextField jTextField;
	private GraphicsManager svgObject;
	private MoleculeFrame moleculeFrame;
	private String smiles;
	private SMILESTool smilesTool;
	private CMLMolecule molecule;
	private MoleculeTool moleculeTool;
	
	/**
	 * @return the moleculeFrame
	 */
	public MoleculeFrame getMoleculeFrame() {
		return moleculeFrame;
	}

	/**
	 * @param moleculeFrame the moleculeFrame to set
	 */
	public void setMoleculeFrame(MoleculeFrame moleculeFrame) {
		this.moleculeFrame = moleculeFrame;
	}

	/**
	 * @param molecule2DCoordinates
	 * @param svgObject
	 */
	public MoleculeText(Molecule2DCoordinates molecule2DCoordinates, GraphicsManager svgObject) {
		this.setMoleculeDraw(molecule2DCoordinates);
		this.setSVGObject(svgObject);
		init();
	}
	
	void init() {
		jTextField = new JTextField();
		this.setLayout(new BorderLayout());
		this.add(jTextField, BorderLayout.CENTER);
		jTextField.getDocument().addDocumentListener(new InputParser());
		jTextField.addCaretListener(new CaretTracker());
		jTextField.addKeyListener(new MoleculeKeyListenerX());
	}
	
	private class MoleculeKeyListenerX implements KeyListener {
		boolean alt = false;
		boolean shift = false;

		/**
		 * @param arg0
		 */
		public void keyPressed(KeyEvent arg0) {
			int ch = arg0.getKeyCode();
			if (ch == KeyEvent.VK_ALT) {
				alt = true;
			} else if (ch == KeyEvent.VK_SHIFT) {
				shift = true;
			} else if (alt) {
				moleculeFrame.sendAltKey(ch, shift);
			}
		}
		
		/**
		 * @param arg0
		 */
		public void keyReleased(KeyEvent arg0) {
			int ch = arg0.getKeyCode();
			if (ch == KeyEvent.VK_ALT) {
				alt = false;
			} else if (ch == KeyEvent.VK_SHIFT) {
				shift = false;
			}
			if (alt) {
				moleculeFrame.sendAltKey(-1, shift);
			}
		}
		
		/**
		 * @param arg0
		 */
		public void keyTyped(KeyEvent arg0) {
			System.out.println("KeyTyped..x "+arg0);
			if (arg0.getKeyChar() == 16) {
				System.out.println("DOWN-ARROW");
			}
		}
	}

	void processCurrentString() {
		smiles = jTextField.getText();
		smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			molecule = smilesTool.getMolecule();
			moleculeFrame.setMolecule(molecule);
			moleculeTool = MoleculeTool.getOrCreateMoleculeTool(molecule);
			new Molecule2DCoordinates(molecule).create2DCoordinates();
			svgObject.setMolecule(molecule);
			moleculeFrame.getMoleculePanel().repaint();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("bad smiles or layout: "+smiles);
		}
	}

	class CaretTracker implements CaretListener {

		/**
		 * @param e
		 */
		public void caretUpdate(CaretEvent e) {
			int pos = jTextField.getCaretPosition();
			if (smilesTool != null) {
				String atomId = smilesTool.getAtomIdAtChar(pos);
				CMLAtom atom = molecule.getAtomById(atomId);
				if (atom != null) {
//					System.out.println("UPDATE CARET "+pos+" "+atomId);
					SelectionTool selectionTool = moleculeTool.getOrCreateSelectionTool();
					selectionTool.clearAllSelections();
					selectionTool.setSelected(atom, true);
//					System.out.println("SEL "+selectionTool);
					moleculeFrame.repaint();
				}
			}
		}
	}
	
	/**
	 * @return the molecule2DCoordinates
	 */
	public Molecule2DCoordinates getMolecule2DCoordinates() {
		return molecule2DCoordinates;
	}

	/**
	 * @param molecule2DCoordinates the molecule2DCoordinates to set
	 */
	public void setMoleculeDraw(Molecule2DCoordinates molecule2DCoordinates) {
		this.molecule2DCoordinates = molecule2DCoordinates;
	}
	
	class InputParser implements DocumentListener {

		/**
		 * @param arg0
		 */
		public void changedUpdate(DocumentEvent arg0) {
			// TODO Auto-generated method stub
			System.out.println("changedUpdate");
			// seems 
		}

		/**
		 * @param arg0
		 */
		public void insertUpdate(DocumentEvent arg0) {
			processCurrentString();
		}

		/**
		 * @param arg0
		 */
		public void removeUpdate(DocumentEvent arg0) {
			processCurrentString();
		}
	}

	/**
	 * @return caretPosition
	 */
	public int getCaretPosition() {
		return jTextField.getCaretPosition();
	}

	/**
	 * @return the svgObject
	 */
	public GraphicsManager getSVGObject() {
		return svgObject;
	}

	/**
	 * @param svgObject the svgObject to set
	 */
	public void setSVGObject(GraphicsManager svgObject) {
		this.svgObject = svgObject;
	}

}

