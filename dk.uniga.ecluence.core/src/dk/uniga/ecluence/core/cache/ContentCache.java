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

import java.util.Collection;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.ContentUpdateListener;
import dk.uniga.ecluence.core.QueryException;

/**
 * Local cache of {@link ContentBean ContentBeans}. Calls to getters return any
 * pages contained locally in the cache. Notifies listeners when the cache is
 * refreshed with new or updated content.
 */
public interface ContentCache {

	/**
	 * Returns the type of use of the cache.
	 * 
	 * @return
	 */
	CacheContentType getType();

	/**
	 * Returns the name of the cache.
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * Returns the page with the given id. Returns <code>null</code> if no such page
	 * is contained in the cache; the page may exists and if so can be retrieved
	 * when the cache has been refreshed.
	 * 
	 * @param id
	 *            Unique identifier of page
	 * @return page or <code>null</code> if page not in cache
	 * @throws QueryException
	 * @throws NullPointerException if id is missing
	 */
	ContentBean get(String id) throws QueryException;

	/**
	 * Returns all pages in the cache.
	 * 
	 * @return Collection of pages
	 * @throws QueryException
	 */
	Collection<ContentBean> getAll() throws QueryException;

	/**
	 * Adds listener to be notified of updates to the cache; does nothing if the
	 * listener is already registered.
	 * 
	 * @param listener
	 */
	void addListener(ContentUpdateListener listener);

	/**
	 * Removes the given listener from the cache; does nothing if the listener is
	 * not registered.
	 * 
	 * @param listener
	 */
	void removeListener(ContentUpdateListener listener);

	/**
	 * Force a refresh of content in the cache. This calls blocks until the content
	 * has been updated.
	 * 
	 * @return number of pages updated in cache
	 * @throws QueryException
	 * @throws ContentStoreException 
	 */
	int refresh() throws QueryException, ContentStoreException;

}
