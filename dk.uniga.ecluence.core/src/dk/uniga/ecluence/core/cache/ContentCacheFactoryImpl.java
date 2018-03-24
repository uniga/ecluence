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

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.client.ContentClient;
import de.itboehmer.confluence.rest.client.SearchClient;
import dk.uniga.ecluence.core.PreferenceBackedStateStore;
import dk.uniga.ecluence.core.QueryException;

/**
 * {@link ContentCacheFactory} that creates {@link ContentCacheImpl} with
 * information about the query to use for fetching content, the content fields
 * to expand on Confluence pages (using a {@link ContentExpander}), and where to
 * store them in a {@link ContentStore}.
 * 
 * Upon creation of a cache, its stored contents get invalidated if its
 * configuration has changed (e.g., if the query used to fetch content has added
 * a label), thus requiring a full fetch of the contents.
 */
public class ContentCacheFactoryImpl implements ContentCacheFactory {

	private static final Logger log = LoggerFactory.getLogger(ContentCacheFactoryImpl.class);
	
	// Do not fetch more than once per 5 minutes
	private final static Duration DURATION_BETWEEN_FETCHING = Duration.ofMinutes(5);

	private final File stateLocation;
	private final SearchClient searchClient;
	private final ContentExpander contentExpander;
	private final ContentStore contentStore;
	private final Executor fetchExecutor;

	private final PreferenceBackedStateStore stateStore = new PreferenceBackedStateStore();
	private final boolean fullFetchRequired;
	
	/**
	 * Constructs a new factory instance.
	 * 
	 * @param stateLocation
	 *            file path for storing the cached content
	 * @param searchClient
	 *            interface for performing Confluence searches
	 * @param contentClient
	 *            interface for retrieving Confluence content
	 * @param fetchExecutor
	 *            Executor for performing potentially long-running fetches
	 *            asynchronously
	 * @throws IOException
	 */
	public ContentCacheFactoryImpl(File stateLocation, SearchClient searchClient,
			ContentClient contentClient, Executor fetchExecutor) throws IOException {
		this.stateLocation = stateLocation;
		this.searchClient = searchClient;
		this.contentExpander = createContentExpander(contentClient);
		this.contentStore = createContentStore(stateLocation);
		this.fetchExecutor = fetchExecutor;
		this.fullFetchRequired = checkChanged();
		log.debug("Full fetch required: " + this.fullFetchRequired);
	}

	private ContentExpanderImpl createContentExpander(ContentClient contentClient) {
		return new ContentExpanderImpl(contentClient, getExpandFields());
	}

	private ContentStore createContentStore(File stateLocation) throws IOException {
		return new ContentStoreCachingImpl(new ContentStoreImpl(new File(stateLocation, "content")));
	}

	/**
	 * Checks whether the cache configuration has changed so that a full fetch of
	 * content is required.
	 */
	private boolean checkChanged() {
		return (stateStore.update("contentExpander", String.valueOf(getExpandFields().hashCode()))
				|| stateStore.update("stateLocation", stateLocation.getAbsolutePath()));
	}
	
	@Override
	public ContentCache createCache(CacheContentType type, String contentName, ContentQuery contentQuery)
			throws QueryException, ContentStoreException {
		ContentSearcher contentSearcher = new ContentSearcherImpl(searchClient, contentQuery);
		ContentFetcher fetcher = createFetcher(contentName, contentSearcher);
		if (checkFullFetchRequired(contentSearcher)) {
			fetcher.requireFullFetch();
			log.debug("createCache with full fetch required");
		}
		FetchingContentStore fetchingStore = new FetchingContentStore(fetcher, contentExpander, contentStore, fetchExecutor);
		return new ContentCacheImpl(type, contentName, contentQuery, fetchingStore);
	}

	private ContentFetcher createFetcher(String contentName, ContentSearcher contentSearcher) {
		FileBackedTimestamp lastFetchingTime = new FileBackedTimestamp(new File(stateLocation, "caches"), contentName);
		return new ContentFetcherImpl(contentSearcher, lastFetchingTime, DURATION_BETWEEN_FETCHING);
	}

	private boolean checkFullFetchRequired(ContentSearcher contentSearcher) {
		return (fullFetchRequired || stateStore.update("contentSearcher", contentSearcher.getHash()));
	}
	
	private List<String> getExpandFields() {
		return ExpandFields.getFullExpandFields();
	}
}
