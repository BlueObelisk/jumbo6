package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.attribute.DelimiterAttribute;
import org.xmlcml.cml.attribute.NamespaceRefAttribute;
import org.xmlcml.cml.attribute.UnitsAttribute;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.base.CMLType;
import org.xmlcml.cml.interfacex.HasDataType;
import org.xmlcml.cml.interfacex.HasUnits;
import org.xmlcml.cml.map.NamespaceToUnitListMap;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.IntMatrix;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealMatrix;
import org.xmlcml.euclid.RealSquareMatrix;
import org.xmlcml.euclid.Util;

/**
 * user-modifiable class supporting matrix. * autogenerated from schema use as a
 * shell which can be edited
 *
 */
public class CMLMatrix extends AbstractMatrix implements HasUnits, HasDataType {

	/** types of matrix.
	 */
	public enum Type {
	    /** dewisott */
        RECTANGULAR("rectangular"),
	    /** dewisott */
        SQUARE("square"),
	    /** dewisott */
        SQUARE_SYMMETRIC("squareSymmetric"),
	    /** dewisott */
        SQUARE_SYMMETRIC_LT("squareSymmetricLT"),
	    /** dewisott */
        SQUARE_SYMMETRIC_UT("squareSymmetricUT"),
	    /** dewisott */
        SQUARE_ANTISYMMETRIC("squareAntisymmetric"),
	    /** dewisott */
        SQUARE_ANTISYMMETRIC_LT("squareAntisymmetricLT"),
	    /** dewisott */
        SQUARE_ANTISYMMETRIC_UT("squareAntisymmetricUT"),
	    /** dewisott */
        DIAGONAL("diagonal"),
	    /** dewisott */
        UPPER_TRIANGULAR("upperTriangular"),
	    /** dewisott */
        UPPER_TRIANGULAR_UT("upperTriangularUT"),
	    /** dewisott */
        LOWER_TRIANGULAR("lowerTriangular"),
	    /** dewisott */
        LOWER_TRIANGULAR_LT("lowerTriangularLT"),
	    /** dewisott */
        UNIT("unit"),
	    /** dewisott */
        UNITARY("unitary"),
	    /** dewisott */
        ROW_EIGENVECTORS("rowEigenvectors"),
	    /** dewisott */
        ROTATION22("rotation22"),
	    /** dewisott */
        ROTATION_TRANSLATION32("rotationTranslation32"),
	    /** dewisott */
        HOMOGENEOUS33("homogeneous33"),
	    /** dewisott */
        ROTATION33("rotation33"),
	    /** dewisott */
		ROTATION_TRANSLATION43("rotationTranslation43"),
	    /** dewisott */
		HOMOGENEOUS44("homogeneous44")
		;
		/** value */
		public String value;
		private Type(String v) {
			this.value = v;
		}
	}

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

	private DelimiterAttribute delimiterAttribute = null;
    /**
     * constructor.
     *
     */
    public CMLMatrix() {
    }

