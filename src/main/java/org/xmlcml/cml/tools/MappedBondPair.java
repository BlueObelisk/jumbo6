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

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLElectron;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLSpectator;

/**
 * tool to support reactions. not fully developed
 * 
 * @author pmr
 * 
 */
public class MappedBondPair extends AtomBondPair implements CMLConstants {

    static Logger logger = Logger.getLogger(MappedBondPair.class.getName());

    CMLReaction reaction;
    CMLBond bond1;
    CMLBond bond2;
    Element[] parent;
    int iReaction;
    int bondWidth;

    CMLAtom atom10 = null;
    CMLAtom atom11 = null;
    CMLAtom atom20 = null;
    CMLAtom atom21 = null;
    double x10;
    double x11;
    double x20;
    double x21;
    double y10;
    double y11;
    double y20;
    double y21;
    double opacity1;
    double opacity2;
    String order1;
    String order2;
    String begin;
    String dur;
    String stereo1;
    String stereo2;

    Element[] g;
    Element[] line;
    Element[] path;

    boolean animate;
    // actual drawing color for bond
	String drawBondColor = null;
	String drawAromBondColor = null;
    
    CMLElement spectator1 = null;
    
// prod react
    /**
     * @param reaction
     * @param bond1 
     * @param bond2 
     * @param parent
     * @param iReaction
     */
    public MappedBondPair(CMLReaction reaction, CMLBond bond1, CMLBond bond2, 
    		Element[] parent, int iReaction) {
    	this.reaction = reaction;
        this.bond1 = bond1;
        this.bond2 = bond2;

        if (bond1 == null) {
//        	String id20 = bond2.getAtom(0).getId();
//        	String id21 = bond2.getAtom(1).getId();
//        	String ref10 = snap.atomMap.getToRef(id20);
//        	String ref11 = snap.atomMap.getToRef(id21);
//
//        	atom10 = (CMLAtom) snap.getAtom(reaction, ref10, snap.reactantAtomMapMap);
//        	atom11 = (CMLAtom) snap.getAtom(reaction, ref11, snap.reactantAtomMapMap);
        } else {
        	atom10 = bond1.getAtom(0);
        	atom11 = bond1.getAtom(1);
        }
        if (bond2 == null) {
//	    	String id10 = bond1.getAtom(0).getId();
//	    	String id11 = bond1.getAtom(1).getId();
//	    	String ref20 = snap.atomMap.getFromRef(id10);
//	    	String ref21 = snap.atomMap.getFromRef(id11);
//        	atom20 = (CMLAtom) snap.getAtom(reaction, ref20, snap.productAtomMapMap);
//        	atom21 = (CMLAtom) snap.getAtom(reaction, ref21, snap.productAtomMapMap);
        } else {
        	atom20 = bond2.getAtom(0);
        	atom21 = bond2.getAtom(1);
        	// if bond in wrong direction, flip it
        	if (reaction != null && bond1 != null) {
                @SuppressWarnings("unused")
    	    	String id10 = bond1.getAtom(0).getId();
//    	    	String ref20 = snap.atomMap.getFromRef(id10);
//	        	CMLAtom atom200 = (CMLAtom) snap.getAtom(reaction, ref20, snap.productAtomMapMap);
	        	// bond might be pointing in wrong direction
//	        	if (atom200 == null || atom200.getId().equals(atom20.getId())) {
//	        	} else {
//	        		CMLAtom temp = atom21;
//	        		atom21 = atom20;
//	        		atom20 = temp;
//	        	}
        	}
        }
    
//      FIXME THE BOND MAY POINT IN DIFFERENT DIRECTIONS IN r AND p SO HAVE TO CHECK WITH THE REACTION MAP
        
        this.parent = parent;
        this.iReaction = iReaction;

//        begin = "" + snap.elapsedTime + "s";
//        dur   = snap.durS;
//        bondWidth = snap.bondWidth;
        
        // if any atom is null, cannot be a spectator
        spectator1 = (atom10 != null) ? (CMLSpectator) atom10.query("ancestor::cml:spectator", CMLConstants.CML_XPATH).get(0) : null;
//    	drawBondColor = (spectator1 != null) ? snap.spectatorColor : snap.bondColor;
//    	drawAromBondColor = (spectator1 != null) ? snap.spectatorColor : snap.aromBondColor;
//    	drawBondColor = snap.bondColor;
//    	drawAromBondColor = snap.aromBondColor;
//    	fullOpacity = (spectator1 == null) ? 1 : snap.spectatorOpacity;
//    	zeroOpacity = (spectator1 == null) ? 0 : 0;
    	
    }

    /**
     * @param reaction
     * @param bond1
     * @param bond2
     * @param parentx
     * @param iReaction
     */
    public MappedBondPair(CMLReaction reaction, CMLBond bond1, CMLBond bond2, 
    		Element parentx, int iReaction) {
		this(reaction, bond1, bond2, new Element[2], iReaction);
		parent = new Element[2];
		parent[0] = parentx;
    }

