package org.xmlcml.cml.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.lite.CMLAtom;
import org.xmlcml.cml.element.lite.CMLBond;
import org.xmlcml.cml.element.lite.CMLMolecule;

/** 
 * a textfield for direct entry of molecule structure
 * @author pm286
 *
 */
public class MoleculeText extends JPanel implements CMLConstants {

	/**
	 */
	private static final long serialVersionUID = 6703519834315672155L;
	private JTextField jTextField;
	private MoleculeFrame moleculeFrame;
	private MoleculeTool smilesMoleculeTool;	// persisent for building only
	private SMILESTool smilesTool;				// persistent for editing
	private int caretPosition;
	
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
		init();
	}

	void init() {
		jTextField = new JTextField();
		jTextField.setHighlighter(new AtomBondHighlighter());
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
			} 
			if (alt) {
				moleculeFrame.getMoleculePanel().sendAltKey(ch, shift);
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
				moleculeFrame.getMoleculePanel().sendAltKey(-1, shift);
			}
//			System.out.println("REL "+(char)ch+"/"+(int)ch+"/"+alt+"/"+shift);
		}
		
		/**
		 * @param arg0
		 */
		public void keyTyped(KeyEvent arg0) {
//			int ch = arg0.getKeyCode();
//			System.out.println("TY "+(char)ch+"/"+(int)ch+"/"+alt+"/"+shift);
		}
	}

	void processCurrentString() {
		String smiles = jTextField.getText();
		smilesTool = new SMILESTool();
		try {
			smilesTool.parseSMILES(smiles);
			CMLMolecule molecule = smilesTool.getMolecule();
			smilesMoleculeTool = MoleculeTool.getOrCreateTool(molecule);
			new MoleculeLayout(smilesMoleculeTool).create2DCoordinates();
			moleculeFrame.setMoleculeTool(smilesMoleculeTool);
			moleculeFrame.repaint();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("bad smiles or layout: "+smiles);
		}
	}
	
	void insertSubstituent(String s) {
		String text = jTextField.getText();
		text = SMILESTool.normalizeRings(text);
		text = text.substring(0, caretPosition)+s+text.substring(caretPosition);
		text = SMILESTool.normalizeRings(text);
		jTextField.setText(text);
	}

	void insertFused(String bond, String ringAtoms, int ring) {
		if (caretPosition >= 1) {
			String text = jTextField.getText();
			text = SMILESTool.normalizeRings(text);
			text = text.substring(0, caretPosition-1)+
			    S_LBRAK+
			    ringAtoms+
				ring+
			    S_RBRAK+
			    bond+
			    text.substring(caretPosition, caretPosition+1)+
			    ring+
			    text.substring(caretPosition+1);
			text = SMILESTool.normalizeRings(text);
			jTextField.setText(text);
		}
	}

	class CaretTracker implements CaretListener {

		/**
		 * @param e
		 */
		public void caretUpdate(CaretEvent e) {
			caretPosition = jTextField.getCaretPosition();
			if (smilesTool != null) {
				String atomId = smilesTool.getAtomIdAtChar(caretPosition);
				if (atomId != null) {
					CMLAtom currentAtom = smilesMoleculeTool.getMolecule().getAtomById(atomId);
					smilesMoleculeTool.setCurrentAtom(currentAtom);
					CMLBond currentBond = smilesMoleculeTool.resetCurrentBond();
					if (currentAtom != null) {
						SelectionTool selectionTool = smilesMoleculeTool.getOrCreateSelectionTool();
						selectionTool.clearAllSelections();
						selectionTool.setSelected(currentAtom, true);
						if (currentBond != null) {
							selectionTool.setSelected(currentBond, true);
							System.out.println("SELBOND "+currentBond.getId());
						}
						moleculeFrame.repaint();
					}
					String atomChunk = smilesTool.getAtomChunkAtChar(caretPosition);
					AtomBondHighlighter atomHighlighter = (AtomBondHighlighter) jTextField.getHighlighter();
					try {
						atomHighlighter.removeAllHighlights();
						atomHighlighter.addHighlight(caretPosition, 
							caretPosition+atomChunk.length(), new AtomHighlightPainter());
					} catch (Exception ee) {
						throw new RuntimeException("HIGHLIGHT "+ee);
					}
				}
			}
		}
	}
	
	class InputParser implements DocumentListener {

		/**
		 * @param arg0
		 */
		public void changedUpdate(DocumentEvent arg0) {
			processCurrentString();
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
	 * @return the jTextField
	 */
	public JTextField getJTextField() {
		return jTextField;
	}

}

class AtomBondHighlighter extends DefaultHighlighter {
	/**
	 */
	public AtomBondHighlighter() {
		super();
	}
	/**
	 * @param arg0 start
	 * @param arg1 end
	 * @param arg2 painter (e.g. for colour)
	 * @return object = javax.swing.text.DefaultHighlighter$LayeredHighlightInfo ???
	 * @throws BadLocationException
	 */
	public Object addHighlight(int arg0, int arg1, HighlightPainter arg2) throws BadLocationException {
		return super.addHighlight(arg0, arg1, arg2);
	}
//	public void changeHighlight(Object arg0, int arg1, int arg2) throws BadLocationException {
//	}
//	public void deinstall(JTextComponent arg0) {
//	}
//	public Highlight[] getHighlights() {
//		return null;
//	}
//	public void install(JTextComponent arg0) {
//	}
//	public void paint(Graphics arg0) {
//	}
//	public void removeAllHighlights() {
//	}
//	public void removeHighlight(Object arg0) {
//	}
//}
}
class AtomHighlightPainter extends DefaultHighlightPainter {
	/**
	 */
	public AtomHighlightPainter() {
		this(Color.PINK);
	}
	/**
	 * @param color
	 */
	public AtomHighlightPainter(Color color) {
		super(color);
	}
}

class BondHighlightPainter extends DefaultHighlightPainter {
	/**
	 */
	public BondHighlightPainter() {
		this(Color.CYAN);
	}
	/**
	 * @param color
	 */
	public BondHighlightPainter(Color color) {
		super(color);
	}
}

