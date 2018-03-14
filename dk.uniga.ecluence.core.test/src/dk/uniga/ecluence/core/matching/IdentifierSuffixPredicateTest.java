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

import dk.uniga.ecluence.core.matching.IdentifierSuffixPredicate;

public class IdentifierSuffixPredicateTest {

	@Test(expected = NullPointerException.class)
	public void testTestNullsuffix() throws Exception {
		new IdentifierSuffixPredicate(null);
	}

	@Test()
	public void testTestNullString() throws Exception {
		IdentifierSuffixPredicate p = new IdentifierSuffixPredicate("suffix");
		assertFalse(p.test(null));
	}

	@Test
	public void testTestNoMatchingSuffix() throws Exception {
		IdentifierSuffixPredicate p = new IdentifierSuffixPredicate("suffix");
		assertFalse(p.test("asuffix"));
	}

	@Test
	public void testTestNoIdentifier() throws Exception {
		IdentifierSuffixPredicate p = new IdentifierSuffixPredicate("suffix");
		assertFalse(p.test("suffix"));
	}

	@Test
	public void testTestMatchingSuffixMatchingIdentifier() throws Exception {
		IdentifierSuffixPredicate p = new IdentifierSuffixPredicate("suffix");
		p.setIdentifier("ab");
		assertTrue(p.test("suffixb"));
	}

	@Test
	public void testTestMatchingSuffixMatchingIdentifierSubstitution() throws Exception {
		IdentifierSuffixPredicate p = new IdentifierSuffixPredicate("suffix");
		p.setIdentifier("a//b");
		assertTrue(p.test("suffix-b"));
	}

	@Test
	public void testTestLabelMatchingSuffixNoMatchingIdentifier() throws Exception {
		IdentifierSuffixPredicate p = new IdentifierSuffixPredicate("suffix");
		p.setIdentifier("aa");
		assertFalse(p.test("suffixb"));
	}
}
