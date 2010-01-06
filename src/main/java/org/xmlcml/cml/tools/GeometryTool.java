package org.xmlcml.cml.tools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.AbstractTool;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAngle;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLLength;
import org.xmlcml.cml.element.CMLMap;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLTable;
import org.xmlcml.cml.element.CMLTorsion;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.tools.AtomMatcher.Strategy;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.IntSquareMatrix;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Point3Vector;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.RealSquareMatrix;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Transform3;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.molutil.ChemicalElement;
/**
 * additional tools for geometry. not fully developed
 * 
 * @author pmr
 * 
 */
public class GeometryTool extends AbstractTool {
    Logger LOG = Logger.getLogger(GeometryTool.class);

    CMLMolecule molecule;
    MoleculeTool moleculeTool;
    
    /**
     * constructor unrelated to individual molecules.
     * 
     * @param molecule
     */
    public GeometryTool() {
    }
    /**
     * constructor with embedded molecule.
     * 
     * @param molecule
     */
    public GeometryTool(CMLMolecule molecule) {
        this.molecule = molecule;
        moleculeTool = MoleculeTool.getOrCreateTool(molecule);
    }
    /**
     * Add calculated 2D coordinates for hydrogen atoms.
     * @param control
     */
    @Deprecated
    public void addCalculated2DCoordinatesForHydrogens(HydrogenControl control) {
         CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
         if (molecules.size() > 0) {
             for (CMLMolecule molecule : molecules) {
                 new GeometryTool(molecule).addCalculated2DCoordinatesForHydrogens(control);
             }
         } else {
        	 boolean omitHydrogens = true;
             if (molecule.hasCoordinates(CoordinateType.TWOD, omitHydrogens)) {
                 double bondLength = moleculeTool.getAverageBondLength(CoordinateType.TWOD, omitHydrogens) * 0.75;
                 if (!Double.isNaN(bondLength)) {
                     for (CMLAtom atom : molecule.getAtoms()) {
                         if (!ChemicalElement.AS.H.equals(atom.getElementType())) {
                             AtomTool atomTool = AtomTool.getOrCreateTool(atom);
                             atomTool.calculateAndAddHydrogenCoordinates(bondLength );
                         }
                     }
                 }
             }
         }
    }
    /**
     * Add calculated 3D coordinates for singly bonded atoms without coordinates
     * (intended for hydrogens).
     * 
     * @param control
     * @throws RuntimeException
     */
    public void addCalculated3DCoordinatesForHydrogens(HydrogenControl control) {
        // TODO
     CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
     if (molecules.size() > 0) {
         for (CMLMolecule molecule : molecules) {
             new GeometryTool(molecule).addCalculated3DCoordinatesForHydrogens(control);
         }
     } else {
         throw new RuntimeException("NYI");
     }
    }
    /**
     * Add calculated coordinates for hydrogen atoms.
     * 
     * We shall add a better selection soon.
     * 
     * @param type 2D or 3D
     * @param control
     */
    public void addCalculatedCoordinatesForHydrogens(CoordinateType type, HydrogenControl control) {
        if (type.equals(CoordinateType.CARTESIAN)) {
            addCalculated3DCoordinatesForHydrogens(control);
        } else if (type.equals(CoordinateType.TWOD)) {
            addCalculated2DCoordinatesForHydrogens(control);
        } else {
            throw new RuntimeException(
                    "Add calculated coordinates for hydrogens: control not recognised: " + type); //$NON-NLS-1$
        }
    }
    boolean addNextTorsion(List<CMLTorsion> tVector,
            Map<CMLAtom, String> usedAtomTable) {
        for (int i = 0; i < tVector.size(); i++) {
            CMLTorsion torsion = tVector.get(i);
            String[] atomRefs4 = torsion.getAtomRefs4();
            CMLAtom at0 = molecule.getAtomById(atomRefs4[0]);
            CMLAtom at1 = molecule.getAtomById(atomRefs4[1]);
            CMLAtom at2 = molecule.getAtomById(atomRefs4[2]);
            CMLAtom at3 = molecule.getAtomById(atomRefs4[3]);
            if (usedAtomTable.get(at0) != null
                    && usedAtomTable.get(at1) != null
                    && usedAtomTable.get(at2) != null) {
                if (usedAtomTable.get(at3) == null) {
                    calculateZMCoords(at0, at1, at2, at3, torsion);
                    usedAtomTable.put(at3, CMLConstants.S_EMPTY); //$NON-NLS-1$
                    tVector.remove(torsion);
                    return true;
                }
            }
            if (usedAtomTable.get(at3) != null
                    && usedAtomTable.get(at2) != null
                    && usedAtomTable.get(at1) != null) {
                if (usedAtomTable.get(at0) == null) {
                    calculateZMCoords(at3, at2, at1, at0, torsion);
                    usedAtomTable.put(at0, CMLConstants.S_EMPTY); //$NON-NLS-1$
                    tVector.remove(torsion);
                    return true;
                }
            }
        }
        return false;
    }
    void calculateZMCoords(CMLAtom at0, CMLAtom at1, CMLAtom at2, CMLAtom at3,
            CMLTorsion torsion) {
        CMLAngle ang0 = MoleculeTool.getOrCreateTool(molecule).getAngle(at1, at2, at3);
        if (ang0 == null) {
            throw new RuntimeException("Cannot find angle: " + //$NON-NLS-1$
                    at1.getId() + " - " + at2.getId() + " - " + at3.getId()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        CMLBond bond = molecule.getBond(at2, at3);
        if (bond == null) {
            throw new RuntimeException(
                    "Cannot find bond: " + at2.getId() + " - " + at3.getId()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        double length = 0.0;
        try {
            length = bond
                    .calculateBondLength(CoordinateType.TWOD);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Cannot find length for: " + at2.getId() + CMLConstants.S_SLASH + at3.getId());} //$NON-NLS-1$ //$NON-NLS-2$
        Angle bondAngle = new Angle(ang0.getXMLContent(), Angle.Units.DEGREES);
        Angle torsionAngle = new Angle(torsion.getXMLContent(),
                Angle.Units.DEGREES);
        Point3 p0 = at0.getXYZ3();
        Point3 p1 = at1.getXYZ3();
        Point3 p2 = at2.getXYZ3();
        try {
            Point3 p3 = Point3.calculateFromInternalCoordinates(p0, p1, p2,
                    length, bondAngle, torsionAngle);
            at3.setXYZ3(p3);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(S_EMPTY + e);
        } //$NON-NLS-1$
    }
    void calculateStartTriangle(CMLAtom at0, CMLAtom at1, CMLAtom at2)
	        {
	    if (calculateTriangle(at0, at1, at2)) {
	        return;
	    }
	    if (calculateTriangle(at1, at2, at0)) {
	        return;
	    }
	    if (calculateTriangle(at2, at0, at1)) {
	        return;
	    }
	    throw new RuntimeException(
	            "Cannot find triangle (2 bonds and 1 angle) for: " + //$NON-NLS-1$
	                    at0.getId() + ", " + at1.getId() + ", " + at2.getId()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	/**
     * calculate Cartesian coords from lengths angles and torsions.
     * 
     * assumes that molecule contains exactly the correct number and order of
     * these. Does not yet use CMLZMatrix
     * 
     * @throws RuntimeException
     */
    public void calculateCoordsFromZMatrix() {
        int atomCount = molecule.getAtomCount();
        if (atomCount == 0) {
            return;
        }
        // special cases - NYI
        int bondCount = molecule.getBondCount();
        if (bondCount == 0 && atomCount < 2) {
            return;
        }
        Elements angles = molecule.getChildCMLElements("angle"); //$NON-NLS-1$
        if (angles.size() == 0 && bondCount < 2) {
            return;
        }
        Elements torsions = molecule.getChildCMLElements("torsion"); //$NON-NLS-1$
        if (torsions.size() == 0 && angles.size() < 2) {
            return;
        }
        List<CMLTorsion> tVector = new ArrayList<CMLTorsion>();
        // temporary clone
        for (int i = 0; i < torsions.size(); i++) {
            tVector.add((CMLTorsion) torsions.get(i));
        }
        // first torsion...
        CMLTorsion torsion = tVector.get(0);
        String[] atomRefs4 = torsion.getAtomRefs4();
        CMLAtom at0 = molecule.getAtomById(atomRefs4[0]);
        CMLAtom at1 = molecule.getAtomById(atomRefs4[1]);
        CMLAtom at2 = molecule.getAtomById(atomRefs4[2]);
        this.calculateStartTriangle(at0, at1, at2);
        Map<CMLAtom, String> usedAtomTable = new HashMap<CMLAtom, String>();
        usedAtomTable.put(at0, CMLConstants.S_EMPTY); //$NON-NLS-1$
        usedAtomTable.put(at1, CMLConstants.S_EMPTY); //$NON-NLS-1$
        usedAtomTable.put(at2, CMLConstants.S_EMPTY); //$NON-NLS-1$
        while (addNextTorsion(tVector, usedAtomTable)) {
            ;
        }
        if (tVector.size() > 0) {
            throw new RuntimeException("Some torsions not resolved"); //$NON-NLS-1$
        }
    }
    boolean calculateTriangle(CMLAtom at0, CMLAtom at1, CMLAtom at2)
            {
    	MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
        CMLAngle ang0 = moleculeTool.getAngle(at0, at1, at2);
        if (ang0 == null) {
            return false;
        }
        CMLBond bond1 = molecule.getBond(at0, at1);
        if (bond1 == null) {
            return false;
        }
        CMLBond bond2 = molecule.getBond(at2, at1);
        if (bond2 == null) {
            return false;
        }
        at0.setXYZ3(new Point3(0., 0., 0.));
        double length1;
        try {
            length1 = bond1.calculateBondLength(CoordinateType.CARTESIAN);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Cannot find length for: " + at0.getId() + CMLConstants.S_SLASH + at1.getId()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        at1.setXYZ3(new Point3(length1, 0., 0.));
        double length2 = 0.0;
        try {
            length2 = bond2.calculateBondLength(CoordinateType.TWOD);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Cannot find length for: " + at2.getId() + CMLConstants.S_SLASH + at1.getId()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        double angle = ang0.getXMLContent() / Angle.DEGREES_IN_RADIAN;
        at2.setXYZ3(new Point3(length1 - length2 * Math.cos(angle), length2
                * Math.sin(angle), 0.));
        return true;
    }
    /**
     * uses bondStereo to adjust 2D coordinates.
     * 
     * @throws RuntimeException
     */
    public void layoutDoubleBonds() {
        // FIXME
        List<CMLBond> acyclicDoubleBonds = new ConnectionTableTool(molecule)
                .getAcyclicDoubleBonds();
        for (CMLBond bond : acyclicDoubleBonds) {
            new StereochemistryTool(molecule).layoutDoubleBond(bond);
        }
    }
    /**
     * compare geometries. not developed
     * 
     * @param mol1
     * @param map
     * @throws RuntimeException
     */
    public void compareGeometries(CMLMolecule mol1, CMLMap map) {
         CMLTable table = null; 
         
         table = compareLengths(map, mol1); 
//         htmlTable = table.createHTMLTable(); 
         molecule.appendChild(table);
         
         table = compareAngles(map, mol1);
         molecule.appendChild(table);
         
         table = compareTorsions(map, mol1);
         molecule.appendChild(table); 
    }
    /** compare lengths and return as a table.
     * @deprecated
     * @param map of lengths between the two molecules
     * @param mol1 must already have <length> children
     * @return table
     * @throws RuntimeException
     */
     public CMLTable compareLengths(CMLMap map, CMLMolecule mol1) { 
        boolean calculate = true;
        boolean add = false;
        List<CMLLength> lengthList = null;
        List<CMLLength> toLengths = null;
        Map<String, CMLLength> lengthTable = null;
        // from molecule
        lengthList = createValenceLengths(calculate, add);
        // to molecule
        toLengths = MoleculeTool.getOrCreateTool(mol1).getLengthElements();
        lengthTable = CMLLength.getIndexedLengths(toLengths);
        CMLTable table = createTable("length", 2);
        CMLArray idArray1 = table.getArrayElements().get(0);
        CMLArray valueArray1 = table.getArrayElements().get(1);
        CMLArray idArray2 = table.getArrayElements().get(2);
        CMLArray valueArray2 = table.getArrayElements().get(3);
        
        for (CMLLength length : lengthList) {
            List<String> idList = new ArrayList<String>();
            idList.add(length.getAtomRefs2()[0]);
            idList.add(length.getAtomRefs2()[1]);
            List<String> toIdList = map.getToRefs(idList);
            CMLLength toLength = (CMLLength) lengthTable.get(CMLBond
                    .atomHash(toIdList.get(0), toIdList.get(1)));
            if (toLength == null) {
                continue;
            }
            try {
                valueArray1.append(length.getXMLContent());
                valueArray1.append(toLength.getXMLContent());
            } catch (NumberFormatException nfe) {
                System.err.println("CMLDOM bug: " + nfe);
                continue;
            }
            idArray1.append(idList.get(0) + CMLConstants.S_UNDER + idList.get(1));
            idArray2.append(toIdList.get(0) + CMLConstants.S_UNDER + toIdList.get(1));
            for (int j = 0; j < 4; j++) {
                CMLArray column = table.getArrayElements().get(j+4);
                column.append(molecule.getAtomById(idList.get(j)).getElementType());
            }
        }
        
        CMLArray diffArray = valueArray1.subtract(valueArray2);
        diffArray.setId("diff");
        diffArray.setTitle("mol1 - mol2");
        table.addArray(diffArray);
        return table; 
     } 
     CMLTable createTable(String type, int count) {
         CMLTable table = new CMLTable();
         table.setId(type);
         table.setTitle(type);
         addColumn(table, XSD_STRING, "atoms1", "atoms in mol1");
         addColumn(table, XSD_DOUBLE, type + "1", type + " in mol1");
         addColumn(table, XSD_STRING, "atoms2", "atoms in mol2");
         addColumn(table, XSD_DOUBLE, type + "2", type + " in mol2");
         List<CMLArray> columnList = new ArrayList<CMLArray>();
         for (int i = 0; i < count; i++) {
             CMLArray elemStringArray = new CMLArray();
             elemStringArray.setId("el"+(i+1));
             elemStringArray.setTitle("el"+(i+1));
             columnList.add(elemStringArray);
         }
         
         return table;
     }
     /**
      * @deprecated
      * @param table
      * @param dataType
      * @param id
      * @param title
      */
     void addColumn(CMLTable table, String dataType, String id, String title) {
         CMLArray array = new CMLArray();
         array.setDataType(dataType);
         array.setId(id);
         array.setTitle(title);
         table.addArray(array);
     }
     /** compare angles and return as a table.
      * @deprecated
      * @param map of angles between the two molecules
      * @param mol1 must already have <angle> children
      * @return table
      * @throws RuntimeException
      */
      public CMLTable compareAngles(CMLMap map, CMLMolecule mol1) { 
         boolean calculate = true;
         boolean add = false;
         List<CMLAngle> angleList = null;
         List<CMLAngle> toAngles = null;
         Map<String, CMLAngle> angleTable = null;
         // from molecule
         angleList = createValenceAngles(calculate, add);
         // to molecule
         toAngles = MoleculeTool.getOrCreateTool(mol1).getAngleElements();
         angleTable = CMLAngle.getIndexedAngles(toAngles);
         CMLTable table = createTable("angle", 3);
         CMLArray idArray1 = table.getArrayElements().get(0);
         CMLArray valueArray1 = table.getArrayElements().get(1);
         CMLArray idArray2 = table.getArrayElements().get(2);
         CMLArray valueArray2 = table.getArrayElements().get(3);
         
         for (CMLAngle angle : angleList) {
             String[] id = angle.getAtomRefs3();
             List<String> idList = new ArrayList<String>();
             idList.add(id[0]);
             idList.add(id[1]);
             idList.add(id[2]);
             List<String> toIdList = map.getToRefs(idList);
             CMLAngle toAngle = (CMLAngle) angleTable.get(CMLAngle
                     .atomHash(toIdList.get(0), toIdList.get(1), toIdList.get(2)));
             if (toAngle == null) {
                 continue;
             }
             try {
                 valueArray1.append(angle.getXMLContent());
                 valueArray1.append(toAngle.getXMLContent());
             } catch (NumberFormatException nfe) {
                 System.err.println("CMLDOM bug: " + nfe);
                 continue;
             }
             idArray1.append(id[0] + CMLConstants.S_UNDER + id[1] + CMLConstants.S_UNDER + id[2]);
             idArray2.append(toIdList.get(0) + CMLConstants.S_UNDER + toIdList.get(1) + CMLConstants.S_UNDER + toIdList.get(2));
             for (int j = 0; j < 3; j++) {
                 CMLArray column = table.getArrayElements().get(j+4);
                 column.append(molecule.getAtomById(id[j]).getElementType());
             }
         }
         
         CMLArray diffArray = valueArray1.subtract(valueArray2);
         diffArray.setId("diff");
         diffArray.setTitle("mol1 - mol2");
         table.addArray(diffArray);
         return table; 
      } 
      /** compare torsions and return as a table.
       * @deprecated
       * @param map of torsions between the two molecules
       * @param mol1 must already have <torsion> children
       * @return table
       * @throws RuntimeException
       */
       public CMLTable compareTorsions(CMLMap map, CMLMolecule mol1) { 
          boolean calculate = true;
          boolean add = false;
          List<CMLTorsion> torsionList = null;
          List<CMLTorsion> toTorsions = null;
          Map<String, CMLTorsion> torsionTable = null;
          // from molecule
          torsionList = createValenceTorsions(calculate, add);
          // to molecule
          toTorsions = MoleculeTool.getOrCreateTool(mol1).getTorsionElements();
          torsionTable = CMLTorsion.getIndexedTorsions(toTorsions);
          CMLTable table = createTable("torsion", 4);
          CMLArray idArray1 = table.getArrayElements().get(0);
          CMLArray valueArray1 = table.getArrayElements().get(1);
          CMLArray idArray2 = table.getArrayElements().get(2);
          CMLArray valueArray2 = table.getArrayElements().get(3);
          
          for (CMLTorsion torsion : torsionList) {
              String[] id = torsion.getAtomRefs4();
              List<String> idList = new ArrayList<String>();
              idList.add(id[0]);
              idList.add(id[1]);
              idList.add(id[2]);
              idList.add(id[3]);
              List<String> toIdList = map.getToRefs(idList);
              CMLTorsion toTorsion = (CMLTorsion) torsionTable.get(CMLTorsion
                      .atomHash(toIdList.get(0), toIdList.get(1), toIdList.get(2), toIdList.get(3)));
              if (toTorsion == null) {
                  continue;
              }
              try {
                  valueArray1.append(torsion.getXMLContent());
                  valueArray1.append(toTorsion.getXMLContent());
              } catch (NumberFormatException nfe) {
                  System.err.println("CMLDOM bug: " + nfe);
                  continue;
              }
              idArray1.append(id[0] + CMLConstants.S_UNDER + id[1] + CMLConstants.S_UNDER + id[2] + CMLConstants.S_UNDER + id[3]);
              idArray2.append(toIdList.get(0) + CMLConstants.S_UNDER + toIdList.get(1) + CMLConstants.S_UNDER + 
                      toIdList.get(2) + CMLConstants.S_UNDER + toIdList.get(3));
              for (int j = 0; j < 4; j++) {
                  CMLArray column = table.getArrayElements().get(j+4);
                  column.append(molecule.getAtomById(id[j]).getElementType());
              }
          }
          
          CMLArray diffArray = valueArray1.subtract(valueArray2);
          diffArray.setId("diff");
          diffArray.setTitle("mol1 - mol2");
          table.addArray(diffArray);
          return table; 
       } 
    /**
     * create all valence lengths for molecule. only include each bond once
     * 
     * @param atomSet
     * @param calculate
     *            false=> empty content; true=>calculated values as content
     * @param add
     *            array as childElements of molecule
     * @return array of lengths (zero length if none)
     */
    public List<CMLLength> createValenceLengths(CMLAtomSet atomSet,
            boolean calculate, boolean add) {
        List<CMLLength> lengthVector = new ArrayList<CMLLength>();
        for (CMLAtom atomi : atomSet.getAtoms()) {
            List<CMLAtom> ligandListI = atomi.getLigandAtoms();
            for (CMLAtom atomj : ligandListI) {
                if (atomi.compareTo(atomj) <= 0)
                    continue;
                CMLLength length = new CMLLength();
                length.setAtomRefs2(new String[] {
                        atomi.getId(),
                        atomj.getId() 
                    });
                if (calculate) {
                    double lengthVal = length.getCalculatedLength(molecule);
                    length.setXMLContent(lengthVal);
                }
                if (add) {
                    try {
                        molecule.appendChild(length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                lengthVector.add(length);
            }
        }
        return lengthVector;
    }
    
    /**
     * create all non-bonded lengths for molecule. only include each length once
     * 
     * @param atomSet
     * @param type of coordinate
     * @return array of lengths (zero length if none)
     */
    public List<CMLLength> createNonBondedLengths(CoordinateType type) {
    	return createNonBondedLengths(moleculeTool.getAtomSet(), type);
    }
    
    /**
     * create all non-bonded lengths for molecule. only include each length once
     * 
     * @param atomSet
     * @param type of coordinate
     * @return array of lengths (zero length if none)
     */
    public List<CMLLength> createNonBondedLengths(
    		CMLAtomSet atomSet, CoordinateType type) {
        List<CMLLength> lengthVector = new ArrayList<CMLLength>();
        List<CMLAtom> atomList = atomSet.getAtoms();
        for (int i = 0; i < atomSet.size(); i++) {
        	CMLAtom atomi = atomList.get(i);
        	Object coordi = getCoordinate(atomi, type);
        	if (coordi == null) {
        		continue;
        	}
            List<CMLAtom> ligandListI = atomi.getLigandAtoms();
            for (int j = i+1; j < atomSet.size(); j++) {
            	CMLAtom atomj = atomList.get(j);
            	// already bonded?
            	if (ligandListI.contains(atomj)) {
            		continue;
            	}
            	Object coordj = getCoordinate(atomj, type);
            	if (coordi == null) {
            		continue;
            	}
        		CMLLength length = new CMLLength();
                length.setAtomRefs2(new String[] {
                    atomi.getId(),
                    atomj.getId() 
                });
                double lengthVal = calculateLength(coordi, coordj, type);
                length.setXMLContent(lengthVal);
                lengthVector.add(length);
            }
        }
        return lengthVector;
    }
    
    private Object getCoordinate(CMLAtom atom, CoordinateType type) {
		if (CoordinateType.CARTESIAN.equals(type)) {
			return atom.getXYZ3();
		} else if (CoordinateType.TWOD.equals(type)) {
			return atom.getXY2();
		} else {
			return null;
		}
    }
    
    private double calculateLength(Object coordi, Object coordj, CoordinateType type) {
    	double length = Double.NaN;
    	if (CoordinateType.TWOD.equals(type)) {
    		length = ((Real2) coordi).getDistance((Real2)coordj);
    	} else if (CoordinateType.CARTESIAN.equals(type)) {
    		length = ((Point3) coordi).getDistanceFromPoint((Point3)coordj);
    	}
    	return length;
    }
    
    /**
     * create all valence angles for molecule.
     * 
     * @param atomSet
     * @param calculate
     *            false=> empty content; true=>calculated values (degrees) as
     *            content
     * @param add
     *            array as childElements of molecule
     * @return array of angles (zero length if none)
     */
    public List<CMLAngle> createValenceAngles(CMLAtomSet atomSet,
            boolean calculate, boolean add) {
        List<CMLAngle> angleVector = new ArrayList<CMLAngle>();
        for (CMLAtom atomi : atomSet.getAtoms()) {
            Set<CMLAtom> usedAtomSetj = new HashSet<CMLAtom>();
            List<CMLAtom> ligandListI = atomi.getLigandAtoms();
            for (CMLAtom atomj : ligandListI) {
                usedAtomSetj.add(atomj);
                for (CMLAtom atomk : ligandListI) {
                    if (usedAtomSetj.contains(atomk))
                        continue;
                    CMLAngle angle = new CMLAngle();
                    angle.setAtomRefs3(new String[] { atomj.getId(),
                            atomi.getId(), atomk.getId() });
                    if (calculate) {
                        double angleVal = angle.getCalculatedAngle(molecule);
                        angle.setXMLContent(angleVal);
                    }
                    if (add) {
                        try {
                            molecule.appendChild(angle);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    angleVector.add(angle);
                }
            }
        }
        return angleVector;
    }
    /**
     * get all valence torsions for molecule.
     * 
     * @param atomSet
     * @param calculate
     *            false=> empty content; true=>calculated values (degrees) as
     *            content
     * @param add
     *            array as childElements of molecule
     * @return array of torsions (zero length if none)
     */
    public List<CMLTorsion> createValenceTorsions(CMLAtomSet atomSet,
            boolean calculate, boolean add) {
        List<CMLTorsion> torsionVector = new ArrayList<CMLTorsion>();
        for (CMLBond bond : molecule.getBonds()) {
            CMLAtom at0 = bond.getAtom(0);
            if (!atomSet.contains(at0)) {
                continue;
            }
            CMLAtom at1 = bond.getAtom(1);
            if (!atomSet.contains(at1)) {
                continue;
            }
            List<CMLAtom> ligandList0 = at0.getLigandAtoms();
            for (CMLAtom ligand0 : ligandList0) {
                if (!atomSet.contains(ligand0)) {
                    continue;
                }
                if (ligand0.equals(at1)) {
                    continue;
                }
                List<CMLAtom> ligandList1 = at1.getLigandAtoms();
                for (CMLAtom ligand1 : ligandList1) {
                    if (!atomSet.contains(ligand1)) {
                        continue;
                    }
                    if (ligand1.equals(at0)) {
                        continue;
                    }
                    CMLTorsion torsion = new CMLTorsion();
                    torsion.setAtomRefs4(new String[] { ligand0.getId(),
                            at0.getId(), at1.getId(), ligand1.getId() });
                    if (calculate) {
                        double torsionVal = Double.NaN;
                        try {
                            torsionVal = torsion
                                .getCalculatedTorsion(molecule);
                            torsion.setXMLContent(torsionVal);
                        } catch (RuntimeException e) {
//                            throw new CMLRuntime("Cannot calculate torsion for "+torsion.getString()+"; "+e);
                        }
                    }
                    if (add) {
                        try {
                            molecule.appendChild(torsion);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    torsionVector.add(torsion);
                }
            }
        }
        return torsionVector;
    }
    /**
     * create nonBonded matrix
     * NOT SURE THIS WORKS
     * @return nonBonded matrix (1 = 1,2 or 1,3 interaction, 0 = non-bonded);
     */
    public IntSquareMatrix getNonBondedMatrix() {
        CMLAtomSet atomSet = MoleculeTool.getOrCreateTool(molecule).getAtomSet();
        IntSquareMatrix ism = null;
        List<CMLAtom> atoms = molecule.getAtoms();
        int[][] nbm = new int[atoms.size()][atoms.size()];
        for (int i = 0; i < atoms.size(); i++) {
            for (int j = 0; j < atoms.size(); j++) {
                nbm[i][j] = 0;
            }
            nbm[i][i] = 1;
        }
        // FIXME
        List<CMLTorsion> torsionList = this.createValenceTorsions(atomSet,
                false, false);
        for (CMLTorsion torsion : torsionList) {
            // CMLAtom at0 = torsions[i].getAtomRefs4()[0];
            // int a0 = this.getAtom(at0);
            // CMLAtom at3 = torsions[i].getAtomRefs4()[3];
            // int a3 = this.getAtom(at3);
            // nbm[a0][a3] = 1;
            // nbm[a3][a0] = 1;
            if (1 == 2)
                torsion.debug("TOR"); // FIXME
        }
        List<CMLAngle> angleList = this.createValenceAngles(atomSet, false,
                false);
        for (CMLAngle angle : angleList) {
            // FIXME
            // CMLAtom at0 = angles[i].getAtomRefs3()[0];
            // int a0 = this.getAtom(at0);
            // CMLAtom at2 = angles[i].getAtomRefs3()[2];
            // int a2 = this.getAtom(at2);
            // nbm[a0][a2] = 1;
            // nbm[a2][a0] = 1;
            if (1 == 2)
                angle.debug("GEOM");
        }
        for (CMLBond bond : molecule.getBonds()) {
            // FIXME
            // CMLAtom at0 = bonds[i].getAtom(0);
            // int a0 = this.getAtom(at0);
            // CMLAtom at1 = bonds[i].getAtom(1);
            // int a1 = this.getAtom(at1);
            // nbm[a0][a1] = 1;
            // nbm[a1][a0] = 1;
            if (1 == 2)
                bond.debug("GEOMTOOL");
        }
        for (int i = 0; i < atoms.size(); i++) {
            for (int j = 0; j < atoms.size(); j++) {
                String s = CMLConstants.S_EMPTY + nbm[i][j];
                LOG.info(S_SPACE + s);
            }
            LOG.info(S_NL);
        }
        try {
            ism = new IntSquareMatrix(nbm);
        } catch (Exception e) {
            LOG.error("BUG " + e);
        }
        return ism;
    }
    /**
     * flip (about bond axis) the 2D coordinates attached to atom0.
     * 
     * @param bond
     * @param atom at end of bond to flip
     */
    public void flip2D(CMLBond bond, CMLAtom atom) {
        CMLAtom otherAtom = bond.getOtherAtom(atom);
        if (otherAtom == null) {
        	throw new RuntimeException("Must select an atom in bond");
        } else {
	        CMLAtomSet atomSet = AtomTool.getOrCreateTool(atom).getDownstreamAtoms(otherAtom);
	        Real2 this2 = atom.getXY2();
	        Real2 other2 = otherAtom.getXY2();
	        Vector2 this2v = new Vector2(this2);
	        Vector2 bondVector = new Vector2(other2.subtract(this2));
	        Vector2 yVector = new Vector2(0.0, 1.0);
	        // translate to origin
	        this2v.negative();
	        Transform2 toOrigin = new Transform2(this2v);
	        // translate from origin
	        this2v.negative();
	        Transform2 fromOrigin = new Transform2(this2v);
	        // rotate to y axis
	        Transform2 rotatePlus = new Transform2(bondVector, yVector);
	        // rotate back from y axis
	        Transform2 rotateMinus = new Transform2(yVector, bondVector);
	        // flip about y
	        RealSquareMatrix flipM = new RealSquareMatrix(2, new double[] {
	                -1.0, 0.0, 0.0, 1.0 });
	        Transform2 flipY = new Transform2(flipM);
	        // unit matrix
	        Transform2 mat = new Transform2();
	        RealSquareMatrix temp = mat;
	        temp = toOrigin.multiply(temp);
	        temp = rotatePlus.multiply(temp);
	        temp = flipY.multiply(temp);
	        temp = rotateMinus.multiply(temp);
	        temp = fromOrigin.multiply(temp);
	        mat = new Transform2(temp);
	        atomSet.transform(mat);
        }
    }
//    public void flip2D(CMLBond bond, CMLAtom atom) {
//        try {
//            CMLAtom otherAtom = bond.getOtherAtom(atom);
//            CMLAtomSet atomSet = moleculeTool.getDownstreamAtoms(atom, otherAtom);
//            Real2 this2 = atom.getXY2();
//            Real2 other2 = otherAtom.getXY2();
//            Vector2 this2v = new Vector2(this2);
//            Vector2 bondVector = new Vector2(other2.subtract(this2));
//            Vector2 yVector = new Vector2(0.0, 1.0);
//            // translate to origin
//            this2v.negative();
//            Transform2 toOrigin = new Transform2(this2v);
//            // translate from origin
//            this2v.negative();
//            Transform2 fromOrigin = new Transform2(this2v);
//            // rotate to y axis
//            Transform2 rotatePlus = new Transform2(bondVector, yVector);
//            // rotate back from y axis
//            Transform2 rotateMinus = new Transform2(yVector, bondVector);
//            // flip about y
//            RealSquareMatrix flipM = new RealSquareMatrix(2, new double[] {
//                    -1.0, 0.0, 0.0, 1.0 });
//            Transform2 flipY = new Transform2(flipM);
//            // unit matrix
//            Transform2 mat = new Transform2();
//            RealSquareMatrix temp = mat;
//            temp = toOrigin.multiply(temp);
//            temp = rotatePlus.multiply(temp);
//            temp = flipY.multiply(temp);
//            temp = rotateMinus.multiply(temp);
//            temp = fromOrigin.multiply(temp);
//            mat = new Transform2(temp);
//            atomSet.transform(mat);
//        } catch (Exception e) {
//            throw new RuntimeException(S_EMPTY + e);
//>>>>>>> .merge-right.r915
//        }
//    }
    /**
     * create all valence angles for molecule.
     * 
     * @param calculate
     *            false=> empty content; true=>calculated values (degrees) as
     *            content
     * @param add
     *            array as childElements of molecule
     * @return array of angles (zero length if none)
     */
    public List<CMLAngle> createValenceAngles(boolean calculate, boolean add) {
        CMLAtomSet atomSet = MoleculeTool.getOrCreateTool(molecule).getAtomSet();
        return this.createValenceAngles(atomSet, calculate, add);
    }
    /**
     * create all valence lengths for molecule.
     * 
     * @param calculate
     *            false=> empty content; true=>calculated values (angstrom) as
     *            content
     * @param add
     *            array as childElements of molecule
     * @return array of lengths (zero length if none)
     */
    public List<CMLLength> createValenceLengths(boolean calculate, boolean add) {
        CMLAtomSet atomSet = MoleculeTool.getOrCreateTool(molecule).getAtomSet();
        return this.createValenceLengths(atomSet, calculate, add);
    }
    /**
     * get all valence torsions for molecule.
     * 
     * @param calculate
     *            false=> empty content; true=>calculated values (degrees) as
     *            content
     * @param add
     *            array as childElements of molecule
     * @return array of torsions (zero length if none)
     */
    public List<CMLTorsion> createValenceTorsions(boolean calculate, boolean add) {
        CMLAtomSet atomSet = MoleculeTool.getOrCreateTool(molecule).getAtomSet();
        return this.createValenceTorsions(atomSet, calculate, add);
    }
    /**
     * get all internal coordinates (including redundant ones).
     * 
     * calculates all lengths, angles and torsions in document order.
     * 
     * @param calculate
     *            add calculated values as content
     * @param add
     *            results as childElements (if false, effectively no-op except
     *            debug)
     * 
     */
    public void calculateGeometry(boolean calculate, boolean add) {
        createValenceLengths(calculate, add);
        createValenceAngles(calculate, add);
        createValenceTorsions(calculate, add);
    }
    /**
     * get transformation to reposition one atom pair to another.
     * 
     * the four atomTools atomTool1A, atomTool1B, atomTool2B and atomTool2A are
     * reoriented to form a straight line. atomTool1A and atomTool1B are
     * atomTools in one fragment, atomTool2A and atomTool2B in another.
     * atomTool1A and atomTool1B are often, but not necessarily bonded
     * 
     * there is no requirement for atoms to be in same molecule it is here for
     * convenience - might be moved later
     * 
     * @param atomTool1A
     *            atom in molecule A
     * @param atomTool1B
     *            atom bonded to 1A
     * @param atomTool2A
     *            atom in molecule B
     * @param atomTool2B
     *            atom bonded to 2A
     * @return the rotational transformation
     * @exception RuntimeException
     *                wrong type of atoms, etc.
     */
    public static Transform3 overlapBonds(CMLAtom atomTool1A,
            CMLAtom atomTool1B, CMLAtom atomTool2A, CMLAtom atomTool2B)
            {
        if (atomTool2B == null || atomTool2A == null || atomTool2B == null
                || atomTool2A == null) {
            throw new RuntimeException("null atomTools: ");
        }
        Vector3 otherVector = atomTool1B.getVector3(atomTool1A);
        Vector3 thisVector = atomTool2B.getVector3(atomTool2A);
        Transform3 transform = null;
        try {
            transform = new Transform3(otherVector, thisVector);
        } catch (Exception e) {
            throw new RuntimeException(S_EMPTY + e);
        }
        return transform;
    }

    /** get calculated length.
     * 
     * @param atom0
     * @param atom1
     * @return length or NaN
     */
    public static double getCalculatedLength (CMLAtom atom0, CMLAtom atom1) {
    	double length = Double.NaN;
    	if (atom0 != null && atom1 != null) {
    		Point3 p0 = atom0.getPoint3(CoordinateType.CARTESIAN);
    		Point3 p1 = atom1.getPoint3(CoordinateType.CARTESIAN);
    		if (p0 != null && p1 != null) {
    			length = p0.getDistanceFromPoint(p1);
    		}
    	}
    	return length;
    }
    
    /** get calculated angle.
     * 
     * @param atom0
     * @param atom1
     * @param atom2
     * @return angle or null
     */
    public static Angle getCalculatedAngle (
    		CMLAtom atom0, CMLAtom atom1, CMLAtom atom2) {
    	Angle angle = null;
    	if (atom0 != null && atom1 != null && atom2 != null) {
    		Point3 p0 = atom0.getPoint3(CoordinateType.CARTESIAN);
    		Point3 p1 = atom1.getPoint3(CoordinateType.CARTESIAN);
    		Point3 p2 = atom2.getPoint3(CoordinateType.CARTESIAN);
    		if (p0 != null && p1 != null && p2 != null) {
    			angle = Point3.getAngle(p0, p1, p2);
    		}
    	}
    	return angle;
    }
    
    /** get calculated torsion.
     * 
     * @param atom0
     * @param atom1
     * @param atom2
     * @param atom3
     * @return torsion or null
     */
    public static Angle getCalculatedTorsion (
    		CMLAtom atom0, CMLAtom atom1, CMLAtom atom2, CMLAtom atom3) {
    	Angle torsion = null;
    	if (atom0 != null && atom1 != null && atom2 != null && atom3 != null) {
    		Point3 p0 = atom0.getPoint3(CoordinateType.CARTESIAN);
    		Point3 p1 = atom1.getPoint3(CoordinateType.CARTESIAN);
    		Point3 p2 = atom2.getPoint3(CoordinateType.CARTESIAN);
    		Point3 p3 = atom3.getPoint3(CoordinateType.CARTESIAN);
    		if (p0 != null && p1 != null && p2 != null && p3 != null) {
    			torsion = Point3.getTorsion(p0, p1, p2, p3);
    		}
    	}
    	return torsion;
    }
    

	public void displayAlignments(List<List<MoleculePair>> moleculePairMatrix, List<CMLMolecule> identicalMoleculeList) {
		
		int size = identicalMoleculeList.size();
		if (size > 1) {
			for (int i = 0; i < size; i++) {
				CMLMolecule moleculei = identicalMoleculeList.get(i);
				Point3Vector coordsi = new Point3Vector(MoleculeTool.getOrCreateTool(moleculei).getCoordinates3(CoordinateType.CARTESIAN));
				List<MoleculePair> moleculePairList = moleculePairMatrix.get(i);
				for (int j = i+1; j < size; j++) {
					CMLMolecule moleculej = identicalMoleculeList.get(j);
					Point3Vector coordsj = new Point3Vector(MoleculeTool.getOrCreateTool(moleculej).getCoordinates3(CoordinateType.CARTESIAN));
					MoleculePair moleculePair = moleculePairList.get(j - i - 1);
					Transform3 transform3 = moleculePair.getTransform3();
					// have to use map for this
					//FIXME
					LOG.error("COORDINATES NEED MAPPING");
					coordsj.transform(transform3);
			        @SuppressWarnings("unused")
					double rms = coordsj.rms(coordsi);
//					Util.print("| "+rms);
				}
				LOG.debug("");
			}
			LOG.debug("-------------");
		} else {
			System.err.println("Cannot align only one molecule");
		}
	}

}
