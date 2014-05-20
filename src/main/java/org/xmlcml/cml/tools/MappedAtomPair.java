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
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLElectron;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLSpectator;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * tool to support reactions. not fully developed
 * 
 * @author pmr
 * 
 */
public class MappedAtomPair extends AtomBondPair {

    static Logger LOG = Logger.getLogger(MappedAtomPair.class);

    CMLAtom atom1;
    CMLAtom atom2;
    String id1;
    String id2;
//    String id; // same for both atoms ! NO!
    Element[] parent;
    int iCurrentReaction;
    Element[] g;
    double opacity1;
    double opacity2;
    double x1;
    double y1;
    double x2;
    double y2;
    double xoff;
    double yoff;
    double xcoff;
    double ycoff;
    double chgrad;
    boolean unmapped;

    String begin;
    String dur;

    String elemColor;

    /**
     * @param atom1
     * @param atom2
     */
    public MappedAtomPair(CMLAtom atom1, CMLAtom atom2) {
        this.atom1 = atom1;
        this.atom2 = atom2;


        id1 = (atom1 == null) ? null : atom1.getId();
        id2 = (atom2 == null) ? null : atom2.getId();
        
        spectator1 = (atom1 != null) ? (CMLSpectator) atom1.query("ancestor::*[local-name()='spectator']").get(0): null;
//    	fullOpacity = (spectator1 == null) ? 1 : snap.spectatorOpacity;
//    	zeroOpacity = (spectator1 == null) ? 0 : 0;
    	
    }

    /** purely to make it compile
     * @param atom1
     * @param atom2
     * @param elements
     * @param iii
     * @deprecated
     */
    public MappedAtomPair(CMLAtom atom1, CMLAtom atom2, Element[] elements, int iii) {
    	throw new RuntimeException("NYI");
    }
    
    /** iterate through list till atomIds match.
     * if one id is null, match the other
     * @param toId
     * @param fromId
     * @param atomPairList
     * @return atom pair
     */
	static MappedAtomPair getAtomPair(String toId, String fromId, List<MappedAtomPair> atomPairList) {
		MappedAtomPair atomPair = null;
		if (toId != null || fromId != null) {
			for (int i = 0; i < atomPairList.size(); i++) {
				MappedAtomPair listAtomPair = atomPairList.get(i);
				if ((toId == null || toId.equals(listAtomPair.id2)) &&
					(fromId == null || fromId.equals(listAtomPair.id1))) {
					atomPair = listAtomPair;
					break;
				}
			}
		}
		return atomPair;
	}

	

    /** decide which atom to add the electrons to.
     * @param electronCount if positive add electrons to second atom
     * @param electronId the id
     */
    CMLElectron createElectrons(int electronCount, String electronId) {
    	CMLElectron electron = null;
        if (electronCount > 0) {
            electron = addElectron(atom2, electronCount, electronId);
        } else if (electronCount < 0) {
        	electron = addElectron(atom1, electronCount, electronId);
        }
        return electron;
    }

