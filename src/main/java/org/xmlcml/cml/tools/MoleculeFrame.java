package org.xmlcml.cml.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLElement.CoordinateType;

/**
 * frame contains panel and text
 * @author pm286
 *
 */
public class MoleculeFrame extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5780889635256810687L;
	private MoleculeText moleculeText;
	private MoleculePanel moleculePanel;
	private MoleculeTool moleculeTool;

	/**
	 */
	public MoleculeFrame() {
		this.setLayout(new BorderLayout());
		this.moleculePanel = new MoleculePanel(this);
		this.add(moleculePanel, BorderLayout.CENTER);
		this.moleculeText = new MoleculeText();
		moleculeText.setMoleculeFrame(this);
		moleculePanel.setMoleculeFrame(this);
		this.add(moleculeText, BorderLayout.SOUTH);
		this.addKeyListener(new MoleculeKeyListenerX());
	}
	
	/**
	 */
	public void repaint() {
		if (moleculePanel != null) {
			moleculePanel.repaint();
		}
		super.repaint();
	}
	
	// I think these work
	private class MoleculeKeyListenerX implements KeyListener {

		/**
		 * @param arg0
		 */
		public void keyPressed(KeyEvent arg0) {
//			System.out.println("KeyPress...x "+arg0);
		}
		
		/**
		 * @param arg0
		 */
		public void keyReleased(KeyEvent arg0) {
//			System.out.println("KeyReleased...x "+arg0);
		}
		
		/**
		 * @param arg0
		 */
		public void keyTyped(KeyEvent arg0) {
//			System.out.println("KeyTyped..x "+arg0);
		}
	}
	
	/**
	 * 
	 */
	public void displayInFrame() {
		JFrame jFrame = new JFrame();
		jFrame.getContentPane().add(this);
		jFrame.setSize(new Dimension(600, 700));
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * @return the moleculePanel
	 */
	public MoleculePanel getMoleculePanel() {
		return moleculePanel;
	}
	

	/**
	 * @param moleculePanel the moleculePanel to set
	 */
	public void setMoleculePanel(MoleculePanel moleculePanel) {
		this.moleculePanel = moleculePanel;
	}

	/**
	 * @return the moleculeText
	 */
	public MoleculeText getMoleculeText() {
		return moleculeText;
	}

	/**
	 * @param moleculeText the moleculeText to set
	 */
	public void setMoleculeText(MoleculeText moleculeText) {
		this.moleculeText = moleculeText;
	}

	/**
	 * @return the moleculeTool
	 */
	public MoleculeTool getMoleculeTool() {
		return moleculeTool;
	}

	/**
	 * @param moleculeTool the moleculeTool to set
	 */
	public void setMoleculeTool(MoleculeTool moleculeTool) {
		if (moleculeTool != null) {
			this.moleculeTool = moleculeTool;
			MoleculeDisplay moleculeDisplay = moleculeTool.getMoleculeDisplay();
			moleculePanel.ensureDisplayList();
			moleculePanel.getDisplayList().setAndProcess(moleculeTool);
			// bump check
			List<AtomPair> bumpList = moleculeTool.getBumps(CoordinateType.TWOD, 
					moleculeDisplay.getBondLength() * 0.1);
			for (AtomPair atomPair : bumpList) {
				System.out.println("bump "+atomPair.getAtom1().getId()+" - "+atomPair.getAtom2().getId()+": .. "+atomPair.getDistance2());
			}
	
			try {
				moleculePanel.getDisplayList().createOrDisplayGraphics();
			} catch (IOException ioe) {
				throw new CMLRuntimeException("bug "+ioe, ioe);
			}
		}
	}
}
