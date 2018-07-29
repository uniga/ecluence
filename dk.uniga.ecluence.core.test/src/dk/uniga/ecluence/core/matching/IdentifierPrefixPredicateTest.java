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

public class IdentifierPrefixPredicateTest {

	@Test
	public void testMatching() throws Exception {
		// note that this matches a string with nothing but the prefix
		assertTrue(new IdentifierPrefixPredicate("prefix").setIdentifier("abc").test("prefix"));
		// matching is not case sensitive
		assertTrue(new IdentifierPrefixPredicate("prefix").setIdentifier("abc").test("prefixA"));
		// false if not matching prefix
		assertFalse(new IdentifierPrefixPredicate("prefix").setIdentifier("123").test("prefix3"));
		assertTrue(new IdentifierPrefixPredicate("prefix").setIdentifier("1./23").test("prefix1-2"));
		// prefix characters do not get replaced
		assertTrue(new IdentifierPrefixPredicate("a./b").setIdentifier("1./23").test("a./b1-2"));
	}
}
