package org.xmlcml.cml.element;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.base.CMLRuntimeException;

/**
 * A set of 3 cell parameters.
 *
 *
 * Either 3 lengths or 3 angles.
 *
 * user-modifiable class autogenerated from schema if no class exists use as a
 * shell which can be edited the autogeneration software will not overwrite an
 * existing class file
 *
 */
public class CMLCellParameter extends AbstractCellParameter {

	/** namespaced element name.*/
	public final static String NS = C_E+TAG;

    /** type of cellParameter */
    public enum Type {
        /** three lengths */
        LENGTH("length"),
        /** three angles */
        ANGLE("angle");
        /** value for comparison */
        public final String s;

        Type(String s) {
            this.s = s;
        }
    }

    /**
     * dictRef attributes for obsolete cell parameters. do not use these if
     * possible. cellParameter has replaced this
     */
    public final static String[] dictRef = { CMLCrystal.A, CMLCrystal.B, CMLCrystal.C,
            CMLCrystal.ALPHA, CMLCrystal.BETA, CMLCrystal.GAMMA, };

    /**
     * constructor
     */

    public CMLCellParameter() {
    }

    /**
     * copy constructor
     *
     * @param old
     *            CMLCellParameter to copy
     */
    public CMLCellParameter(CMLCellParameter old) {
        super((AbstractCellParameter) old);
    }

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new CMLCellParameter(this);
    }

    /**
     * create new instance in context of parent, overridable by subclasses.
     *
     * @param parent
     *            parent of element to be constructed (ignored by default)
     * @return CMLCellParameter
     */
    public CMLElement makeElementInContext(Element parent) {
        return new CMLCellParameter();
    }

    /**
     * constructor from 3 CMLScalar of correct type.
     *
     * @param scalar
     * @param type
     */
    public CMLCellParameter(CMLScalar[] scalar, Type type) {
        this();
        if (scalar == null || scalar.length != 3) {
            throw new CMLRuntimeException("bad args to constructor" + scalar);
        }
        double[] values = new double[3];
        String units = null;
        double[] error = new double[3];
        boolean hasError = false;
        for (int i = 0; i < 3; i++) {
            values[i] = scalar[i].getDouble();
            if (units == null) {
                units = scalar[i].getUnits();
            }
            error[i] = Double.NaN;
            if (scalar[i].getErrorValueAttribute() != null) {
                error[i] = scalar[i].getErrorValue();
                hasError = true;
            }
        }
        if (units != null) {
            this.setUnits(units);
        }
        if (hasError) {
            this.setError(error);
        }
        if (type == null) {
            throw new CMLRuntimeException("Null type");
        } else if (type.equals(Type.LENGTH) || type.equals(Type.ANGLE)) {
            this.setType(type.s);
            this.setXMLContent(values);
        }

    }

    /**
     * constructor from 3 CMLScalar of correct type.
     *
     * @param scalar
     * @param type
     */
    public CMLCellParameter(List<CMLScalar> scalar, Type type) {
        this((CMLScalar[]) scalar.toArray(new CMLScalar[0]), type);
    }

    /**
     * extracts cellParameter of given type.
     *
     * @param cellParameters
     *            array of length 2 with lengths and angles (any order). MUST
     *            have both this list is what is normally returned by accessing
     *            the XOM
     * @param type
     *            of parameter
     * @return the cellParameter (or null if not found or corrupt
     *         cellParameters)
     */
    public static CMLCellParameter getCellParameter(
            List<CMLCellParameter> cellParameters, Type type) {
        CMLCellParameter cellParameter = null;
        if (cellParameters != null && cellParameters.size() == 2) {
            CMLCellParameter length = null;
            CMLCellParameter angle = null;
            for (CMLCellParameter cellP : cellParameters) {
                if (Type.LENGTH.s.equals(cellP.getType())) {
                    length = cellP;
                } else if (Type.ANGLE.s.equals(cellP.getType())) {
                    angle = cellP;
                } else if (cellP.getType() == null) {
                    throw new CMLRuntimeException(
                            "cellParameter requires type attribute");
                } else {
                    throw new CMLRuntimeException("unknown type on cellParameter: "
                            + cellP.getType());
                }
            }
            if (length != null && angle != null) {
                cellParameter = (Type.LENGTH.equals(type)) ? length : angle;
            }
        }
        return cellParameter;
    }

    /**
     * extracts cellParameter of given type.
     *
     * @param cellParameters
     *            array of length 2 with lengths and angles (any order). MUST
     *            have both this list is what is normally returned by accessing
     *            the XOM
     * @param type
     *            of parameter
     * @return the cellParameter (or null if not found or corrupt
     *         cellParameters)
     */
    public static CMLCellParameter getCellParameter(
            CMLElements<CMLCellParameter> cellParameters, Type type) {
        List<CMLCellParameter> cellParams = new ArrayList<CMLCellParameter>();
        for (CMLCellParameter cellParam : cellParameters) {
            cellParams.add(cellParam);
        }
        return getCellParameter(cellParams, type);
    }

    /**
     * gets CMLScalar representation from valid cellParameters.
     *
     * @param cellParameterElements
     *            must be exactly one each of length and angle
     * @return the 6 cell parameters in order a,b,c,alpha,beta,gamma
     */
    public static List<CMLScalar> createCMLScalars(
            List<CMLCellParameter> cellParameterElements) {
        List<CMLScalar> cellParams = new ArrayList<CMLScalar>();
        CMLCellParameter length = CMLCellParameter.getCellParameter(
                cellParameterElements, CMLCellParameter.Type.LENGTH);
        CMLCellParameter angle = CMLCellParameter.getCellParameter(
                cellParameterElements, CMLCellParameter.Type.ANGLE);
        if (length != null && angle != null) {
            double[] error = length.getError();
            for (int i = 0; i < 3; i++) {
                cellParams.add(CMLCrystal.createScalar(dictRef[i], length
                        .getXMLContent()[i], length.getUnits(),
                        (error == null) ? Double.NaN : error[i]));
            }
            error = angle.getError();
            for (int i = 3; i < 6; i++) {
                cellParams.add(CMLCrystal.createScalar(dictRef[i], angle
                        .getXMLContent()[i - 3], angle.getUnits(),
                        (error == null) ? Double.NaN : error[i - 3]));
            }
        }
        return cellParams;
    }

    /**
     * gets CMLScalar representation from valid cellParameters.
     *
     * @param cellParameterElements
     *            must be exactly one each of length and angle
     * @return the 6 cell parameters in order a,b,c,alpha,beta,gamma
     */
    public static List<CMLScalar> createCMLScalars(
            CMLElements<CMLCellParameter> cellParameterElements) {
        List<CMLCellParameter> cellParams = new ArrayList<CMLCellParameter>();
        for (CMLCellParameter param : cellParameterElements) {
            cellParams.add(param);
        }
        return createCMLScalars(cellParams);
    }
}
