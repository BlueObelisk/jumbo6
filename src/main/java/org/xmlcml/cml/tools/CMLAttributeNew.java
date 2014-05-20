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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xmlcml.cml.tools;

import org.xmlcml.util.CMLUtilNew;
import org.xmlcml.util.JumboDouble;

/**
 *
 * @author pm286
 */
    public abstract class CMLAttributeNew
    {
        // don't care about count of tokens in attribute
        public final static int ANYCOUNT = -1;

        // all attributes
        // atom
        public final static String ELEMENTTYPE = "elementType";

        public final static String ELEMENTTYPE_PATTERN =
            "H|He|Li|Be|B|C|N|O|F|Ne|Na|Mg|Al|Si|P|S|Cl|Ar|K|Ca|Sc|Ti|V|Cr|Mn|Fe|Co|Ni|Cu|Zn|Ga|Ge|As|Se|Br|Kr|Rb|Sr|Y|Zr|Nb|Mo|Tc|Ru|Rh|Pd|Ag|Cd|In|Sn|Sb|Te|I|Xe|Cs|Ba|La|Ce|Pr|Nd|Pm|Sm|Eu|Gd|Tb|Dy|Ho|Er|Tm|Yb|LuHf|Ta|W|Re|Os|Ir|Pt|Au|Hg|Tl|Pb|Bi|Po|At|Rn|Fr|Ra|Ac|Th|Pa|U|Np|Pu|Am|Cm|Bk|Cf|Es|Fm|Md|No|Xx|Rf|Db|Sg|Bh|Hs|Mt|Ds|Rg|Uub|Uut|Uuq|Uup|Uuh|Uus|Uuo";

        public final static String ID = "id";
        public final static String ID_PATTERN = "[A-Za-z_][A-Za-z0-9_]*";

        public final static String X2 = "x2";
        public final static String Y2 = "y2";
        public final static String X3 = "x3";
        public final static String Y3 = "y3";
        public final static String Z3 = "y3";
        public final static double COORD_MIN = - CMLUtilNew.DOUBLE_MAX_VALUE;
        public final static double COORD_MAX = CMLUtilNew.DOUBLE_MAX_VALUE;

        public final static String ISOTOPENUMBER = "isotopeNumber";
        public final static int ISOTOPENUMBER_MIN = 0;
        public final static int ISOTOPENUMBER_MAX = CMLUtilNew.INTEGER_MAX_VALUE;

        // bond
        public final static String ATOMREFS2 = "atomRefs2";
        public final static int ATOMREFS2_COUNT = 2;
        public final static String ATOMREFS2_PATTERN = ID_PATTERN;

        public final static String ORDER = "order";
        public final static String ORDER_PATTERN = "1|S|2|D|3|T|A";

        //cml
        public final static String VERSION = "version";

        // dictionary
        public final static String NAMESPACE = "namespace";

        // entry
        public final static String TERM = "term";

        // formula
        public final static String REF = "ref";
        public final static String CHIRALITY = "chirality";

        public final static String CONCISE = "concise";
        public final static String CONCISE_PATTERN = "([A-Z][a-z]? [0-9]+(\\.[0-9])?)+( -?\\d)?";

        public final static String INLINE = "inline";

        // molecule

        // peak
        public final static String XVALUE = "xValue";
        public final static String YVALUE = "yValue";
        public final static String XMAX = "xMax";
        public final static String XMIN = "xMin";

        public final static String PEAKMULTIPLICITY = "peakMultiplicity";
        public final static String PEAKMULTIPLICITY_PATTERN = null;

        // peakList
        public final static String XUNITS = "xUnits";
        public final static String YUNITS = "yUnits";

        // peakStructure
        public final static String PEAKSHAPE = "peakShape";
        public final static String PEAKSHAPE_PATTERN = null;

        // scalar
        public final static String UNITS = "units";
        public final static String MIN = "min";
        public final static String MAX = "max";

        // atomParity & bondStereo
        public final static String ATOMREFS4 = "atomRefs4";
        public final static int ATOMREFS4_COUNT = 4;
        public final static String ATOMREFS4_PATTERN = ID_PATTERN;

        // formula & molecule
        public final static String COUNT = "count";
        public final static double COUNT_MIN = 0.0;
        public final static double COUNT_MAX = JumboDouble.NaN;

        // formalCharge
        public final static String FORMALCHARGE = "formalCharge";
        public final static int FORMALCHARGE_MIN = - CMLUtilNew.INTEGER_MAX_VALUE;
        public final static int FORMALCHARGE_MAX = CMLUtilNew.INTEGER_MAX_VALUE;

        // hydrogenCount
        public final static String HYDROGENCOUNT = "hydrogenCount";

        // general
        public final static String QNAME_PATTERN = "[A-Za-z_][A-Za-z0-9_]*:[A-Za-z_][A-Za-z0-9_]*";


        public final static String DICTREF = "dictRef";
        public final static String DICTREF_PATTERN = QNAME_PATTERN;

        public final static String CONVENTION = "convention";
        public final static String CONVENTION_PATTERN = QNAME_PATTERN;

        public final static String TITLE = "title";
        public final static String TITLE_PATTERN = null;

        public final static String ROLE = "role";
        public final static String ROLE_PATTERN = null;

        public final static String SPINMULTIPLICITY = "spinMultiplicity";
        public final static int SPINMULTIPLICITY_MIN = 0;
        public final static int SPINMULTIPLICITY_MAX = CMLUtilNew.INTEGER_MAX_VALUE;

        public final static String ATOMREFS = "atomRefs";
        public final static int ATOMREFS_COUNT = ANYCOUNT;
        public final static String ATOMREFS_PATTERN = ID_PATTERN;

        public final static String BONDREFS = "bondRefs";
        public final static int BONDREFS_COUNT = ANYCOUNT;
        public final static String BONDREFS_PATTERN = ID_PATTERN;

        public final static String DATATYPE = "dataType";
        public final static String DATATYPE_PATTERN = QNAME_PATTERN;

        public final static String VALUE = "value";
        // ... not complete


    }
