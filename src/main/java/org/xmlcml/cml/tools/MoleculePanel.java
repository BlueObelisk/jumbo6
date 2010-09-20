package org.xmlcml.cml.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.graphics.GraphicsElement;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;

/**
 * @author pm286
 *
 */
public class MoleculePanel extends JPanel implements /* CMLDrawable, */CMLConstants {
	private static Logger LOG = Logger.getLogger(MoleculePanel.class);
	
	/**
	 */
	private static final long serialVersionUID = -8163558817500143947L;
	private static Map<String, String> groupMap;
	static {
		groupMap = new HashMap<String, String>();
		groupMap.put("A", "(C9=CC=CC=C9)");     // aromatic
		groupMap.put("B", "(CCCC)");            // butyl
		groupMap.put("C", "(CC(=O)O)");         // carboxy
		groupMap.put("E", "(CC)");              // ethyl
		groupMap.put("F", "(C(=O))");           // formyl
		groupMap.put("M", "(C)");               // methyl
		groupMap.put("N", "(N)");               // amiNe
		groupMap.put("P", "(CCC)");             // propyl
		groupMap.put("V", "(=O)");              // carbonyl
		groupMap.put("Y", "(C(=O)C)");          // acetyl
		groupMap.put("Z", "(C(C9=CC=CC=C9))");  // benZyl
	};
	

	private MoleculeFrame moleculeFrame;
	private MoleculeText moleculeText;
	private MoleculeDisplayList displayList;
	
	// keep track of movements
	private Transform2 move = new Transform2(); 
	private int lastX = 0;
	private int lastY = 0;
	private double scale = 1.0;
	private double angle = 0.0;
	int x = 0;
	int y = 0;
	
	
	/**
	 */	
	private MoleculePanel() {
		this.addMouseMotionListener(new MoleculeMouseMotionListener());
		this.addMouseListener(new MoleculeMouseListener());
	}

	/**
	 * @param moleculeFrame
	 */
	public MoleculePanel(MoleculeFrame moleculeFrame) {
		this();
		this.moleculeFrame = moleculeFrame;
	}
	
	void ensureDisplayList() {
		if (displayList == null) {
			displayList = new MoleculeDisplayList();
		}
	}
	
	class MoleculeMouseMotionListener implements MouseMotionListener {

		/**
		 * @param arg0
		 */
		public void mouseDragged(MouseEvent arg0) {
			LOG.debug("DRAGPanel "+arg0);
		}

		/**
		 * @param arg0
		 */
		public void mouseMoved(MouseEvent arg0) {
//			LOG.debug("MOVE "+arg0);
		}
	}
	
	class MoleculeMouseListener implements MouseListener {

		/**
		 * @param e
		 */
		public void mouseClicked(MouseEvent e) {
			LOG.debug("CLICK "+e);
		}

		/**
		 * @param e
		 */
		public void mouseEntered(MouseEvent e) {
//			LOG.debug("ENTER "+e);
		}

		/**
		 * @param e
		 */
		public void mouseExited(MouseEvent e) {
//			LOG.debug("EXIT "+e);
		}

		/**
		 * @param e
		 */
		public void mousePressed(MouseEvent e) {
			LOG.debug("PRESS "+e);
		}

		/**
		 * @param e
		 */
		public void mouseReleased(MouseEvent e) {
			LOG.debug("RELEASE "+e);
		}
	}
	

//	<?xml version="1.0" encoding="UTF-8"?>
//	<svg xmlns="http://www.w3.org/2000/svg">
//	  <g transform="matrix(16.146254327058795,0., 0.,-16.146254327058795,72.98344340225616,72.98344340225616)"
//	        font-style="normal" font-weight="normal" font-family="helvetica" 
//		    font-size="1.0" fill="black" opacity="1.0">
//	    <g style="stroke-width:0.2;">
//	      <line x1="4.817664105611036" y1="-0.2465136668648764" x2="7.134575966969313" y2="1.3758054838256792"
//          stroke="black" style="stroke-width:0.12;"/>
//	    </g>
//	    <g transform="matrix(1.0,0., 0.,-1.0,4.817664105611036,-0.2465136668648764)" fill="black">
//	      <circle cx="0.0" cy="0.0" r="0.4" fill="white"/>
//	      <text stroke="none" x="-0.34" y="0.35">U</text>
//	    </g>
//	  </g>
//	</svg>
	
