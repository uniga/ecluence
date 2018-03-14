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

import java.util.Collection;

import dk.uniga.ecluence.core.QueryException;

/**
 * Finds and returns content that matches a given selection object.
 */
public interface ContentMatcher {
	/**
	 * Returns all content matching the given selection object.
	 * 
	 * @param selection
	 *            Selection object to match
	 * @return Collection of content matches.
	 * @throws QueryException
	 */
	Collection<ContentMatch> getMatches(Object selection) throws QueryException;
}
