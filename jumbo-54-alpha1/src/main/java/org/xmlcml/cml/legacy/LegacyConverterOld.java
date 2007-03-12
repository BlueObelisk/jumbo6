package org.xmlcml.cml.legacy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import nu.xom.Document;

import org.xmlcml.cml.base.CMLRuntimeException;
import org.xmlcml.cml.element.CMLCml;

/**
 * converts legacy format to and from CML
 * because the legacy has so many forms, this is a general interface
 * some methods will be inappropriate for some legacy types and this
 * normally results in CMLRuntime 
 * 
 * some legacy types (e.g. CIF, SDF) can contain concatenated files
 * while others such as MOL cannot. The list methods will not
 * work for the latter. Similarly some types have a
 * legacyObject representation (CIF, etc.) 
 * 
 * this interface extends Iterator so that a large legacy file can be
 * parsed piece by piece.
 * 
 * @author pm286
 */
public interface LegacyConverterOld extends Iterator {

    /** sets parser-specific or converter-specific controls.
     * ideally the strings are defined as an enum in the specific
     * legacyConverter. 
     * @param controls
     * @throws CMLRuntimeException incorrect string
     */
    void setControls(String... controls) throws CMLRuntimeException;
    
    /** returns legacy parsing as a XOM Document.
     * the root element might be a single CMLElement or 
     * a container with a set of child CML Molecules.
     * precise form will depend on the indivdual legacy converter
     * @param r the input
     * @return the document
     * @throws IOException
     * @throws CMLRuntimeException
     */
    Document parseLegacy(Reader r) throws IOException, CMLRuntimeException;

    /** sets the reader before parsing.
     * needed if Iterator is to be used 
     * @param r the input
     */
    void setReader(Reader r);

    /** returns legacy parsing as a XOM Document.
     * the root element might be a single CMLElement or 
     * a container with a set of child CML Molecules.
     * precise form will depend on the indivdual legacy converter
     * @param is the input
     * @return the document
     * @throws IOException
     * @throws CMLRuntimeException
     */
    Document parseLegacy(InputStream is) throws IOException, CMLRuntimeException;

    /** gets Document after parseLegacy().
     * has a root CMLCml element which either has CMLCml children
     * corresponding to a list or is a single CMLCml element
     * @return the document (null if no parse)
     */
    Document getDocument();
    
    /** gets the legacy information as an object. 
     * this option may be available for complex legacy formats such as 
     * CIF or ChemDraw but others may return null
     * @return the object or null.
     */
    Object getLegacyObject();
    
    /** gets the legacy information as a list of legacyObjects. 
     * this option may be available for complex legacy formats such as 
     * CIF or ChemDraw but others may return null
     * @return the object or null.
     */
    List getLegacyObjectList();
    
    /** gets the CML information as a list of CMLCml. 
     * this option may be available for complex legacy formats such as 
     * CIF or ChemDraw but others may return null
     * @return the object or null.
     */
    List<CMLCml> getCMLCmlList();
    
    /** sets CMLCml element for conversion.
     * required before writing legacy
     * @param cml element comtaining info to be converted
     */
    void setCMLCml(CMLCml cml);

    /** writes legacy to output.
     * 
     * @param os
     * @throws IOException
     * @throws CMLRuntimeException
     */
    void writeLegacy(OutputStream os) throws IOException, CMLRuntimeException;
}