	/**
	 * @param ggg
	 */
	public void paintComponent(Graphics ggg) {
// Clear off-screen bitmap
		setBackground(Color.WHITE);
		super.paintComponent(ggg);
  // Cast Graphics to Graphics2D
		Graphics2D g2d = (Graphics2D)ggg;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.drawMolecule(g2d);
		this.calculateAndDrawProperties(g2d);
	}
	

	private static String[] rings = {
		"",
		"",
		"",
		"C9CC9",
		"C9CCC9",
		"C9CCCC9",
		"C9CCCCC9",
		"C9CCCCCC9",
		"C9CCCCCCC9",
		"C9CCCCCCCC9",
	};
	private static String[] fused = {
		"",
		"",
		"",
		"-C",
		"-CC",
		"-CCC",
		"-CCCC",
		"-CCCCC",
		"-CCCCCC",
		"-CCCCCCC",
	};
	
	void sendAltKey(int ch, boolean shift) {
		MoleculeDisplayList displayList = this.getDisplayList();
		MoleculeDisplay moleculeDisplay = displayList.getMoleculeDisplay();
		char c = (char) ch;
		boolean arrow = false;
		if (ch == KeyEvent.VK_ALT) {
			// no other key (or shift)
		} else if (ch == KeyEvent.VK_SHIFT) {
			// no other key (or alt)
		} else if (ch < 0) {
			x = 0;
			y = 0;
		} else if (c == '1') {
			// atom labels
			AtomDisplay atomDisplay = moleculeDisplay.getDefaultAtomDisplay();
			atomDisplay.setDisplayLabels(!atomDisplay.isDisplayLabels());
			LOG.debug("LABEL: "+!atomDisplay.isDisplayLabels());
			displayList.setAndProcess(moleculeFrame.getMoleculeTool());
			this.repaint();
		} else if (c == '2') {
			// omit hydrogens
			AtomDisplay atomDisplay = moleculeDisplay.getDefaultAtomDisplay();
			atomDisplay.setOmitHydrogens(!atomDisplay.isOmitHydrogens());
			displayList.setAndProcess(moleculeFrame.getMoleculeTool());
			this.repaint();
			// rings
		} else if (Character.isDigit(ch) || Character.isLetter(ch)) {
			this.altCharacter(ch, shift);
		} else if (ch == KeyEvent.VK_LEFT) {
			x -= 1;
			arrow = true;
		} else if (ch == KeyEvent.VK_RIGHT) {
			x += 1;
			arrow = true;
		} else if (ch == KeyEvent.VK_UP) {
			y -= 1;
			arrow = true;
		} else if (ch == KeyEvent.VK_DOWN) {
			y += 1;
			arrow = true;
		} else {
			LOG.debug("CHHHH "+ch+"/"+KeyEvent.VK_ALT);
			this.altCharacter(ch, shift);
		}
		if (arrow) {
			if (x != 0 || y != 0) {
				if (!shift) {
					this.shift(x, y);
				} else {
					this.rotScale(x,y);
				}
			}
		}
	}
	
	
	void altCharacter(int ch, boolean shift) {
		ensureMoleculeDisplay();
		ensureMoleculeText();
		char c = (char) ch;
		int ringSize = c - '0';
		String s = "";
		// ALT only
		if (!shift) {
			if (c == '0') {
				// unused
			} else if (c > '2' && c <= '9') {
				s = CMLConstants.S_LBRAK+rings[ringSize]+S_RBRAK;
				// groups
			} else if (c == '\\') {
				CMLBond currentBond = moleculeFrame.getMoleculeTool().incrementCurrentBond();
				currentBond.debug("BB");
			} else if (Character.isUpperCase(c)) {
				s = groupMap.get(""+c);
			} else {
				LOG.debug("ALT "+(int)c);
			}
			// substituent rings and groups
			if (s != null && !"".equals(s)) {
				moleculeText.insertSubstituent(s);
			}
			// ALT+SHIFT
		} else {
			if (c > '2' && c <= '9') {
				s = fused[ringSize];
			} else if (c == 'A') {
				s = "=C=CC=C";
			} else {
				LOG.debug("ALT SHIFT "+(int)c);
			}
			// fused rings
			if (!"".equals(s)) {
				moleculeText.insertFused(s.substring(0, 1), s.substring(1), 9);
			}
		}
	}
	
