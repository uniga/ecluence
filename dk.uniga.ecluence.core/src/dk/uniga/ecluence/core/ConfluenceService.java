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
package dk.uniga.ecluence.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IPath;

import de.itboehmer.confluence.rest.client.ContentClient;
import de.itboehmer.confluence.rest.client.SearchClient;
import de.itboehmer.confluence.rest.client.impl.ClientFactoryImpl;
import de.itboehmer.confluence.rest.core.RestException;
import de.itboehmer.confluence.rest.core.SecurityException;
import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.impl.APIAuthConfig;
import de.itboehmer.confluence.rest.core.impl.APIUriProvider;
import de.itboehmer.confluence.rest.core.impl.HttpAuthRequestService;
import dk.uniga.ecluence.core.cache.CacheContentType;
import dk.uniga.ecluence.core.cache.ContentCache;
import dk.uniga.ecluence.core.cache.ContentCacheFactoryImpl;
import dk.uniga.ecluence.core.cache.ContentCacheProvider;
import dk.uniga.ecluence.core.cache.ContentCacheRegistryListener;
import dk.uniga.ecluence.core.cache.ContentStoreException;

/**
 * Service for accessing Confluence content via {@link ContentCache}s.
 */
public final class ConfluenceService implements ContentCacheRegistryListener {

	private final String baseWikiUrl;

	private final ClientFactoryImpl factory;
	
	private final SearchClient searchClient;
	
	private final ContentClient contentClient;

	private final ContentCacheFactoryImpl cacheFactory;

	private final Collection<ContentCache> contentCaches = new HashSet<>();
	
	private final ContentCacheMediator contentCacheMediator = new ContentCacheMediator();

	/**
	 * Constructs a new service instance.
	 * 
	 * @param authProps properties for authenticating user with the Confluence API
	 * @param fetchExecutor Executor for performing fetching operations asynchronously, which must handle RuntimeExceptions
	 * @param stateLocation file path to store state data
	 * @throws URISyntaxException
	 * @throws RestException
	 * @throws IOException
	 * @throws QueryException
	 * @throws ContentStoreException
	 */
	public ConfluenceService(final APIAuthConfig authProps, final Executor fetchExecutor, final IPath stateLocation)
			throws URISyntaxException, RestException, IOException, QueryException, ContentStoreException {
		baseWikiUrl = authProps.getBaseUrl() + "/wiki";
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		APIUriProvider apiConfig = new APIUriProvider(new URI(baseWikiUrl));
		factory = new ClientFactoryImpl(executorService, connectRequestService(authProps), apiConfig);
		searchClient = factory.getSearchClient();
		contentClient = factory.getContentClient();
		cacheFactory = createCacheFactory(authProps, fetchExecutor, stateLocation);
	}

	private ContentCacheFactoryImpl createCacheFactory(final APIAuthConfig authProps, final Executor fetchExecutor,
			final IPath stateLocation) throws IOException {
		File cacheBaseLocation = getBaseSiteLocation(authProps, stateLocation);
		return new ContentCacheFactoryImpl(cacheBaseLocation, searchClient, contentClient,
				fetchExecutor);
	}

	private File getBaseSiteLocation(final APIAuthConfig authProps, final IPath stateLocation) {
		return stateLocation.append(".sites").append(getSiteIdentifier(authProps.getBaseUrl())).toFile();
	}

	public ContentClient getContentClient() {
		return contentClient;
	}

	private HttpAuthRequestService connectRequestService(APIAuthConfig authProps)
			throws SecurityException, URISyntaxException {
		HttpAuthRequestService requestService = new HttpAuthRequestService();
		requestService.connect(new URI(authProps.getBaseUrl()), authProps.getUser(), authProps.getPassword());
		return requestService;
	}

	private String getSiteIdentifier(String url) {
		String protocol = StringUtils.substringBefore(url, "://");
		return StringUtils.replaceChars(StringUtils.removeStart(url, protocol), ":/", "");
	}

	public String getLinkUrl(String webui) {
		return baseWikiUrl + webui;
	}

	public void cacheProviderAdded(final ContentCacheProvider provider) throws QueryException, ContentStoreException {
		ContentCache cache = provider.getCache(cacheFactory);
		cache.addListener(contentCacheMediator);
		contentCaches.add(cache);
	}

	public Collection<ContentBean> getPages(final CacheContentType... types) throws QueryException {
		return contentCaches.stream()
		.filter((cache) -> Arrays.stream(types).anyMatch(type -> type.equals(cache.getType())))
		.flatMap((cache) -> {
			try {
				return cache.getAll().stream();
			} catch (QueryException e) {
				throw new RuntimeException(e);
			}
		})
		.collect(Collectors.toList());
	}
	
	public Collection<ContentBean> getIndexPages() throws QueryException {
		return getPages(CacheContentType.Index);
	}

	public Collection<ContentBean> getContextPages() throws QueryException {
		return getPages(CacheContentType.Context);
	}
	
	public Optional<ContentCache> getContentCache(String cacheName) {
		return contentCaches.stream().filter(cache -> cacheName.equals(cache.getName())).findFirst();
	}

	public Collection<ContentCache> getContentCaches() {
		return Collections.unmodifiableCollection(contentCaches);
	}

	public int refreshCaches() throws QueryException, ContentStoreException {
		return contentCaches.stream().mapToInt(cache -> {
			try {
				return cache.refresh();
			} catch (QueryException | ContentStoreException e) {
				throw new RuntimeException(e);
			}
		}).sum();
	}

	public void addCacheListener(ContentUpdateListener listener) {
		contentCacheMediator.addListener(listener);
	}

	public void removeCacheListener(ContentUpdateListener listener) {
		contentCacheMediator.removeListener(listener);
	}

	private final class ContentCacheMediator implements ContentUpdateListener {

		private Set<ContentUpdateListener> listeners = new HashSet<>();

		@Override
		public void contentUpdated(Collection<ContentBean> pages) {
			listeners.forEach(listener -> listener.contentUpdated(pages));
		}

		public void addListener(ContentUpdateListener listener) {
			listeners.add(listener);
		}

		public void removeListener(ContentUpdateListener listener) {
			listeners.remove(listener);
		}

	}

}