    CMLElectron createElectrons(int electronCount, String electronId) {
    	CMLElectron electron = null;
        if (electronCount > 0) {
        	electron = addElectron(bond2, electronCount, electronId);
        } else if (electronCount < 0) {
            electron = addElectron(bond1, electronCount, electronId);
        }
        return electron;
    }

    /**
     */
    public void draw() {
        if (
        	bond1 == null && 
        	bond2 == null) {
            return;
        }
        if (
        	atom10 == null &&
            atom11 == null && 
        	atom20 == null && 
            atom21 == null) {
            return;
        }
//    	if (snap.skipAddedHydrogens) {
    		if (atom10 != null && atom10.getId().indexOf("_h") != -1 ||
    			atom11 != null && atom11.getId().indexOf("_h") != -1 || 
    			atom20 != null && atom20.getId().indexOf("_h") != -1 ||
    			atom21 != null && atom21.getId().indexOf("_h") != -1) {
    			return;
    		}
//    	}
//        x10 = snap.getXCoord(atom10);
//        x11 = snap.getXCoord(atom11);
//        x20 = snap.getXCoord(atom20) + snap.xBoxSeparation;
//        x21 = snap.getXCoord(atom21) + snap.xBoxSeparation;
//        y10 = snap.getYCoord(atom10);
//        y11 = snap.getYCoord(atom11);
//        y20 = snap.getYCoord(atom20);
//        y21 = snap.getYCoord(atom21);

        order1 = (bond1 == null) ? "0" : bond1.getOrder();
        order2 = (bond2 == null) ? "0" : bond2.getOrder();
        if (atom10 == null) {
            x10 = x20;
            y10 = y20;
        }
        if (atom11 == null) {
            x11 = x21;
            y11 = y21;
        }
        if (atom20 == null) {
            x20 = x10;
            y20 = y10;
        }
        if (atom21 == null) {
            x21 = x11;
            y21 = y11;
        }

        stereo1 = getStereo(bond1);
        stereo2 = getStereo(bond2);

//        g = snap.createElement(snap.svgDoc, "g", snap.animate);
//        snap.appendChild(parent, g);

        // if bond doesn't change, only draw once
        if (!stereo1.equals("")) {
            if (stereo1.equals(stereo2)) {
                opacity1 = fullOpacity;
                opacity2 = fullOpacity;
                drawStereo(stereo1);
            } else if (!stereo2.equals("")) {
                opacity1 = fullOpacity;
                opacity2 = zeroOpacity;
                drawStereo(stereo1);
                opacity1 = zeroOpacity;
                opacity2 = fullOpacity;
                drawStereo(stereo2);
            } else {
                opacity1 = fullOpacity;
                opacity2 = zeroOpacity;
                drawStereo(stereo1);
                opacity1 = zeroOpacity;
                opacity2 = fullOpacity;
                draw(order2);
            }
        } else if (!stereo2.equals("")) {
            opacity1 = fullOpacity;
            opacity2 = zeroOpacity;
            draw(order1);
            opacity1 = zeroOpacity;
            opacity2 = fullOpacity;
            drawStereo(stereo2);
        } else if (order1.equals(order2)) {
            if (!order1.equals("0")) {
                opacity1 = fullOpacity;
                opacity2 = fullOpacity;
                draw(order1);
            }
        } else {
            opacity1 = fullOpacity;
            opacity2 = zeroOpacity;
            draw(order1);
            opacity1 = zeroOpacity;
            opacity2 = fullOpacity;
            draw(order2);
        }
    }

    String getStereo(CMLBond bond) {
        String stereo = "";
        if (bond != null) {
            CMLBondStereo bs = bond.getBondStereoElements().get(0);
            if (bs != null) {
                stereo = bs.getStringContent();
                // only take wedge/hatch stereo
                if (!(stereo.equals(CMLBond.HATCH) || stereo.equals(CMLBond.WEDGE))) {
                    stereo = "";
                }
            }
        }
        return stereo;
    }

    void drawStereo(String stereo) {
//        path = snap.createElement(snap.svgDoc, "path", snap.animate);
//        String fillColor = (stereo.equals(CMLBond.WEDGE)) ? drawBondColor : snap.backgroundColor;
//        snap.setAttribute(path, "style", "stroke-width: "+bondWidth+"; fill: "+fillColor+"; stroke : "+snap.bondColor+";");
//
//        snap.appendChild(g, path);
//        animateWedge();
//        snap.makeAnimate(path, snap.svgDoc, "opacity", ""+opacity1, ""+opacity2, begin, dur);
    }

