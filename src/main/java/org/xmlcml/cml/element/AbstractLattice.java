package org.xmlcml.cml.element;


import nu.xom.*;

import org.xmlcml.cml.base.*;
import org.xmlcml.cml.attribute.*;

// end of part 1
/** CLASS DOCUMENTATION */
public abstract class AbstractLattice extends CMLElement {
    /** local name*/
    public final static String TAG = "lattice";
    /** constructor. */    public AbstractLattice() {
        super("lattice");
    }
/** copy constructor.
* deep copy using XOM copy()
* @param old element to copy
*/
    public AbstractLattice(AbstractLattice old) {
        super((CMLElement) old);
    }
// attribute:   title

    /** cache */
    StringSTAttribute _att_title = null;
    /** A title on an element.
    * No controlled value.
    * @return CMLAttribute
    */
    public CMLAttribute getTitleAttribute() {
        return (CMLAttribute) getAttribute("title");
    }
    /** A title on an element.
    * No controlled value.
    * @return String
    */
    public String getTitle() {
        StringSTAttribute att = (StringSTAttribute) this.getTitleAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** A title on an element.
    * No controlled value.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setTitle(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_title == null) {
            _att_title = (StringSTAttribute) attributeFactory.getAttribute("title", "lattice");
            if (_att_title == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : title probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_title);
        super.addRemove(att, value);
    }
// attribute:   id

    /** cache */
    IdAttribute _att_id = null;
    /** null
    * @return CMLAttribute
    */
    public CMLAttribute getIdAttribute() {
        return (CMLAttribute) getAttribute("id");
    }
    /** null
    * @return String
    */
    public String getId() {
        IdAttribute att = (IdAttribute) this.getIdAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** null
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setId(String value) throws CMLRuntimeException {
        IdAttribute att = null;
        if (_att_id == null) {
            _att_id = (IdAttribute) attributeFactory.getAttribute("id", "lattice");
            if (_att_id == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : id probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new IdAttribute(_att_id);
        super.addRemove(att, value);
    }
// attribute:   convention

    /** cache */
    StringSTAttribute _att_convention = null;
    /** A reference to a convention.
    * There is no controlled vocabulary for conventions, but the author must ensure that the semantics are openly available and that there are mechanisms for implementation. The convention is inherited by all the subelements, 
    * so that a convention for molecule would by default extend to its bond and atom children. This can be overwritten
    *     if necessary by an explicit convention.
    *                     It may be useful to create conventions with namespaces (e.g. iupac:name).
    *     Use of convention will normally require non-STMML semantics, and should be used with
    *     caution. We would expect that conventions prefixed with "ISO" would be useful,
    *     such as ISO8601 for dateTimes.
    *                     There is no default, but the conventions of STMML or the related language (e.g. CML) will be assumed.
    * @return CMLAttribute
    */
    public CMLAttribute getConventionAttribute() {
        return (CMLAttribute) getAttribute("convention");
    }
    /** A reference to a convention.
    * There is no controlled vocabulary for conventions, but the author must ensure that the semantics are openly available and that there are mechanisms for implementation. The convention is inherited by all the subelements, 
    * so that a convention for molecule would by default extend to its bond and atom children. This can be overwritten
    *     if necessary by an explicit convention.
    *                     It may be useful to create conventions with namespaces (e.g. iupac:name).
    *     Use of convention will normally require non-STMML semantics, and should be used with
    *     caution. We would expect that conventions prefixed with "ISO" would be useful,
    *     such as ISO8601 for dateTimes.
    *                     There is no default, but the conventions of STMML or the related language (e.g. CML) will be assumed.
    * @return String
    */
    public String getConvention() {
        StringSTAttribute att = (StringSTAttribute) this.getConventionAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** A reference to a convention.
    * There is no controlled vocabulary for conventions, but the author must ensure that the semantics are openly available and that there are mechanisms for implementation. The convention is inherited by all the subelements, 
    * so that a convention for molecule would by default extend to its bond and atom children. This can be overwritten
    *     if necessary by an explicit convention.
    *                     It may be useful to create conventions with namespaces (e.g. iupac:name).
    *     Use of convention will normally require non-STMML semantics, and should be used with
    *     caution. We would expect that conventions prefixed with "ISO" would be useful,
    *     such as ISO8601 for dateTimes.
    *                     There is no default, but the conventions of STMML or the related language (e.g. CML) will be assumed.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setConvention(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_convention == null) {
            _att_convention = (StringSTAttribute) attributeFactory.getAttribute("convention", "lattice");
            if (_att_convention == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : convention probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_convention);
        super.addRemove(att, value);
    }
// attribute:   dictRef

    /** cache */
    DictRefAttribute _att_dictref = null;
    /** null
    * @return CMLAttribute
    */
    public CMLAttribute getDictRefAttribute() {
        return (CMLAttribute) getAttribute("dictRef");
    }
    /** null
    * @return String
    */
    public String getDictRef() {
        DictRefAttribute att = (DictRefAttribute) this.getDictRefAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** null
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setDictRef(String value) throws CMLRuntimeException {
        DictRefAttribute att = null;
        if (_att_dictref == null) {
            _att_dictref = (DictRefAttribute) attributeFactory.getAttribute("dictRef", "lattice");
            if (_att_dictref == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : dictRef probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new DictRefAttribute(_att_dictref);
        super.addRemove(att, value);
    }
// attribute:   latticeType

    /** cache */
    StringSTAttribute _att_latticetype = null;
    /** The primitivity of a lattice.
    * No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency between spacegroups, symmetry operators, etc.
    * @return CMLAttribute
    */
    public CMLAttribute getLatticeTypeAttribute() {
        return (CMLAttribute) getAttribute("latticeType");
    }
    /** The primitivity of a lattice.
    * No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency between spacegroups, symmetry operators, etc.
    * @return String
    */
    public String getLatticeType() {
        StringSTAttribute att = (StringSTAttribute) this.getLatticeTypeAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** The primitivity of a lattice.
    * No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency between spacegroups, symmetry operators, etc.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setLatticeType(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_latticetype == null) {
            _att_latticetype = (StringSTAttribute) attributeFactory.getAttribute("latticeType", "lattice");
            if (_att_latticetype == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : latticeType probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_latticetype);
        super.addRemove(att, value);
    }
// attribute:   spaceType

    /** cache */
    StringSTAttribute _att_spacetype = null;
    /** The spaceType of the lattice.
    * Usually real or reciprocal. No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency for unitTypes, etc.
    * @return CMLAttribute
    */
    public CMLAttribute getSpaceTypeAttribute() {
        return (CMLAttribute) getAttribute("spaceType");
    }
    /** The spaceType of the lattice.
    * Usually real or reciprocal. No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency for unitTypes, etc.
    * @return String
    */
    public String getSpaceType() {
        StringSTAttribute att = (StringSTAttribute) this.getSpaceTypeAttribute();
        if (att == null) {
            return null;
        }
        return att.getString();
    }
    /** The spaceType of the lattice.
    * Usually real or reciprocal. No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency for unitTypes, etc.
    * @param value title value
    * @throws CMLRuntimeException attribute wrong value/type
    */
    public void setSpaceType(String value) throws CMLRuntimeException {
        StringSTAttribute att = null;
        if (_att_spacetype == null) {
            _att_spacetype = (StringSTAttribute) attributeFactory.getAttribute("spaceType", "lattice");
            if (_att_spacetype == null) {
                throw new CMLRuntimeException("BUG: cannot process attributeGroupName : spaceType probably incompatible attributeGroupName and attributeName");
            }
        }
        att = new StringSTAttribute(_att_spacetype);
        super.addRemove(att, value);
    }
// element:   scalar

    /** The spaceType of the lattice.
    * Usually real or reciprocal. No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency for unitTypes, etc.
    * @param scalar child to add
    */
    public void addScalar(AbstractScalar scalar) {
        scalar.detach();
        this.appendChild(scalar);
    }
    /** The spaceType of the lattice.
    * Usually real or reciprocal. No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency for unitTypes, etc.
    * @return CMLElements<CMLScalar>
    */
    public CMLElements<CMLScalar> getScalarElements() {
        Elements elements = this.getChildElements("scalar", CML_NS);
        return new CMLElements<CMLScalar>(elements);
    }
// element:   latticeVector

    /** The spaceType of the lattice.
    * Usually real or reciprocal. No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency for unitTypes, etc.
    * @param latticeVector child to add
    */
    public void addLatticeVector(AbstractLatticeVector latticeVector) {
        latticeVector.detach();
        this.appendChild(latticeVector);
    }
    /** The spaceType of the lattice.
    * Usually real or reciprocal. No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency for unitTypes, etc.
    * @return CMLElements<CMLLatticeVector>
    */
    public CMLElements<CMLLatticeVector> getLatticeVectorElements() {
        Elements elements = this.getChildElements("latticeVector", CML_NS);
        return new CMLElements<CMLLatticeVector>(elements);
    }
// element:   matrix

    /** The spaceType of the lattice.
    * Usually real or reciprocal. No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency for unitTypes, etc.
    * @param matrix child to add
    */
    public void addMatrix(AbstractMatrix matrix) {
        matrix.detach();
        this.appendChild(matrix);
    }
    /** The spaceType of the lattice.
    * Usually real or reciprocal. No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency for unitTypes, etc.
    * @return CMLElements<CMLMatrix>
    */
    public CMLElements<CMLMatrix> getMatrixElements() {
        Elements elements = this.getChildElements("matrix", CML_NS);
        return new CMLElements<CMLMatrix>(elements);
    }
// element:   symmetry

    /** The spaceType of the lattice.
    * Usually real or reciprocal. No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency for unitTypes, etc.
    * @param symmetry child to add
    */
    public void addSymmetry(AbstractSymmetry symmetry) {
        symmetry.detach();
        this.appendChild(symmetry);
    }
    /** The spaceType of the lattice.
    * Usually real or reciprocal. No default. The semantics of this are software-dependent (i.e. this Schema does not check for consistency for unitTypes, etc.
    * @return CMLElements<CMLSymmetry>
    */
    public CMLElements<CMLSymmetry> getSymmetryElements() {
        Elements elements = this.getChildElements("symmetry", CML_NS);
        return new CMLElements<CMLSymmetry>(elements);
    }
    /** overrides addAttribute(Attribute)
     * reroutes calls to setFoo()
     * @param att  attribute
    */
    public void addAttribute(Attribute att) {
        String name = att.getLocalName();
        String value = att.getValue();
        if (name == null) {
        } else if (name.equals("title")) {
            setTitle(value);
        } else if (name.equals("id")) {
            setId(value);
        } else if (name.equals("convention")) {
            setConvention(value);
        } else if (name.equals("dictRef")) {
            setDictRef(value);
        } else if (name.equals("latticeType")) {
            setLatticeType(value);
        } else if (name.equals("spaceType")) {
            setSpaceType(value);
	     } else {
            super.addAttribute(att);
        }
    }
}
