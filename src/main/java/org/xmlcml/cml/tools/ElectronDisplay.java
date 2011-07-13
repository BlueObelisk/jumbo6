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

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLElectron;

/**
 * tool to support reactions. not fully developed
 * 
 * @author pmr
 * 
 */
public class ElectronDisplay {
    static Logger logger = Logger.getLogger(ElectronDisplay.class.getName());

    double fullOpacity = 1;
    double zeroOpacity = 0;
    
    Element[] g;
    double opacity1;
    double opacity2;
    double x11;
    double y11;
    double x21;
    double y21;
    // vector for orientation
    double x12;
    double y12;
    double x22;
    double y22;
    double xoff;
    double yoff;
    double xcoff;
    double ycoff;
    double chgrad;
    
    // to ensure compilation
    CMLElectron electron1;
    CMLElectron electron2;

    /**
     * 
     */
    public ElectronDisplay() {
    }
    
    
    /**
     */
    public void draw() {
        CMLElement ab1 = (electron1 == null) ? null : (CMLElement) electron1.getParent();
        CMLElement ab2 = (electron2 == null) ? null : (CMLElement) electron2.getParent();
        drawElectron(ab1, ab2);
    }

    /**
    * @return 4 doubles, atom coord and at0-> at1 vector
    */
    double[] getXYCoord(CMLAtom atom) {
        double[] xy = null;
        if (atom != null) {
            xy = new double[4];
            xy[0] = atom.getX2();
            xy[1] = atom.getY2();
            // default vector
            xy[2] = 0;
            xy[3] = 0;
            int nlig = atom.getLigandAtoms().size();
            for (int i = 0; i < nlig; i++) {
                CMLAtom ligand = atom.getLigandAtoms().get(i);
                xy[2] += ligand.getX2();
                xy[3] += ligand.getY2();
            }
            if (nlig > 0) {
                xy[2] /= nlig;
                xy[3] /= nlig;
            } else {
                xy[2] = xy[0];
                xy[3] = xy[1];
            }
            xy[2] -= xy[0];
            xy[3] -= xy[1];
//            xy[0] = scaleX(xy[0]);
//            xy[1] = scaleY(xy[1]);
//            xy[0] = snap.xMol2Screen(xy[0]);
//            xy[1] = snap.yMol2Screen(xy[1]);
            xy[2] = scaleDX(xy[2]);
            xy[3] = scaleDY(xy[3]);
        }
        return xy;
    }

    double scaleDX(double x) {
//      return (x ) * snap.scale;
      return (x );
    }
    
    double scaleDY(double y) {
//        return ( - y) * snap.scale;
        return ( - y);
    }
    /** electrons in bond
    * @return 4 doubles, midpoint and at0-> at1 vector
    */
    // FIXME
    double[] getXYCoord(CMLBond bond) {
        double[] xy = null;
        if (bond != null) {
//            CMLAtom atom0 = bond.getAtom(0);
//            double x0 = snap.xMol2Screen(atom0.getX2());
//            double y0 = snap.yMol2Screen(atom0.getY2());
//            CMLAtom atom1 = bond.getAtom(1);
//            double x1 = snap.xMol2Screen(atom1.getX2());
//            double y1 = snap.yMol2Screen(atom1.getY2());
//            xy = new double[4];
//            xy[0] = (x0 + x1) / 2.;
//            xy[1] = (y0 + y1) / 2.;
//            // default vector
//            xy[2] = (x1 - x0);
//            xy[3] = (y1 - y0);
        }
        return xy;
    }

    @SuppressWarnings("unused")
    void drawElectron(CMLElement ab1, CMLElement ab2) {
        if (ab1 == null) {
//            LOG.info("Null electron1");
            return;
        }
        if (ab2 == null) {
//            LOG.info("Null electron2");
            return;
        }
//        double[] xy1 = null;
//        double[] xy2 = null;
//        if (ab1 instanceof CMLBond) {
//            xy1 = getXYCoord((CMLBond) ab1);
//        } else if (ab1 instanceof CMLAtom) {
//            xy1 = getXYCoord((CMLAtom) ab1);
//        }
//        if (ab2 instanceof CMLBond) {
//            xy2 = getXYCoord((CMLBond) ab2);
//        } else if (ab2 instanceof CMLAtom) {
//            xy2 = getXYCoord((CMLAtom) ab2);
//        }
//        xy2[0] += snap.xBoxSeparation;
//        
//        opacity1 = fullOpacity;
//        opacity2 = fullOpacity;
//        if (electron1 == null) {
//            System.arraycopy(xy2, 0, xy1, 0, 4);
//            opacity1 = 0;
//        } else if (electron2 == null) {
//            System.arraycopy(xy1, 0, xy2, 0, 4);
//            opacity2 = 0;
//        }
//        if (snap.drawPair) {
//            drawElectron1(1.0, xy1, xy2, opacity1, opacity2);
//            drawElectron1(-1.0, xy1, xy2, opacity1, opacity2);
//        }
//        if (snap.drawArrow) {
//            drawArrow(xy1, xy2, opacity1, opacity2);
//        }
    }

