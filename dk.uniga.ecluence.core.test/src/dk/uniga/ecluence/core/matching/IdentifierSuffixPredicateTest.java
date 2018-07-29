/*******************************************************************************
 * Copyright (c) 2017, 2018 Uniga.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mikkel R. Jakobsen - initial API and implementation
 *******************************************************************************/
package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IdentifierSuffixPredicateTest {

	@Test(expected = NullPointerException.class)
	public void testNullPrefixFails() throws Exception {
		new IdentifierSuffixPredicate(null);
	}

	@Test
	public void testMatching() throws Exception {
		// note that this matches a string with nothing but the prefix
		assertTrue(new IdentifierSuffixPredicate("prefix").setIdentifier("abc").test("prefix"));
		// matching is not case sensitive
		assertTrue(new IdentifierSuffixPredicate("prefix").setIdentifier("abc").test("prefixC"));
		// false if not matching prefix
		assertFalse(new IdentifierSuffixPredicate("prefix").setIdentifier("123").test("prefix1"));
		assertTrue(new IdentifierSuffixPredicate("prefix").setIdentifier("1./23").test("prefix23"));
		// prefix characters do not get replaced
		assertTrue(new IdentifierSuffixPredicate("a./b").setIdentifier("1./23").test("a./b3"));
	}
}