	void ensureMoleculeDisplay() {
		ensureDisplayList();
		MoleculeDisplay moleculeDisplay = displayList.getMoleculeDisplay();
		if (moleculeDisplay == null) {
			if (moleculeFrame.getMoleculeTool() == null) {
				throw new RuntimeException("null molecule Tool");
			}
			moleculeDisplay = moleculeFrame.getMoleculeTool().getMoleculeDisplay();
			displayList.setMoleculeDisplay(moleculeDisplay);
		}
	}
	
	void ensureMoleculeText() {
		if (moleculeText == null) {
			moleculeText = moleculeFrame.getMoleculeText();
		}
	}
	
	void rotScale(int x, int y) {
		if (x == lastX) {
			scale(y);
		} else if (y == lastY) {
			rotate(x);
		}
		lastX = x;
		lastY = y;
	}
	
	void rotate(int x) {
		if (x < lastX) {
			angle = -0.005;
		} else if (x > lastX) {
			angle = 0.005;
		} else {
		}
		double cosa = Math.cos(angle);
		double sina = Math.sin(angle);
		Transform2 t2 = new Transform2(new double[] {
			cosa, sina, 0.0,
			-sina, cosa, 0.0,
			0.0, 0.0, 1.0,
		});
		move = t2.concatenate(move);
		this.repaint();
	}

	void scale(int y) {
		if (y > lastY) {
			scale = 0.99;
		} else if (y < lastY) {
			scale = 1.01;
		} else {
			scale = 1.0;
		}
		Transform2 t2 = new Transform2(new double[] {
			scale, 0.0, 0.0,
			0.0, scale, 0.0,
			0.0, 0.0, 1.0,
		});
		move = t2.concatenate(move);
		lastY = y;
		this.repaint();
	}

	void shift(int x, int y) {
		Real2 xy = new Real2((double) x, (double) y);
		Transform2 t2 = new Transform2(new double[] {
			1.0, 0.0, xy.getX(),
			0.0, 1.0, xy.getY(),
			0.0, 0.0, 1.0,
		});
		move = t2.concatenate(move);
		this.repaint();
	}

	/** draws molecule
	 * @param g2d 
	 */
	public void drawMolecule(Graphics2D g2d) {
		if (displayList != null) {
			SVGG svg = displayList.getSvg();
			if (svg != null) {
				svg.clearCumulativeTransformRecursively();
				svg.setAttributeFromTransform2(move);
				svg.setCumulativeTransformRecursively();
				svg.draw(g2d);
			}
		}
		Stroke s = new BasicStroke(1.5f);
		g2d.setStroke(s);
	}
	
	/**
	 * 
	 * @param g2d
	 */
	public void calculateAndDrawProperties(Graphics2D g2d) {
		MoleculeTool moleculeTool = moleculeFrame.getMoleculeTool();
		if (moleculeTool != null) {
			CMLFormula formula = moleculeTool.getCalculatedFormula(CMLMolecule.HydrogenControl.USE_EXPLICIT_HYDROGENS);
			String f = formula.getConcise();
			if (f != null) {
				g2d.setColor(Color.BLACK);
				Font oldFont = g2d.getFont();
				g2d.setFont(new Font("helvetica", Font.BOLD, 12));
				g2d.drawString(f, 10, 10);
				double d = formula.getCalculatedMolecularMass();
				g2d.drawString(("MWt: "+d).substring(0, 10), 300, 10);
				g2d.setFont(oldFont);
			}
		}
	}
	
	/**
	 * @param displayList
	 */
	public void setDisplayList(MoleculeDisplayList displayList) {
		this.displayList = displayList;
	}
	
	/**
	 * @return element
	 */
	public SVGElement createGraphicsElement() {
		return new SVGG();
	}

	/**
	 * @throws IOException
	 */
	public void createOrDisplayGraphics()throws IOException {
		if (moleculeFrame.getMoleculeTool() == null) {
			throw new RuntimeException("null molecule Tool");
		}
		// FIXME
//	    /*g = */moleculeFrame.getMoleculeTool().createSVG(this);
	}

	/** dummy
	 * @param g
	 * @throws IOException
	 */
	public  void output(GraphicsElement g) throws IOException {
		
	}

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
	 * @return the displayList
	 */
	public MoleculeDisplayList getDisplayList() {
		return displayList;
	}

}
