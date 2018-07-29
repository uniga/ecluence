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

import java.util.function.Predicate;

/**
 * Tests a string against another identifier string.
 */
public interface IdentifierPredicate extends Predicate<String> {

	/**
	 * Sets identifier to match prefix.
	 * 
	 * @param identifier identifier to match or <code>null</code>
	 * @return this {@link IdentifierPredicate}
	 */
	IdentifierPredicate setIdentifier(String identifier);
	
}
