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
 * string following the prefix matches the given identifier exactly. Always
 * evaluates to <code>false</code> if identifier is <code>null</code>.
 * 
 * Replaces all non-word characters in the identifier with dash (-), for example
 * 'dk.uniga.ecluence' matches 'dk-uniga-ecluence'.
 * 
 * For example:
 * 
 * <pre>
 * assertTrue(new IdentifierEqualsPredicate("code-project-")
 * 		.setIdentifier("client.api")
 * 		.test("code-project-client-api"));
 * </pre>
 */
public class IdentifierEqualsPredicate extends IdentifierComparisonPredicate {

	public IdentifierEqualsPredicate(String stringPrefix) {
		super((id, afterPrefix) -> StringUtils.equalsIgnoreCase(id, afterPrefix), stringPrefix);
	}
}
