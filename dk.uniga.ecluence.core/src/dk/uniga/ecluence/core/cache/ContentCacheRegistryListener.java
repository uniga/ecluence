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
 * Listens to new {@link ContentCacheProvider}s being registered.
 */
public interface ContentCacheRegistryListener {

	/**
	 * Notifies that the given ContentCacheProvider was added.
	 * 
	 * @param provider
	 * @throws QueryException
	 * @throws ContentStoreException
	 */
	void cacheProviderAdded(ContentCacheProvider provider) throws QueryException, ContentStoreException;

}
