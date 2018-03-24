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
 * Describes a ContentCache and allows creating a cache using a given factory.
 */
public interface ContentCacheProvider {

	/**
	 * Returns the name of the cache returned by this provider.
	 * 
	 * @return name of cache
	 */
	String getName();

	/**
	 * Returns the type of content stored in the cache returned by this provider.
	 * 
	 * @return CacheContentType
	 */
	CacheContentType getCacheContentType();

	/**
	 * Returns a ContentCache created by the given ContentCacheFactory.
	 * 
	 * @param cacheFactory
	 * @return
	 * @throws QueryException
	 * @throws ContentStoreException
	 */
	ContentCache getCache(ContentCacheFactory cacheFactory) throws QueryException, ContentStoreException;

}
