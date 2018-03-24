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
package dk.uniga.ecluence.core.cache;

import dk.uniga.ecluence.core.QueryException;

/**
 * Factory for constructing a ContentCache.
 */
public interface ContentCacheFactory {

	/**
	 * Creates a ContentCache for the given type of content and using the given
	 * contentQuery.
	 * 
	 * @param cacheType type of content in the cache
	 * @param cacheName name of the cache
	 * @param contentQuery the query to use for querying content
	 * @return
	 * @throws QueryException
	 * @throws ContentStoreException
	 */
	public ContentCache createCache(CacheContentType cacheType, String cacheName, ContentQuery contentQuery) throws QueryException, ContentStoreException;

}