    void animateWedge() {
//        double dx1 = (x11 - x10) * snap.wedgeWidth;
//        double dy1 = (y11 - y10) * snap.wedgeWidth;
//        double dx2 = (x21 - x20) * snap.wedgeWidth;
//        double dy2 = (y21 - y20) * snap.wedgeWidth;
//        String p1 ="M "+snap.format(x10)+" "+snap.format(y10)+" L "+snap.format(x11-dy1-dx1)+" "+snap.format(y11+dx1-dy1)+" "+snap.format(x11+dy1-dx1)+" "+snap.format(y11-dx1-dy1)+" z";
//        String p2 ="M "+snap.format(x20)+" "+snap.format(y20)+" L "+snap.format(x21-dy2-dx2)+" "+snap.format(y21+dx2-dy2)+" "+snap.format(x21+dy2-dx2)+" "+snap.format(y21-dx2-dy2)+" z";
//        snap.makeAnimate(path, snap.svgDoc, "d", p1, p2, begin, dur);
    }

    void draw(String order) {
//    	if (order == null) {
//        } else if (order.equals("0")) {
////			LOG.debug("ORDER ZERO");
////            draw(bondWidth, "pink");
//        } else if (order.equals(CMLBond.SINGLE) || order.equals(CMLBond.SINGLE_S)) {
//            draw(bondWidth, drawBondColor);
//        } else if (order.equals(CMLBond.DOUBLE) || order.equals(CMLBond.DOUBLE_D)) {
//            draw(3 * bondWidth, drawBondColor);
//            draw(bondWidth, snap.backgroundColor);
//        } else if (order.equals(CMLBond.AROMATIC)) {
//            draw(3 * bondWidth, drawAromBondColor);
//            draw(bondWidth, snap.backgroundColor);
//        } else if (order.equals(CMLBond.TRIPLE_T) || order.equals(CMLBond.TRIPLE)) {
//            draw(5 * bondWidth, drawBondColor);
//            draw(3 * bondWidth, snap.backgroundColor);
//            draw(bondWidth, drawBondColor);
//        }
    }

    void draw(int bondWidth, String color) {
//        line = snap.createElement(snap.svgDoc, "line", snap.animate);
//        snap.setAttribute(line, "style", "stroke-width: "+bondWidth+"; fill: "+color+"; stroke : "+color+";");
//        snap.appendChild(g, line);
//        animate();
//        snap.makeAnimate(line, snap.svgDoc, "opacity", ""+opacity1, ""+opacity2, begin, dur);
    }

    void animate() {
//        snap.makeAnimate(line, snap.svgDoc, "x1", snap.format(x10), snap.format(x20), begin, dur);
//        snap.makeAnimate(line, snap.svgDoc, "x2", snap.format(x11), snap.format(x21), begin, dur);
//        snap.makeAnimate(line, snap.svgDoc, "y1", snap.format(y10), snap.format(y20), begin, dur);
//        snap.makeAnimate(line, snap.svgDoc, "y2", snap.format(y11), snap.format(y21), begin, dur);
    }

    /**
     * @param e
     */
    public void setElectronChange(int e) {
        this.electronChange = e;
    }

    /** gets the atompair from the i'th atoms.
     * Thus for the bond pair {a1 a2} {b1 b2}
     * getAtomPair(0) gets atomPair {a1 b1}
     * if either bond is null, returns incomplete atomPairs:
     * Thus getAtomPair(0) on bond pair {null} {b1 b2} returns {null b1}
     * Thus getAtomPair(1) on bond pair {null} {b1 b2} returns {null b2}
     * Thus getAtomPair(0) on bond pair {a1 a2} {null} returns {a1 null}
     * Thus getAtomPair(1) on bond pair {a1 a2} {null} returns {a2 null}
     * @param ii 0 or 1
     * @param atomPairList to choose 
     * @return new atomPair
     */
    public MappedAtomPair getAtomPair(int ii, List<MappedAtomPair> atomPairList) {
    	String atomId1 = (bond1 == null) ? null : bond1.getAtomId(ii);
    	String atomId2 = (bond2 == null) ? null : bond2.getAtomId(ii);
    	MappedAtomPair atomPair = MappedAtomPair.getAtomPair(atomId1, atomId2, atomPairList);
    	return atomPair;
    }
    
    /** tests if bond1 and bond2 contain respective atoms corresponding to atomPair.
     * 
     * @param atomPair
     * @return false if atomPair is null or bondPair does not contain atoms in atomPair
     */
    public boolean containsAtomPair(MappedAtomPair atomPair) {
    	if (atomPair == null) {
    		return false;
    	}
    	String fromId = (bond1 == null) ? null : bond1.getOtherAtomId(atomPair.id1);
    	String toId = (bond2 == null) ? null : bond2.getOtherAtomId(atomPair.id2);
    	return (bond1 != null && fromId != null) || (bond2 != null && toId != null);
    }
    /**
     * @return to string
     */
    public String toString() {
        String s = "BondPair: ";
        s += "["+((bond1 == null) ? "" : bond1.getAtomRefs2())+"]";
        s += " -- ";
        s += "["+((bond2 == null) ? "" : bond2.getAtomRefs2())+"]";
        if (electronChange != 0) {
            s += " change: "+electronChange;
        }
        return s;
    }

}
