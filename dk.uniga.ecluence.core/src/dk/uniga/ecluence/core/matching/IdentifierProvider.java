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

import java.util.Optional;

/**
 * Provides an identifier for a given object.
 */
public interface IdentifierProvider {

	/**
	 * Returns an identifier for the given object.
	 * @param o
	 * @return
	 */
	Optional<String> getIdentifier(Object o);

	/**
	 * Returns a description of the given object if it can be identified.
	 * 
	 * @param o
	 * @return Optional {@link SelectionDescription} which is guaranteed present if
	 *         {@link #getIdentifier(Object)} return value is present for the same
	 *         argument
	 */
	Optional<SelectionDescription> getSelectionDescription(Object o);

}
