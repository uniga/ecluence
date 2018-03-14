package dk.uniga.ecluence.core.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import dk.uniga.ecluence.core.QueryException;

/**
 * Registry of {@link ContentCacheProvider}s that notifies listeners of changes.
 */
public class ContentCacheRegistry {
	
	private final Set<ContentCacheProvider> providers = new HashSet<>();
	
	private final Set<ContentCacheRegistryListener> listeners = new HashSet<>();
	
	public void addProvider(ContentCacheProvider provider) throws QueryException, ContentStoreException {
		if (providers.add(provider))
			notifyAdded(provider);
	}

	public void removeProvider(ContentCacheProvider provider) throws QueryException, ContentStoreException {
		if (providers.remove(provider))
			notifyRemoved(provider);
	}

	public Collection<ContentCacheProvider> getProviders() {
		return Collections.unmodifiableSet(providers);
	}
	
	public void addListener(ContentCacheRegistryListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ContentCacheRegistryListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyAdded(ContentCacheProvider provider) throws QueryException, ContentStoreException {
		for (ContentCacheRegistryListener listener : listeners) {
			listener.cacheProviderAdded(provider);
		}
	}
	
	private void notifyRemoved(ContentCacheProvider provider) throws QueryException, ContentStoreException {
	}
}
