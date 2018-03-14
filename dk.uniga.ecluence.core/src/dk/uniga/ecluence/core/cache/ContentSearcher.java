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
package dk.uniga.ecluence.core.cache;

import java.time.LocalDateTime;
import java.util.List;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.cql.SearchResultEntry;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.SearchContentException;

/**
 * Searches for matching {@link ContentBean ContentBeans}, which are returned
 * wrapped in {@link SearchResultEntry} objects.
 */
public interface ContentSearcher {

	/**
	 * Searches for content created or modified since the given time. Calls to this
	 * method can be expected to block the thread.
	 * 
	 * @param time
	 *            the time since which content should have been created or modified
	 *            in order to be returned
	 * @return List of search result entries.
	 * @throws QueryException
	 */
	List<SearchResultEntry> searchSince(LocalDateTime time) throws SearchContentException;

	/**
	 * Searches for all matching content. Calls to this method can be expected to
	 * block the thread.
	 * 
	 * @return List of search result entries.
	 * @throws QueryException
	 */
	List<SearchResultEntry> search() throws QueryException;

	/**
	 * Creates a hash code for this searcher. If the hash of two searchers are
	 * different, they can be expected to return different results.
	 */
	String getHash();

}