    /**
     */
    public void draw() {
    	// skip added hydrogens
//    	if (snap.skipAddedHydrogens) {
//    		if ((id1 != null && id1.indexOf("_h") != -1) || (id2 != null && id2.indexOf("_h") != -1)) {
//    			return;
//    		}
//    	}
//        xoff = snap.xFontOff * snap.scale;
//        yoff = snap.yFontOff * snap.scale;
//        chgrad = snap.chgrad * snap.scale;
//        xcoff = snap.xChgOff * snap.scale + chgrad;
//        ycoff = snap.yChgOff * snap.scale + chgrad;
//        x1 = snap.getXCoord(atom1);
//        y1 = snap.getYCoord(atom1);
//        x2 = snap.getXCoord(atom2) + snap.xBoxSeparation;
//        y2 = snap.getYCoord(atom2);
        String charg1 = getCharge(atom1);
        String charg2 = getCharge(atom2);
        opacity1 = fullOpacity;
        opacity2 = fullOpacity;
        unmapped = false;
        if (atom1 == null) {
            x1 = x2;
            y1 = y2;
            charg1 = charg2;
            opacity1 = zeroOpacity;
            unmapped = true;
        } else if (atom2 == null) {
            x2 = x1;
            y2 = y1;
            charg2 = charg1;
            opacity2 = 0;
            unmapped = true;
        }
        
//        g = snap.createElement(snap.svgDoc, "g", snap.animate);
//        snap.appendChild(parent, g);
        // get label string ("" if meaningless)
        // USE FIRST LABEL for annotation (use last for equivalence)
        CMLLabel label1 = (atom1 == null) ? atom2.getLabelElements().get(0): atom1.getLabelElements().get(0);
        CMLLabel label2 = (atom2 == null) ? atom1.getLabelElements().get(0): atom2.getLabelElements().get(0);
        String label1S = (label1 == null) ? "" : label1.getValue();
        @SuppressWarnings("unused")
        String label2S = (label2 == null) ? "" : label2.getValue();
        
//        labelS = (labelS == null) ? "" : labelS;
        // and element type
        String elementType =
            (atom2 == null) ? atom1.getElementType() : atom2.getElementType();
        if (elementType.equals("")) {
            elementType = AS.C.value;
        }
        if (AS.C.equals(elementType)) {
//            elementType = snap.carbon;
        }
        if (!elementType.equals("")) {
//        	Element[] circle = snap.createElement(snap.svgDoc, "circle", snap.animate);
//        	snap.appendChild(g, circle);
//            snap.setAttribute(circle, "r", ""+snap.fontrad * snap.scale);
//            snap.setAttribute(circle, "style", "fill: "+snap.backgroundColor+";");
//            snap.makeAnimate(circle, snap.svgDoc, "cx", snap.format(x1), snap.format(x2), begin, dur);
//            snap.makeAnimate(circle, snap.svgDoc, "cy", snap.format(y1), snap.format(y2), begin, dur);
        }
        if (unmapped) {
//        	Element[] circle = snap.createElement(snap.svgDoc, "circle", snap.animate);
//        	snap.appendChild(g, circle);
//            snap.setAttribute(circle, "r", ""+snap.highlightrad * snap.scale);
//            snap.setAttribute(circle, "style", "fill: "+snap.highlightColor+";");
//            snap.makeAnimate(circle, snap.svgDoc, "cx", snap.format(x1), snap.format(x2), begin, dur);
//            snap.makeAnimate(circle, snap.svgDoc, "cy", snap.format(y1), snap.format(y2), begin, dur);
        }
        ChemicalElement cElement = ChemicalElement.getChemicalElement(elementType);
        if (cElement == null && !elementType.equals("") && !elementType.equals("R")) {
            LOG.error("Unknown element "+elementType);
        }
        // kludge to display carbon
//        elementType = (elementType.equals("")) ? snap.atomDot : elementType;
        elemColor = (cElement == null) ? "#999999" : cElement.getColorString();
        if (elemColor.equals("#ffffff")) {
            elemColor = "#777777";
        }
        if (spectator1 != null) {
//        	elemColor = snap.spectatorColor;
        }
        // elementType
        @SuppressWarnings("unused")
        String elemType1 = (!label1S.equals("")) ? label1S : elementType;
//        String elemType2 = (!label2S.equals("")) ? label2S : elementType;
        // FIXME animate this if two different labels
//        Element text[] = snap.createTextElement(snap.svgDoc, "text", elemType1, snap.animate);
//        snap.appendChild(g, text);
//        snap.setAttribute(text, "style", "font-size: "+snap.atomFontSize+"pt; fill: "+elemColor+"; stroke: none;");
//        snap.makeAnimate(text, snap.svgDoc, "x", snap.format(x1+xoff), snap.format(x2+xoff), begin, dur);
//        snap.makeAnimate(text, snap.svgDoc, "y", snap.format(y1+yoff), snap.format(y2+yoff), begin, dur);
//        // id
//        if (snap.drawId) {
//            drawId();
//        }

        if (!charg1.equals("") || !charg2.equals("")) {
            charge(g, charg1, fullOpacity, zeroOpacity);
            charge(g, charg2, zeroOpacity, fullOpacity);
        }
        // animate the atom opacity
//        snap.makeAnimate(g, snap.svgDoc, "opacity", ""+opacity1, ""+opacity2, begin, dur);
    }
    
