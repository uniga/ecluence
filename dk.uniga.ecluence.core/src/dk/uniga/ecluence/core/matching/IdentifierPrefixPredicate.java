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

import org.apache.commons.lang3.StringUtils;

/**
 * Testing that a string matches a given prefix and if so, if the part of the
 * string following the prefix is the prefix of the given identifier. Always
 * evaluates to <code>false</code> if identifier is <code>null</code>.
 * 
 * Replaces all non-word characters in the identifier with dash (-), for example
 * 'dk.uniga.ecluence' matches 'dk-uniga-ecluence'.
 * 
 * For example:
 * 
 * <pre>
 * assertTrue(new IdentifierPrefixPredicate("code-project-prefix-")
 * 		.setIdentifier("client-api")
 * 		.test("code-project-prefix-client"));
 * </pre>
 */
public class IdentifierPrefixPredicate extends IdentifierComparisonPredicate {

	public IdentifierPrefixPredicate(String stringPrefix) {
		super((id, afterPrefix) -> StringUtils.startsWithIgnoreCase(id, afterPrefix), stringPrefix);
	}
}