    void drawElectron1(double sep, double[] xy1, double[] xy2, double opacity1, double opacity2) {
//        double offset = snap.fontrad * snap.scale * sep;
//        g = snap.createElement(snap.svgDoc, "g", snap.animate);
//        snap.appendChild(parent, g);
//        Element[] circle = snap.createElement(snap.svgDoc, "circle", snap.animate);
//        snap.appendChild(g, circle);
//        snap.setAttribute(circle, "r", ""+snap.fontrad * snap.scale * snap.electronSize);
//        snap.setAttribute(circle, "style", "fill: "+snap.electronColor+";");
//      double vecscale = snap.electronSep * offset;
//      double vecscale = 1.0;

      @SuppressWarnings("unused")
        double dx1 = 0;
      @SuppressWarnings("unused")
        double dx2 = 0;
        @SuppressWarnings("unused")
        double dy1 = 0;
        @SuppressWarnings("unused")
        double dy2 = 0;

//        if (xy1 != null) {
//            dx1 = -xy1[3]*vecscale;
//            dy1 = xy1[2]*vecscale;
//        }
//        if (xy2 != null) {
//            dx2 = -xy2[3]*vecscale;
//            dy2 = xy2[2]*vecscale;
//        }

//        snap.makeAnimate(circle, snap.svgDoc, "cx", snap.format(xy1[0]+dx1), snap.format(xy2[0]+dx2), begin, dur);
//        snap.makeAnimate(circle, snap.svgDoc, "cy", snap.format(xy1[1]+dy1), snap.format(xy2[1]+dy2), begin, dur);
//        snap.makeAnimate(g, snap.svgDoc, "opacity", ""+opacity1, ""+opacity2, begin, dur);
    }

    void drawArrow(double[] xy1, double[] xy2, double opacity1, double opacity2) {
//        g = snap.createElement(snap.svgDoc, "g", snap.animate);
//        snap.appendChild(parent, g);

//        double x1 = xy1[0];
//        double x2 = xy2[0];
//        double y1 = xy1[1];
//        double y2 = xy2[1];

        /** I can't work this...
        Element linearGradient = snap.new Element("linearGradient");
        String id = ""+linearGradient.hashCode();
        linearGradient.addAttribute(new Attribute("x1", ""+snap.format(x1));
        linearGradient.addAttribute(new Attribute("x2", ""+snap.format(x2));
        linearGradient.addAttribute(new Attribute("y1", ""+snap.format(y1));
        linearGradient.addAttribute(new Attribute("y2", ""+snap.format(y2));
        linearGradient.addAttribute(new Attribute("id", id);
        linearGradient.addAttribute(new Attribute("gradientUnits", "UserSpaceOnUse");
        Element stop = snap.new Element("stop");
        stop.addAttribute(new Attribute("offset", "0");
        stop.addAttribute(new Attribute("style", "stop-color:white");
        linearGradient.appendChild(stop);
        stop = snap.new Element("stop");
        stop.addAttribute(new Attribute("offset", "1");
        stop.addAttribute(new Attribute("style", "stop-color:blue");
        linearGradient.appendChild(stop);
        g.appendChild(linearGradient);
        ...-*/

//        Element[] line = snap.createElement(snap.svgDoc, "line", snap.animate);
//        snap.appendChild(g, line);
//
//        snap.setAttribute(line, "x1", ""+snap.format(x1));
//        snap.setAttribute(line, "x2", ""+snap.format(x2));
//        snap.setAttribute(line, "y1", ""+snap.format(y1));
//        snap.setAttribute(line, "y2", ""+snap.format(y2));
////        line.addAttribute(new Attribute("style", "stroke:url(#"+id+"); stroke-width: "+snap.arrowWidth+";");
//        snap.setAttribute(line, "style", "stroke: #70f070; stroke-width: "+snap.arrowWidth+";");
//
//        snap.setAttribute(line, "x2", ""+snap.format(x2));
////        snap.makeAnimate(line, snap.svgDoc, "x1", snap.format(x1), snap.format(x1), begin, dur);
//        snap.makeAnimate(g, snap.svgDoc, "opacity", ""+opacity1, ""+opacity2, begin, dur);
    }

    /**
     * @return string
     */
    public String toString() {
        String s = "";
        s += electron1+"/";
        s += electron2+"/";
        return s;
    }

}
