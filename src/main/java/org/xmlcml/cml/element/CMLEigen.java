package org.xmlcml.cml.element;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLException;
import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.euclid.EuclidException;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealMatrix;

/**
 * user-modifiable class supporting eigen. * autogenerated from schema use as a
 * shell which can be edited
 * 
 */
public class CMLEigen extends AbstractEigen {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;
	
    /** orientation of matrix. */
    public enum Orientation {
        /** values down */
        VALUES_ROWS("rowVectors", "eigenvalues correspond to rows"),
        /** values across */
        VALUES_COLS("columnVectors", "eigenvalues correspond to columns");
        /** this should corespond to schema enumeration */
        public String value;

        /** a description */
        public String desc;

        private Orientation(String value, String desc) {
            this.desc = desc;
            this.value = value;
        }
    }

    /**
     * constructor.
     */
    public CMLEigen() {
    }

    /**
     * constructor.
     * 
     * @param old
     */
    public CMLEigen(CMLEigen old) {
        super((AbstractEigen) old);

    }

    /**
     * copy node .
     * 
     * @return Node
     */
    public Node copy() {
        return new CMLEigen(this);

    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     * 
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLEigen
     */
    public static CMLEigen makeElementInContext(Element parent) {
        CMLEigen eigen = new CMLEigen();
        return eigen;
    }

    /**
     * check child array and matrix and update.
     * 
     * @param parent
     *            element
     */
    public void finishMakingElement(Element parent) {
        CMLArray eigenvalues = this.getEigenvalues();
        CMLMatrix eigenvectors = this.getEigenvectors();
        if (eigenvalues != null && eigenvectors != null) {
            if (eigenvectors.getRowsAttribute() == null
                    || eigenvectors.getColumnsAttribute() == null) {
                throw new CMLRuntimeException(
                        "must give rows and columns attributes on eigenvectors");
            }
            String orientation = this.getOrientation();
            if (!Orientation.VALUES_COLS.value.equals(orientation)
                    && !Orientation.VALUES_ROWS.value.equals(orientation)) {
                throw new CMLRuntimeException(
                        "must give valid orientation on eigenvectors: "
                                + orientation);
            }
        }
    }

    /**
     * create from matched eigenvectors and eigenvalues. must be of matched
     * size.
     * 
     * @param eigenvectors
     * @param eigenvalues
     * @param orient
     *            orientation
     * @throws CMLException
     */
    public CMLEigen(CMLMatrix eigenvectors, CMLArray eigenvalues,
            Orientation orient) throws CMLException {
        if (eigenvectors == null && eigenvalues == null) {
            throw new CMLException("null eigen argument(s)");
        }
        if (eigenvectors.getRows() != eigenvectors.getColumns()) {
            throw new CMLException("eigenvector matrix must be square: rows("
                    + eigenvectors.getRows() + ") columns ("
                    + eigenvectors.getColumns() + S_RBRAK);
        }
        if (eigenvalues.getSize() != eigenvectors.getColumns()) {
            throw new CMLException("eigenvector matrix ("
                    + eigenvectors.getColumns()
                    + ") incompatible with eigenvalues ("
                    + eigenvalues.getSize() + S_RBRAK);
        }
        if (!(XSD_DOUBLE.equals(eigenvalues.getDataType()))) {
            throw new CMLException("eigenvalue matrix must be real numbers");
        }
        if (!(XSD_DOUBLE.equals(eigenvectors.getDataType()))) {
            throw new CMLException("eigenvector matrix must be real numbers");
        }
        this.appendChild(eigenvalues);
        this.appendChild(eigenvectors);
        this.setOrientation(orient.value);
        // RealMatrix rm = eigenvectors.getEuclidRealMatrix();
    }

    /**
     * return size of matrix and array.
     * 
     * @return the size (or 0 if no valid matrix)
     */
    public int getSize() {
        int size = 0;
        CMLArray eigenvalues = this.getEigenvalues();
        if (eigenvalues != null) {
            size = eigenvalues.getDoubles().length;
        }
        return size;
    }

    /**
     * gets eigenvectors. note that the orientation is not normalized
     * 
     * @return eigenvectors
     */
    public CMLMatrix getEigenvectors() {
        CMLMatrix eigenvectors = (this.getMatrixElements().size() == 1) ? this
                .getMatrixElements().get(0) : null;
        if (eigenvectors != null
                && !XSD_DOUBLE.equals(eigenvectors.getDataType())) {
            throw new CMLRuntimeException("eigenvectors array must be of type double");
        }
        return eigenvectors;
    }

    /**
     * gets eigenvectors. note that the orientation is not normalized
     * 
     * @return eigenvectors
     */
    public CMLArray getEigenvalues() {
        CMLArray eigenvalues = (this.getArrayElements().size() == 1) ? this
                .getArrayElements().get(0) : null;
        if (eigenvalues != null
                && !XSD_DOUBLE.equals(eigenvalues.getDataType())) {
            throw new CMLRuntimeException("eigenvalues array must be of type double");
        }
        return eigenvalues;
    }

    /**
     * gets given eigenvector. convenience method. Inefficient if many
     * eigenvectors are required in which case a CMLMatrix should be extracted
     * and processed.
     * 
     * @param serial
     * @return eigenvector or null if none
     * @throws CMLRuntimeException
     *             if serial is out of range
     */
    public RealArray getEigenvector(int serial) throws CMLRuntimeException {
        RealArray array = null;
        if (serial < 0) {
            throw new CMLRuntimeException("bad index: " + serial);
        }
        CMLMatrix matrix = getEigenvectors();
        if (matrix != null) {
            if (serial >= matrix.getRows()) {
                throw new CMLRuntimeException("bad index: " + serial);
            }
            if (matrix != null && serial < matrix.getRows()
                    && XSD_DOUBLE.equals(matrix.getDataType())) {
                RealMatrix mat = matrix.getEuclidRealMatrix();
                if (Orientation.VALUES_COLS.value.equals(this.getOrientation())) {
                    try {
                        array = mat.extractColumnData(serial);
                    } catch (EuclidException e) {
                        throw new CMLRuntimeException("bug " + e);
                    }
                } else if (Orientation.VALUES_ROWS.value.equals(this
                        .getOrientation())) {
                    System.out.println("ROW");
                    try {
                        array = mat.extractRowData(serial);
                    } catch (EuclidException e) {
                        throw new CMLRuntimeException("bug " + e);
                    }
                } else {
                    throw new CMLRuntimeException("unknown orientation: "
                            + this.getOrientation());
                }
            }
        }
        return array;
    }
}