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
package dk.uniga.ecluence.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.RestException;
import de.itboehmer.confluence.rest.core.domain.content.AttachmentBean;
import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.impl.APIAuthConfig;
import dk.uniga.ecluence.core.Activator.Described;
import dk.uniga.ecluence.core.cache.ContentCache;
import dk.uniga.ecluence.core.cache.ContentCacheProvider;
import dk.uniga.ecluence.core.cache.ContentCacheRegistry;
import dk.uniga.ecluence.core.cache.ContentCacheRegistryListener;
import dk.uniga.ecluence.core.cache.ContentStoreException;
import dk.uniga.ecluence.core.cache.ExpandFields;

public class ConfluenceFacadeImpl implements ConfluenceFacade {

	private static final Logger log = LoggerFactory.getLogger(ConfluenceFacadeImpl.class);

	private final Executor executor;

	private final IPath stateLocation;

	private final Consumer<ContentCacheProvider> cacheAddedCallback;
	
	private ConfluenceService confluenceService;

	private ContentCacheRegistryListener cacheRegistryListener = new ContentCacheRegistryListener() {
		@Override
		public void cacheProviderAdded(ContentCacheProvider provider) throws QueryException, ContentStoreException {
			log.debug("cacheProviderAdded({})", provider);
			if (confluenceService != null)
				confluenceService.cacheProviderAdded(provider);
		}
	};

	private ContentCacheRegistry contentCacheRegistry;

	public ConfluenceFacadeImpl(Executor executor, IPath stateLocation, Consumer<ContentCacheProvider> cacheAddedCallback) {
		this.executor = executor;
		this.stateLocation = stateLocation;
		this.cacheAddedCallback = cacheAddedCallback;
	}

	public void connect(APIAuthConfig authProps)
			throws URISyntaxException, RestException, IOException, QueryException, ContentStoreException {
		Executor messagingExecutor = createMessagingExecutor(
				"Fetching content from Confluence location " + authProps.getBaseUrl());
		confluenceService = new ConfluenceService(authProps, messagingExecutor, stateLocation);
		addCaches();
	}

	/**
	 * Creates an Executor that describes a Runnable with a message.
	 * 
	 * @param message
	 * @return
	 */
	private Executor createMessagingExecutor(final String message) {
		return new Executor() {
			@Override
			public void execute(Runnable command) {
				executor.execute(new InternalCommand(command, message));
			}
		};
	}

	private void addCaches() {
		if (confluenceService != null && contentCacheRegistry != null)
			for (ContentCacheProvider provider : contentCacheRegistry.getProviders()) {
				log.debug("addCacheProvider {}", provider);
				createMessagingExecutor("Adding Ecluence cache " + provider.getName()).execute(() -> {
					try {
						confluenceService.cacheProviderAdded(provider);
						cacheAddedCallback.accept(provider);
					} catch (QueryException | ContentStoreException e) {
						throw new RuntimeException(e);
					}
				});
			}
	}

	private ConfluenceService getConfluenceService() throws NotConnectedException {
		if (confluenceService == null)
			throw new NotConnectedException("Not connected to Confluence host");
		return confluenceService;
	}

	@Override
	public Collection<ContentBean> getPages() throws QueryException {
		Collection<ContentCache> caches = getConfluenceService().getContentCaches();
		log.debug("getPages from {}", Arrays.toString(caches.toArray()));
		return caches.stream().flatMap(cache -> {
			try {
				return cache.getAll().stream();
			} catch (QueryException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toSet());
	}

	@Override
	public Collection<ContentBean> getPages(String cacheName) throws QueryException {
		log.debug("getPages({})", cacheName);
		Optional<ContentCache> cache = getConfluenceService().getContentCache(cacheName);
		return cache.isPresent() ? cache.get().getAll() : Collections.emptyList();
	}

	public ContentBean getPageById(String contentId) throws QueryException {
		List<String> expands = ExpandFields.getFullExpandFields();
		try {
			return getConfluenceService().getContentClient().getContentById(contentId, 0, expands).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new QueryException("Could not retrieve page.", e);
		}
	}

	@Override
	public String getLinkUrl(String webui) throws NotConnectedException {
		return getConfluenceService().getLinkUrl(webui);
	}

	@Override
	public int refresh() throws NotConnectedException, QueryException, ContentStoreException {
		return getConfluenceService().refreshCaches();
	}
	
	public Future<InputStream> getAttachment(String id) throws NotConnectedException {
		AttachmentBean attachment = new AttachmentBean(id);
		attachment.setTitle(id);
		return getConfluenceService().getContentClient().downloadAttachement(attachment);
	}
	
	@Override
	public void addContentListener(ContentUpdateListener listener) throws NotConnectedException {
		getConfluenceService().addCacheListener(listener);
	}

	@Override
	public void removeContentListener(ContentUpdateListener listener) throws NotConnectedException {
		getConfluenceService().removeCacheListener(listener);
	}

	@Override
	public void setContentCacheRegistry(ContentCacheRegistry contentCacheRegistry)
			throws QueryException, ContentStoreException {
		this.contentCacheRegistry = contentCacheRegistry;
		contentCacheRegistry.addListener(cacheRegistryListener);
		addCaches();
	}
	
	private final class InternalCommand implements Described, Runnable {

		private final Runnable runnable;
		private final String description;

		public InternalCommand(Runnable runnable, String description) {
			this.runnable = runnable;
			this.description = description;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public void run() {
			runnable.run();
		}

	}

}