    /**
     * constructor.
     *
     * @param old
     */
    public CMLMatrix(CMLMatrix old) {
        super((AbstractMatrix) old);

    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLMatrix(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLMatrix
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLMatrix();
    }

    private void ensureDelimiterAttribute() {
    	if (delimiterAttribute == null) {
    		delimiterAttribute = (DelimiterAttribute) this.getDelimiterAttribute();
    	}
    	if (delimiterAttribute == null) {
    		delimiterAttribute = new DelimiterAttribute(S_SPACE);
    	}
    }

    // =========================== additional constructors
    // ========================

    /**
     * formed from components. rows and columns are extracted from array
     * dimensions sets dataType to xsd:double
     *
     * @param matrix
     *            rectangular matrix
     */
    public CMLMatrix(double[][] matrix) {
        this.setMatrix(matrix);
    }

    /**
     * formed from components. rows and columns are extracted from array
     * dimensions sets dataType to xsd:integer
     *
     * @param matrix
     *            rectangular matrix
     * @throws CMLException
     *             wrong matrix shape
     */
    public CMLMatrix(int[][] matrix) throws CMLException {
        this.setMatrix(matrix);
    }

    /**
     * formed from components. rows and columns are extracted from array
     * dimensions sets dataType to xsd:double
     *
     * @param rows
     * @param columns
     * @param array rectangular array
     * @throws CMLRuntimeException wrong shape
     */
    public CMLMatrix(int rows, int columns, double[] array) {
        this.setArray(rows, columns, array);
    }

    /**
     * formed from components. rows and columns are extracted from array
     * dimensions sets dataType to xsd:integer
     *
     * @param rows
     * @param columns
     * @param array
     *            rectangular array
     * @throws CMLRuntimeException
     *             wrong matrix shape
     */
    public CMLMatrix(int rows, int columns, int[] array) throws CMLRuntimeException {
        this.setArray(rows, columns, array);
    }

    // FIXME - move to Euclid
    /** create matrix with special shape.
     * @param array
     * @param rows
     * @param type
     * @return matrix
     */
    public static CMLMatrix createSquareMatrix(RealArray array, int rows, Type type) {
    	CMLMatrix matrix = null;
    	int n = array.size();
    	RealSquareMatrix rsm = null;
    	if (type == Type.SQUARE ||
    			type == Type.SQUARE_SYMMETRIC	// more values later
    		) {
    		if (rows * rows != n) {
    			throw new CMLRuntimeException("square array size ("+n+
					") incompatible with rows: "+rows);
    		}
    		matrix = new CMLMatrix(rows, rows, array.getArray());
    	} else if (type == Type.SQUARE_SYMMETRIC_LT) {
    		if ((rows * (rows + 1)) /2 != n) {
    			throw new CMLRuntimeException("triangular array size ("+n+
    					") incompatible with rows: "+rows);
    		}
        	rsm = RealSquareMatrix.fromLowerTriangle(array);
    	} else if (type == Type.SQUARE_SYMMETRIC_UT) {
        	rsm = RealSquareMatrix.fromUpperTriangle(array);
    	}
    	if (rsm != null) {
        	if (rsm.getRows() != rows) {
    			throw new CMLRuntimeException("array size ("+n+
    					") incompatible with rows: "+rows);
        	}
        	matrix = new CMLMatrix(rows, rows, rsm.getMatrixAsArray());
    	}
    	return matrix;
    }

    // ====================== housekeeping methods =====================

    /**
     * get row count.
     *
     * @return count
     * @throws CMLRuntimeException
     *             if attribute not set
     */
    public int getRows() {
        if (super.getRowsAttribute() == null) {
            throw new CMLRuntimeException("rows attribute must be set");
        }
        return super.getRows();
    }

    /**
     * get column count.
     *
     * @return count
     * @throws CMLRuntimeException
     *             if attribute not set
     */
    public int getColumns() {
        if (super.getColumnsAttribute() == null) {
            throw new CMLRuntimeException("columns attribute must be set");
        }
        return super.getColumns();
    }

    // ====================== housekeeping methods =====================

    /**
     * get strings.
     *
     * @return strings
     */
    String[] getStrings() {
        String[] ss = getSplitContent();
        return ss;
    }

    /**
     * splits content into tokens. if delimiter is whitespace, trims content and
     * splits at whitespace (however long) else assume starts and ends with
     * delimiter
     *
     * @return the tokens
     * @throws CMLRuntimeException
     *             if size attribute is inconsistent
     */
    private String[] getSplitContent() throws CMLRuntimeException {
        String content = this.getXMLContent().trim();
        ensureDelimiterAttribute();
        String[] ss = new String[0];
        content = content.trim();
        if (content.length() > 0) {
	        ensureDelimiterAttribute();
	        ss = delimiterAttribute.getSplitContent(content);
        }
        return ss;
    }

    String[] getStringMatrixElements() throws CMLRuntimeException {
        String delimiter = getDelimiter();
        int rows = getRows();
        int cols = getColumns();
        String content = this.getXMLContent();
        String regex = (delimiter == null || delimiter.trim().equals(S_EMPTY)) ? S_WHITEREGEX
                : delimiter;
        String[] stringArray = content.split(regex);
        if (stringArray.length != rows * cols) {
            throw new CMLRuntimeException("Bad array shape rows: " + rows + " cols: "
                    + cols + " incompatible with elements: "
                    + stringArray.length);
        }
        return stringArray;
    }

    /*--
     void getRealMatrixElements() {
     if (euclRealMatrix == null) {
     getStringMatrixElements();
     double[] doubleArray = new double[stringArray.length];
     if (stringArray != null) {
     for (int i = 0; i < doubleArray.length; i++) {
     try {
     doubleArray[i] = new Double(stringArray[i]).doubleValue();
     } catch (NumberFormatException nfe) {
     throw new EuclidRuntime("Bad double :"+stringArray[i]+" at position: "+i);
     }
     }
     }
     try {
     euclRealMatrix = new RealMatrix(rows, cols, doubleArray);
     } catch (EuclidException e) {
     throw new EuclidRuntime("bug: "+e);
     }
     }
     }
     --*/

    /*--
     void getIntMatrixElements() throws CMLException {
     if (euclIntMatrix == null) {
     getStringMatrixElements();
     int[] intArray = new int[stringArray.length];
     if (stringArray != null) {
     for (int i = 0; i < intArray.length; i++) {
     try {
     intArray[i] = new Integer(stringArray[i]).intValue();
     } catch (NumberFormatException nfe) {
     throw new CMLException("Bad int :"+stringArray[i]+" at position: "+i);
     }
     }
     }
     try {
     euclIntMatrix = new IntMatrix(rows, cols, intArray);
     } catch (EuclidException e) {
     throw new CMLException(S_EMPTY+e);
     }
     }
     }
     --*/

    /**
     * create new CMLMatrix from RealMatrix.
     *
     * @param realMatrix
     *            to create from
     * @return the matrix
     */
    static CMLMatrix createCMLMatrix(RealMatrix realMatrix) {
        CMLMatrix cmlMatrix = null;
        String delimiter = S_SPACE;
        cmlMatrix = new CMLMatrix();
        setXMLContent(cmlMatrix, delimiter, realMatrix.getMatrixAsArray());
        cmlMatrix.setColumns(realMatrix.getCols());
        cmlMatrix.setRows(realMatrix.getRows());
        cmlMatrix.setDelimiter(delimiter);
        return cmlMatrix;
    }

    /**
     * create new CMLMatrix from IntMatrix.
     *
     * @param intMatrix
     *            to create from
     * @return the matrix
     */
    static CMLMatrix createCMLMatrix(IntMatrix intMatrix) {
        CMLMatrix cmlMatrix = null;
        String delimiter = S_SPACE;
        cmlMatrix = new CMLMatrix();
        setXMLContent(cmlMatrix, delimiter, intMatrix.getMatrixAsArray());
        cmlMatrix.setColumns(intMatrix.getCols());
        cmlMatrix.setRows(intMatrix.getRows());
        cmlMatrix.setDelimiter(delimiter);
        return cmlMatrix;
    }

    static void setXMLContent(CMLMatrix cmlMatrix, String delimiter,
            double[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(array[i]);
        }
        cmlMatrix.setXMLContent(sb.toString());
    }

    static void setXMLContent(CMLMatrix cmlMatrix, String delimiter,
            int[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(array[i]);
        }
        cmlMatrix.setXMLContent(sb.toString());
    }

    /**
     * wraps content.
     *
     * @return the matrix (created not copied)
     */
    public RealMatrix getEuclidRealMatrix() {
        RealMatrix rm = new RealMatrix(this.getRows(), this.getColumns(), this
                    .getDoubleArray());
        return rm;
    }

    /**
     * wraps content.
     *
     * @return the matrix (created not copied)
     */
    public IntMatrix getEuclidIntMatrix() {
        return new IntMatrix(this.getRows(), this.getColumns(), this
                    .getIntegerArray());
    }

    // ====================== subsidiary accessors =====================

    /**
     * sets components. resets dataType
     *
     * @param mat
     *            matrix of rowsxcolumns doubles, columns fastest
     */
    public void setMatrix(double[][] mat) {
        RealMatrix mm = new RealMatrix(mat);
        String content = Util.concatenate(mm.getMatrixAsArray(), S_SPACE);
        setRows(mm.getRows());
        setColumns(mm.getCols());
        setDataType(XSD_DOUBLE);
        setXMLContent(content);
    }

    /**
     * sets components. resets dataType
     *
     * @param mat
     *            matrix of rowsxcolumns ints, columns fastest
     * @throws EuclidRuntimeException wrong shape
     */
    public void setMatrix(int[][] mat) {
        IntMatrix mm = new IntMatrix(mat);
        String content = Util.concatenate(mm.getMatrixAsArray(), S_SPACE);
        setRows(mm.getRows());
        setColumns(mm.getCols());
        setDataType(XSD_INTEGER);
        setXMLContent(content);
    }

    /**
     * sets components. resets dataType
     *
     * @param rows
     * @param columns
     * @param array
     *            of rowsxcolumns doubles, columns fastest
     */
    public void setArray(int rows, int columns, double[] array) {
        RealMatrix euclRealMatrix = new RealMatrix(rows, columns, array);
        setRows(rows);
        setColumns(columns);
        setDataType(XSD_DOUBLE);
        setXMLContent(Util.concatenate(euclRealMatrix.getMatrixAsArray(),
                S_SPACE));
    }

    /**
     * sets components. resets dataType
     *
     * @param rows
     * @param columns
     * @param array
     *            of rowsxcolumns ints, columns fastest
     * @throws CMLRuntimeException
     *             wrong shape
     */
    public void setArray(int rows, int columns, int[] array) throws CMLRuntimeException {
        IntMatrix euclIntMatrix = new IntMatrix(rows, columns, array);
        setRows(rows);
        setColumns(columns);
        setDataType(XSD_INTEGER);
        setXMLContent(Util.concatenate(euclIntMatrix.getMatrixAsArray(),
                S_SPACE));
    }

    /**
     * gets dataType. dataType is set by the type of array
     *
     * @return data type
     */
    public String getDataType() {
        String dataType = super.getDataType();
        if (dataType == null) {
            dataType = XSD_STRING;
            super.setDataType(dataType);
        }
		return CMLType.getNormalizedValue(dataType);
    }

    /**
     * gets values as array.
     *
     * @return double array (or null if different type)
     */
    public double[] getDoubleArray() {
        double[] dd = null;
        if (this.getDataType().equals(XSD_DOUBLE) || this.getDataType().equals(FPX_REAL)) {
            dd = Util.splitToDoubleArray(this.getXMLContent(), S_WHITEREGEX);
        }
        return dd;
    }

    /**
     * gets values as array.
     *
     * @return int array (or null if different type)
     */
    public int[] getIntegerArray() {
        int[] ii = null;
        if (XSD_INTEGER.equals(this.getDataType())) {
            try {
                ii = Util.splitToIntArray(this.getXMLContent(), S_WHITEREGEX);
            } catch (EuclidRuntimeException e) {
                throw new CMLRuntimeException("bug " + e);
            }
        }
        return ii;
    }

    /**
     * gets values as matrix.
     *
     * @return double matrix (or null if different type)
     */
    public double[][] getDoubleMatrix() {
        double[][] ddd = null;
        double[] dd = this.getDoubleArray();
        int count = 0;
        if (dd != null) {
            int rows = getRows();
            int columns = getColumns();
            ddd = new double[rows][columns];
            for (int i = 0; i < rows; i++) {
                System.arraycopy(dd, count, ddd[i], 0, columns);
                count += columns;
            }
        }
        return ddd;
    }

    /**
     * gets values as matrix.
     *
     * @return int matrix (or null if different type)
     */
    public int[][] getIntegerMatrix() {
        int[][] iii = null;
        int[] ii = this.getIntegerArray();
        int count = 0;
        if (ii != null) {
            int rows = getRows();
            int columns = getColumns();
            iii = new int[rows][columns];
            for (int i = 0; i < rows; i++) {
                System.arraycopy(ii, count, iii[i], 0, columns);
                count += columns;
            }
        }
        return iii;
    }

    // ====================== functionality =====================

    /**
     * is matrix square.
     *
     * @return columns = rows
     */
    public boolean isSquare() {
        return getRows() == getColumns();
    }

    /**
     * set matrix to be symmetric. currently set by user, so take care fails if
     * columns != rows
     *
     * @param sym
     */
    /*--
     public void setSymmetric(boolean sym) {
     isSymmetric = sym;
     }
     --*/

    /**
     * is matrix symmetric. currently set by user, so take care fails if columns !=
     * rows
     *
     * @return is square and stated to be symmetric
     */
    /*--
     public boolean isSymmetric() {
     return isSymmetric;
     }
     --*/

    /**
     * are two matrices equal. compare rows columns and array contents
     *
     * @param matrix
     *            to compare
     * @param eps
     *            max allowed difference
     * @return true if equal
     */
    public boolean isEqualTo(CMLMatrix matrix, double eps) {
        return (this.getRows() == matrix.getRows()
                && this.getColumns() == matrix.getColumns() && Util.isEqual(
                this.getDoubleArray(), matrix.getDoubleArray(), eps));
    }

    /**
     * concatenate. (I think this is right...) result = this * m2 i.e. if x' =
     * m2 * x and x'' = this * x'' then x'' = result * x;
     *
     * @param m2
     *            matrix to be concatenated
     * @return result of applying this to m2
     * @throws CMLException
     */
    public CMLMatrix multiply(CMLMatrix m2) throws CMLException {
        RealMatrix t = null;
        int m2r = m2.getRows();
        int m2c = m2.getColumns();
        RealMatrix teucl3 = new RealMatrix(m2r, m2c, this.getDoubleArray());
        t = teucl3.multiply(m2.getEuclidRealMatrix());
        return new CMLMatrix(m2r, m2c, t.getMatrixAsArray());
    }

    /**
     * gets unit for matrix.
     *
     * @param unitListMap
     * @return the unit (null if none)
     */
    public CMLUnit getUnit(NamespaceToUnitListMap unitListMap) {
        UnitsAttribute unitsAttribute = (UnitsAttribute) this.getUnitsAttribute();
        CMLUnit unit = null;
        if (unitsAttribute != null) {
            unit = unitListMap.getUnit(unitsAttribute);
        }
        return unit;
    }

    /**
     * converts a real matrix to SI. only affects matrix with units attribute
     * and dataType='xsd:double' replaces the values with the converted values
     * and the units with the SI Units
     *
     * @param unitListMap
     *            map to resolve the units attribute
     */
    public void convertToSI(NamespaceToUnitListMap unitListMap) {
        CMLUnit unit = this.getUnit(unitListMap);
        if (unit != null && XSD_DOUBLE.equals(this.getDataType())
                && unit.getMultiplierToSIAttribute() != null) {
            CMLUnit siUnit = unit.getParentSIUnit();
            if (siUnit != null) {
                double[] array = this.getDoubleArray();
                RealArray realArray = new RealArray(array);
                if (unit.getMultiplierToSIAttribute() != null) {
                    // multiply current value by unit multiplier
                    double multiplier = unit.getMultiplierToSI();
                    realArray = realArray.multiplyBy(multiplier);
                }
                if (unit.getConstantToSIAttribute() != null) {
                    // add constant
                    double constant = unit.getConstantToSI();
                    realArray = realArray.addScalar(constant);
                }
                this.setArray(this.getRows(), this.getColumns(),
                		realArray.getArray());
                siUnit.setUnitsOn(this);
            }
        }
    }

    /**
     * sets units attribute. requires namespace for unit to be in scope.
     *
     * @param prefix for namespace
     * @param id for unit
     * @param namespaceURI sets units namespace if not present already
     */
    public void setUnits(String prefix, String id, String namespaceURI) {
        NamespaceRefAttribute.setUnits((HasUnits)this, prefix, id, namespaceURI);
    }
}
