package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IdentifierEqualsPredicateTest {

	@Test(expected = NullPointerException.class)
	public void testNullPrefixFails() throws Exception {
		new IdentifierEqualsPredicate(null);
	}

	@Test
	public void testMatching() throws Exception {
		// matching is not case sensitive
		assertTrue(new IdentifierEqualsPredicate("prefix").setIdentifier("A").test("prefixa"));
		// false if not matching entire string
		assertFalse(new IdentifierEqualsPredicate("prefix").setIdentifier("1").test("prefix123"));
		assertFalse(new IdentifierEqualsPredicate("prefix").setIdentifier("3").test("prefix123"));
		assertTrue(new IdentifierEqualsPredicate("prefix").setIdentifier("1./2").test("prefix1-2"));
		// prefix characters do not get replaced
		assertTrue(new IdentifierEqualsPredicate("a./b").setIdentifier("1./2").test("a./b1-2"));
	}
}
