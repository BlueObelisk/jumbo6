/**
 */
package org.xmlcml.cml.tools;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;

/**
 * @author pm286
 *
 */
public class ReactionTransformToolTest {

	private static String transformS = "" +
			"<reaction id='rr1' xmlns='http://www.xml-cml.org/schema'>" +
			"  <reactantList>" +
			"    <reactant id='r1'>" +
			"      <molecule id='m1'>" +
			"        <atomArray>" +
			"          <atom id='a11' elementType='C'/>" +
			"          <atom id='a12' elementType='O'/>" +
			"          <atom id='a13' elementType='Cl'/>" +
			"        </atomArray>" +
			"        <bondArray>" +
			"          <bond atomRefs2='a11 a12'/>" +
			"          <bond atomRefs2='a12 a13'/>" +
			"        </bondArray>" +
			"      </molecule>" +
			"    </reactant>" +
			"    <reactant id='r2'>" +
			"      <molecule id='m2'>" +
			"        <atomArray>" +
			"          <atom id='a21' elementType='C'/>" +
			"          <atom id='a22' elementType='S'/>" +
			"          <atom id='a23' elementType='Br'/>" +
			"        </atomArray>" +
			"        <bondArray>" +
			"          <bond atomRefs2='a21 a22'/>" +
			"          <bond atomRefs2='a22 a23'/>" +
			"        </bondArray>" +
			"      </molecule>" +
			"    </reactant>" +
			"  </reactantList>" +
			"  <productList>" +
			"    <product id='r1'>" +
			"      <molecule id='m1'>" +
			"        <atomArray>" +
			"          <atom id='a11' elementType='C'/>" +
			"          <atom id='a23' elementType='Br'/>" +
			"        </atomArray>" +
			"        <bondArray>" +
			"          <bond atomRefs2='a11 a23'/>" +
			"        </bondArray>" +
			"      </molecule>" +
			"    </product>" +
			"    <product id='r2'>" +
			"      <molecule id='m2'>" +
			"        <atomArray>" +
			"          <atom id='a21' elementType='C'/>" +
			"          <atom id='a22' elementType='S'/>" +
			"          <atom id='a12' elementType='O'/>" +
			"          <atom id='a13' elementType='Cl'/>" +
			"        </atomArray>" +
			"        <bondArray>" +
			"          <bond atomRefs2='a21 a22'/>" +
			"          <bond atomRefs2='a22 a12'/>" +
			"          <bond atomRefs2='a12 a13'/>" +
			"        </bondArray>" +
			"      </molecule>" +
			"    </product>" +
			"  </productList>" +
			"</reaction>";
	CMLReaction transform;
	
	/**
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		transform = (CMLReaction) new CMLBuilder().parseString(transformS);
	}
	/**
	 * Test method for {@link org.xmlcml.cml.tools.ReactionTransformTool#ReactionTransformTool(org.xmlcml.cml.element.CMLReaction)}.
	 */
	@Test
    @Ignore
	public final void testReactionTransformTool() {
//		ReactionTool transformTool = new ReactionTool(transform);
		CMLReactantList reactantList = transform.getReactantList();
		CMLElements reactants = reactantList.getReactantElements();
		Assert.assertEquals("reactant count", 2, reactants.size());
		CMLElements products = transform.getProductList().getProductElements();
		Assert.assertEquals("product count", 2, products.size());
		CMLFormula productFormula = ReactionTool.getAggregateFormula(reactantList);
		productFormula.debug();
	}

	/**
	 * Test method for {@link org.xmlcml.cml.tools.ReactionTransformTool#addReactantList(org.xmlcml.cml.element.CMLReactantList, java.util.List)}.
	 */
	@Test
    @Ignore
	public final void testAddReactantList() {
		fail("Not yet implemented"); // TODO
	}

}
