package org.xmlcml.cml.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.xmlcml.cml.element.CMLMolecule;

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
	private CMLMolecule molecule;
	private Molecule2DCoordinates moleculeDraw;
	private GraphicsManager svgObject;
	
	/**
	 * @param moleculeDraw
	 * @param svgObject
	 */
	public MoleculeFrame(Molecule2DCoordinates moleculeDraw, GraphicsManager svgObject) {
		this.setLayout(new BorderLayout());
		this.moleculePanel = new MoleculePanel();
		moleculePanel.setMoleculeFrame(this);
		this.add(moleculePanel, BorderLayout.CENTER);
		this.moleculeText = new MoleculeText(moleculeDraw, svgObject);
		moleculeText.setMoleculeFrame(this);
		moleculePanel.setMoleculeFrame(this);
// debug		
		this.add(moleculeText, BorderLayout.SOUTH);
		this.moleculeDraw = moleculeDraw;
		this.setSVGObject(svgObject);
		this.addKeyListener(new MoleculeKeyListenerX());
	}
	
	private class MoleculeKeyListenerX implements KeyListener {

		/**
		 * @param arg0
		 */
		public void keyPressed(KeyEvent arg0) {
			System.out.println("KeyPress...x "+arg0);
		}
		
		/**
		 * @param arg0
		 */
		public void keyReleased(KeyEvent arg0) {
			System.out.println("KeyReleased...x "+arg0);
		}
		
		/**
		 * @param arg0
		 */
		public void keyTyped(KeyEvent arg0) {
			System.out.println("KeyTyped..x "+arg0);
		}
	}
	
	int x = 0;
	int y = 0;
	void sendAltKey(int ch, boolean shift) {
		if (ch < 0) {
			x = 0;
			y = 0;
		} else if (ch == KeyEvent.VK_LEFT) {
			x -= 1;
		} else if (ch == KeyEvent.VK_RIGHT) {
			x += 1;
		} else if (ch == KeyEvent.VK_UP) {
			y -= 1;
		} else if (ch == KeyEvent.VK_DOWN) {
			y += 1;
		}
		if (x != 0 || y != 0) {
			moleculePanel.shift(x, y);
			System.out.println((""+x+"/"+y));
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
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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

}
