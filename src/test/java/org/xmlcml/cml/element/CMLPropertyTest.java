package org.xmlcml.cml.element;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
/** tests property
 * 
 * @author pm286
 *
 */
public class CMLPropertyTest extends AbstractTest {

	CMLProperty prop1;
	/** 
	 * @exception Exception
	 */
	@Before
	public void setUp() throws Exception {
		String prop1S = 
			"<property "+CML_XMLNS+">" +
			"  <scalar dictRef='foo:bar' units='units:g' dataType='xsd:double'>12.3</scalar>" +
			"</property>";
		prop1 = (CMLProperty) parseValidString(prop1S);
	}

	/** */
	@Test
	@Ignore
	public final void testGetPropertyList() {
		String cmlS = 
			"<cml " +CML_XMLNS+">"+
			"<property>" +
			"  <scalar dictRef='foo:bar' units='units:g' dataType='xsd:double'>12.3</scalar>" +
			"</property>"+
			"<property "+CML_XMLNS+">" +
			"  <scalar dictRef='foo:plugh' units='units:g' dataType='xsd:double'>45.6</scalar>" +
			"</property>" +
			"<property "+CML_XMLNS+">" +
			"  <scalar dictRef='foo:plugh' units='units:g' dataType='xsd:double'>49.6</scalar>" +
			"</property>" +
			"</cml>";
		CMLCml cml = (CMLCml) parseValidString(cmlS);
		CMLPropertyList propertyList = CMLProperty.getPropertyList(cml, "foo:bar");
		Assert.assertEquals("propertyList", 3, propertyList.getPropertyElements().size());
	}

	/** */
	@Test
	@Ignore
	public final void testGetPropertyCMLElementString() {
		fail("Not yet implemented"); // TODO
	}

	/** */
	@Test
	public final void testCanonicalize() {
		CMLProperty prop2 = new CMLProperty(prop1);
		prop2.canonicalize();
		prop2.debug();
	}

	/** */
	@Test
	public final void testGetUnits() {
		String units = prop1.getUnits();
		Assert.assertEquals("units", CMLUnit.Units.GRAM.value, units);
	}

	/** */
	@Test
	@Ignore
	public final void testGetDouble() {
		fail("Not yet implemented"); // TODO
	}

	/** */
	@Test
	@Ignore
	public final void testGetString() {
		fail("Not yet implemented"); // TODO
	}

	/** */
	@Test
	@Ignore
	public final void testGetInt() {
		fail("Not yet implemented"); // TODO
	}

	/** */
	@Test
	@Ignore
	public final void testGetStringValues() {
		fail("Not yet implemented"); // TODO
	}

	/** */
	@Test
	@Ignore
	public final void testGetInts() {
		fail("Not yet implemented"); // TODO
	}

	/** */
	@Test
	@Ignore
	public final void testGetDoubles() {
		fail("Not yet implemented"); // TODO
	}

	/** */
	@Test
	@Ignore
	public final void testGetChild() {
		fail("Not yet implemented"); // TODO
	}

	/** */
	@Test
	@Ignore
	public final void testSetChild() {
		fail("Not yet implemented"); // TODO
	}

	/** */
	@Test
	@Ignore
	public final void testGetDataType() {
		fail("Not yet implemented"); // TODO
	}

}
