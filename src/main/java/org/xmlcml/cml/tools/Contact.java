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

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLTransform3;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.RealRange;

/**
 * tool for managing contacts
 * doe NOT recalculate as it is easy to lose context
 * @author pmr
 * 
 */
public class Contact implements Comparable<Contact>, CMLConstants {
    CMLAtom fromAtom;
    CMLAtom toAtom;
    CMLAtom transformedToAtom;
    double dist;
    boolean isInSameMolecule;
    
    CMLTransform3 transform3;
    
    /** make contact.
     * save the two original atoms (to and from), the transformation, and
     * the result of the transformation
     * @param fromAtom
     * @param toAtom original atom 
     * @param transformedToAtom (could be null)
     * @param transform3 (could be null)
     * @param dist the distance
     */
    public Contact(CMLAtom fromAtom, CMLAtom toAtom, CMLAtom transformedToAtom, 
            CMLTransform3 transform3, double dist) {
        this.fromAtom = fromAtom;
        this.toAtom = toAtom;
        this.transformedToAtom = transformedToAtom;
        if (transformedToAtom != null && !toAtom.getId().equals(transformedToAtom.getId())) {
            throw new RuntimeException("transformed atom on contact has wrong id");
        }
        this.transform3 = transform3;
        this.dist = dist;
    }

    /** get from atom.
     * 
     * @return the fromAtom
     */
    public CMLAtom getFromAtom() {
        return fromAtom;
    }

    /** get to atom.
     * 
     * @return the toAtom
     */
    public CMLAtom getToAtom() {
        return toAtom;
    }

    /** get transformed atom.
     * 
     * @return the atom
     */
    public CMLAtom getTransformedToAtom() {
        return transformedToAtom;
    }

    /** get the transform.
     * 
     * @return transform
     */
    public CMLTransform3 getCMLTransform3() {
        return transform3;
    }
    
    /** get distance.
     * 
     * @return interatomic distance
     */
    public double getDistance() {
        return dist;
    }
    
    /** is contact between symmetry related fragments
     * 
     * @return true if fragments originally from same molecule
     */
    public boolean getIsInSameMolecule() {
        return isInSameMolecule;
    }
    
    /** is contact between symmetry related fragments
     * 
     * @param sameMolecule true if fragments originally from same molecule
     */
    public void setSameMolecule(boolean sameMolecule) {
        this.isInSameMolecule = sameMolecule;
    }

    /** get transform.
     * 
     * @return transform
     */
    public CMLTransform3 getTransform3() {
        return transform3;
    }
    /** check whether interpoint distance is in range.
     * 
     * @param xyz
     * @param xyz1
     * @param dist2Range min and max SQUARED distances
     * @return true if d(xyz - xyz1)^2 >= dist2Range.getMin() and
     *  d(xyz - xyz1)^2 <= dist2Range.getMax()
     */
    public static boolean isInRange(Point3 xyz, Point3 xyz1, RealRange dist2Range) {
        double dist2 = xyz.getSquaredDistanceFromPoint(xyz1);
        boolean inRange = dist2 >= dist2Range.getMin() && dist2 <= dist2Range.getMax();
        return inRange;
    }

    /** comparison.
     * uses String representation
     * @param contact to compare
     * @throws ClassCastException contact is is not a Contact
     * @return comparsion of strings
     */
    public int compareTo(Contact contact) throws ClassCastException {
        return this.toString().compareTo(((Contact)contact).toString());
    }
    /** string value.
     * @return string
     */
    public String toString() {
        String s = "";
        s += fromAtom.getId()+S_LBRAK+fromAtom.getElementType()+") "+fromAtom.getXYZ3();
        s += " -> ";
        s += toAtom.getId()+S_LBRAK+toAtom.getElementType()+S_RBRAK+toAtom.getXYZ3();
        s += CMLConstants.S_LBRAK+getCMLTransform3().getEuclidTransform3().getCrystallographicString()+") = "+getDistance();
        return s;
    }
};
