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

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.ContentUpdateListener;
import dk.uniga.ecluence.core.QueryException;

/**
 * {@link ContentCache} that keeps local copies of {@link ContentBean
 * ContentBeans} in a {@link ContentStoreImpl} and fetches content as needed
 * using a {@link ContentSearcher}. Keeps track of the last time the cache was
 * updated with search results.
 * 
 * Content is fetched from a search in non-blocking calls and listeners are
 * notified of updated content.
 */
public final class ContentCacheImpl implements ContentCache {

	private final CacheContentType type;

	private final String name;
	
	private final FetchingContentStore fetchingContentStore;
	
	private final ContentQuery query;
	
	private final Map<String, ContentBean> contentById = new HashMap<>();
	
	private final Collection<ContentUpdateListener> listeners = new HashSet<>();

	/**
	 * 
	 * @param type 
	 * @param name
	 * @param lastFetchingTime
	 * @param contentSearcher
	 * @param query
	 * @param contentExpander
	 * @param fetchingContentStore 
	 * @throws QueryException
	 * @throws ContentStoreException 
	 */
	public ContentCacheImpl(CacheContentType type, String name, ContentQuery query, 
			FetchingContentStore fetchingContentStore) throws QueryException, ContentStoreException {
		this.type = requireNonNull(type);
		this.name = requireNonNull(name);
		this.query = requireNonNull(query);
		this.fetchingContentStore = requireNonNull(fetchingContentStore);
		refreshFromStore();
		fetchingContentStore.addListener((pages) -> update(pages));
		fetchingContentStore.fetch();
	}

	@Override
	public CacheContentType getType() {
		return type;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public ContentBean get(String id) {
		requireNonNull(id);
		fetchingContentStore.fetch();
		return contentById.get(id);
	}

	@Override
	public Collection<ContentBean> getAll() {
		fetchingContentStore.fetch();
		return contentById.values();
	}

	@Override
	public int refresh() throws QueryException, ContentStoreException {
		return fetchingContentStore.fetch(true);
	}

	private void refreshFromStore() throws ContentStoreException {
		Collection<ContentBean> results = fetchingContentStore.getAll();
		results.stream().filter(c -> query.matches(c)).forEach(c -> {
			contentById.put(c.getId(), c);
		});
	}

	private void update(Collection<ContentBean> pages) {
		pages.forEach((page) -> {
			contentById.put(page.getId(), page);
		});
		notifyListeners(pages);
	}

	private void notifyListeners(Collection<ContentBean> pages) {
		for (ContentUpdateListener listener : listeners) {
			listener.contentUpdated(pages);
		}
	}

	@Override
	public void addListener(ContentUpdateListener listener) {
		listeners.add(requireNonNull(listener));
	}

	@Override
	public void removeListener(ContentUpdateListener listener) {
		listeners.remove(requireNonNull(listener));
	}
}
