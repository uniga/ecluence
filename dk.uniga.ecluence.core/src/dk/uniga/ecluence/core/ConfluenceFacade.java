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
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import de.itboehmer.confluence.rest.core.RestException;
import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.impl.APIAuthConfig;
import dk.uniga.ecluence.core.cache.ContentCacheRegistry;
import dk.uniga.ecluence.core.cache.ContentStoreException;

public interface ConfluenceFacade {

	void connect(APIAuthConfig authProps)
			throws URISyntaxException, RestException, IOException, QueryException, ContentStoreException;

	/**
	 * Returns all pages from all caches. This method may be long-running and should
	 * not be called directly from the UI thread.
	 * 
	 * @return collection of pages
	 * @throws NotConnectedException if the facade is not connected to an API
	 * @throws QueryException if a problem occurred while querying for pages
	 */
	Collection<ContentBean> getPages() throws NotConnectedException, QueryException;

	/**
	 * Returns all pages from the given cache. This method may be long-running and
	 * should not be called directly from the UI thread.
	 * 
	 * @return collection of pages
	 * @throws NotConnectedException if the facade is not connected to an API
	 * @throws QueryException if a problem occurred while querying for pages
	 */
	Collection<ContentBean> getPages(String cacheName) throws NotConnectedException, QueryException;

	/**
	 * Returns the page for a given id.
	 * 
	 * @param contentId the page id
	 * @return the page or <code>null</code> if no such page exists
	 * @throws NotConnectedException if the facade is not connected to an API
	 * @throws QueryException if a problem occurred while querying the page
	 */
	ContentBean getPageById(String contentId) throws NotConnectedException, QueryException;

	/**
	 * Returns a URL to the confluence API with the given suffix appended.
	 * 
	 * @param webui String containing the path to append the base URL
	 * @return String containing the URL for connecting to the API
	 * @throws NotConnectedException if the facade is not connected to an API
	 */
	String getLinkUrl(String webui) throws NotConnectedException;

	/**
	 * Refreshes the caches
	 * 
	 * @return
	 * @throws NotConnectedException if the facade is not connected to an API
	 * @throws QueryException if a problem occurred while querying the API
	 * @throws ContentStoreException if a problem occurred storing or accessing stored content
	 */
	int refresh() throws NotConnectedException, QueryException, ContentStoreException;

	/**
	 * Returns an input stream for reading an attachment with a given id.
	 * 
	 * @param id the id of the attachment
	 * @return an InputStream for reading the attachment
	 * @throws NotConnectedException if the facade is not connected to an API
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException 
	 */
	InputStream getAttachment(String id) throws NotConnectedException, InterruptedException, ExecutionException, TimeoutException;

	/**
	 * Adds a listener to be notified if content in a cache has been updated.
	 * 
	 * @param listener the listener to receive notifications
	 * @throws NotConnectedException if the facade is not connected to an API
	 */
	void addContentListener(ContentUpdateListener listener) throws NotConnectedException;

	/**
	 * Removes a given listener so that it no longer receives notifications.
	 * 
	 * @param listener the listener that should stop receiving notifications
	 * @throws NotConnectedException if the facade is not connected to an API
	 */
	void removeContentListener(ContentUpdateListener contentCacheListener) throws NotConnectedException;

	/**
	 * Sets the ContentCacheRegistry that this facade should use to access cached content.
	 * 
	 * @param contentCacheRegistry
	 * @throws QueryException
	 * @throws ContentStoreException
	 */
	void setContentCacheRegistry(ContentCacheRegistry contentCacheRegistry) throws QueryException, ContentStoreException;

}