    void drawId() {
    	if (atom1 != null) {
//	        Element[] text = snap.createTextElement(snap.svgDoc, "text", atom1.getId(), snap.animate);
//	        snap.appendChild(g, text);
//	        snap.setAttribute(text, "style", "font-size: "+snap.atomFontSize/2+"pt; fill: "+snap.idColor+"; stroke: none;");
//	        snap.makeAnimate(text, snap.svgDoc, "x", snap.format(x1-xoff/2), snap.format(x2-xoff/2), begin, dur);
//	        snap.makeAnimate(text, snap.svgDoc, "y", snap.format(y1-yoff/2), snap.format(y2-yoff/2), begin, dur);
    	}
    }

    void charge(Element g[], String charg, double opacity1, double opacity2) {
//        Element circle[] = snap.createElement(snap.svgDoc, "circle", snap.animate);
//// maybe not required
////        g.appendChild(circle);
//        snap.setAttribute(circle, "r", ""+chgrad);
//        snap.setAttribute(circle, "style", "fill: "+snap.backgroundColor+";");
//        snap.makeAnimate(circle, snap.svgDoc, "cx", ""+snap.format(x1+xcoff), snap.format(x2+xcoff), begin, dur);
//        snap.makeAnimate(circle, snap.svgDoc, "cy", ""+snap.format(y1+ycoff), snap.format(y2+ycoff), begin, dur);
//        snap.makeAnimate(circle, snap.svgDoc, "opacity", ""+opacity1, ""+opacity2, begin, dur);
//
//        Element chg[] = snap.createTextElement(snap.svgDoc, "text", charg, snap.animate);
//        snap.setAttribute(chg, "style", "font-size: 24pt; "+"fill: "+elemColor+";");
//        snap.appendChild(g, chg);
//        snap.makeAnimate(chg, snap.svgDoc, "x", snap.format(x1+xcoff+xoff), snap.format(x2+xcoff+xoff), begin, dur);
//        snap.makeAnimate(chg, snap.svgDoc, "y", snap.format(y1+ycoff+yoff), snap.format(y2+ycoff+yoff), begin, dur);
//        snap.makeAnimate(chg, snap.svgDoc, "opacity", ""+opacity1, ""+opacity2, begin, dur);
    }

    static String getCharge(CMLAtom atom) {
        String chg = "";
        if (atom != null) {
            int c = atom.getFormalCharge();
            if (c == 0) {
            } else if (c == -1) {
                chg = "-";
            } else if (c == 1) {
                chg = "+";
            } else if (c < 0) {
                chg = ""+(-c)+"-";
            } else {
                chg = ""+c+"+";
            }
        }
        return chg;
    }

    /**
     * @param e
     */
    public void setElectronChange(int e) {
        this.electronChange = e;
    }

    /**
     * @return hash
     */
    public int hashCode() {
    	return id1.hashCode()+id2.hashCode();
    }
    
    /**
     * @param atomPair
     * @return equality
     * 
     */
    public boolean equals(Object atomPair) {
    	return (atomPair instanceof AtomPair &&
			((MappedAtomPair) atomPair).id1.equals(id1) &&
			((MappedAtomPair) atomPair).id2.equals(id2));
    }

    /**
     * @return string
     */
    public String toString() {
        return "AtomPair: "+((atom1 == null) ? "?" : atom1.getId())+"--"+((atom2 == null) ? "?" : atom2.getId())+" change: "+electronChange;
    }
}



