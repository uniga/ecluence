/*******************************************************************************
 * Copyright (c) 2017 Uniga.
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

import dk.uniga.ecluence.core.matching.IdentifierPrefixPredicate;

public class IdentifierPrefixPredicateTest {

	@Test(expected = NullPointerException.class)
	public void testTestNullPrefix() throws Exception {
		new IdentifierPrefixPredicate(null);
	}
	
	@Test()
	public void testTestNullString() throws Exception {
		IdentifierPrefixPredicate p = new IdentifierPrefixPredicate("prefix");
		assertFalse(p.test(null));
	}

	@Test
	public void testTestNoMatchingPrefix() throws Exception {
		IdentifierPrefixPredicate p = new IdentifierPrefixPredicate("prefix");
		assertFalse(p.test("aprefix"));
	}

	@Test
	public void testTestNoIdentifier() throws Exception {
		IdentifierPrefixPredicate p = new IdentifierPrefixPredicate("prefix");
		assertFalse(p.test("prefix"));
	}

	@Test
	public void testTestMatchingPrefixMatchingIdentifier() throws Exception {
		IdentifierPrefixPredicate p = new IdentifierPrefixPredicate("prefix");
		p.setIdentifier("ab");
		assertTrue(p.test("prefixa"));
	}

	@Test
	public void testTestMatchingPrefixMatchingIdentifierSubstitution() throws Exception {
		IdentifierPrefixPredicate p = new IdentifierPrefixPredicate("prefix");
		p.setIdentifier("a//b");
		assertTrue(p.test("prefixa-b"));
	}

	@Test
	public void testTestLabelMatchingPrefixNoMatchingIdentifier() throws Exception {
		IdentifierPrefixPredicate p = new IdentifierPrefixPredicate("prefix");
		p.setIdentifier("bb");
		assertFalse(p.test("prefixa"));
	}
}

