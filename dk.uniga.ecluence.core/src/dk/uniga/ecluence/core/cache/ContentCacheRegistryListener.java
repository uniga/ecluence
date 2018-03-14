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
