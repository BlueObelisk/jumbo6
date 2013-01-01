package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.euclid.IntMatrix;

public class AdjacencyMatrix {
	private IntMatrix adjacency;
    Map<String, Integer> atomIdSerialMap = new HashMap<String, Integer>();
	private int size;
	private CMLAtomSet atomSet;

    public AdjacencyMatrix() {
    	
    }

    public AdjacencyMatrix(CMLAtomSet atomSet) {
    	this.atomSet = atomSet;
		size = atomSet.size();
    	populateIntMatrix();
        indexAtomSerialById();
    }

    private void populateIntMatrix() {
    	adjacency = new IntMatrix(size, size);
        atomIdSerialMap = new HashMap<String, Integer>();
        adjacency.setAllElements(1);
        for (int i = 0; i < size; i++) {
            adjacency.setElementAt(i, i, 0);
        }
 	}

	private void indexAtomSerialById() {
		for (int i = 0; i < size; i++) {
        	atomIdSerialMap.put(atomSet.getAtom(i).getId(), i);
        }
	}
	
    public void removeTorsionsFromAdjacencyMatrix(List<CMLTorsion> torsionList) {
		for (CMLTorsion torsion : torsionList) {
            Integer atId0 = atomIdSerialMap.get(torsion.getAtomRefs4()[0]);
            Integer atId3 = atomIdSerialMap.get(torsion.getAtomRefs4()[3]);
            adjacency.setElementAt(atId0, atId3, 0);
            adjacency.setElementAt(atId3, atId0, 0);
        }
	}
	public void removeAnglesFromAdjacencyMatrix(List<CMLAngle> angleList) {
		for (CMLAngle angle : angleList) {
            Integer atId0 = atomIdSerialMap.get(angle.getAtomRefs3()[0]);
            Integer atId2 = atomIdSerialMap.get(angle.getAtomRefs3()[2]);
            adjacency.setElementAt(atId0, atId2, 0);
            adjacency.setElementAt(atId2, atId0, 0);
        }
	}
	public void removeBondsFromAdjacencyMatrix(List<CMLBond> bondList) {
		for (CMLBond bond : bondList) {
            Integer atId0 = atomIdSerialMap.get(bond.getAtomRefs2()[0]);
            Integer atId1 = atomIdSerialMap.get(bond.getAtomRefs2()[1]);
            adjacency.setElementAt(atId0, atId1, 0);
            adjacency.setElementAt(atId1, atId0, 0);
        }
	}

	public IntMatrix getAdjacencies() {
		return adjacency;
	}

	/** gets unique off diagonal pairs as CMLLength elements without values
	 * 
	 * @return
	 */
	public List<CMLLength> getUniquePairs() {
		List<CMLLength> lengthList = new ArrayList<CMLLength>();
		for (int i = 0; i < size; i++) {
			CMLAtom atomi = atomSet.getAtom(i);
			for (int j = i + 1; j < size; j++) {
				if (adjacency.elementAt(i, j) == 1) {
					CMLAtom atomj = atomSet.getAtom(j);
					CMLLength length = new CMLLength();
					length.setAtomRefs2(atomi, atomj);
					length.setId(atomi.getId()+"_"+atomj.getId());
					lengthList.add(length);
				}
			}
		}
		return lengthList;
	}

	public int getSize() {
		return size;
	}
}
