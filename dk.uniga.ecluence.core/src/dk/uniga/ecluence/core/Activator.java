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

import java.util.Arrays;
import java.util.concurrent.Executor;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.RestException;
import dk.uniga.ecluence.core.cache.CacheContentType;
import dk.uniga.ecluence.core.cache.ContentCacheProvider;
import dk.uniga.ecluence.core.cache.ContentCacheRegistry;
import dk.uniga.ecluence.core.cache.ContentProvider;
import dk.uniga.ecluence.core.cache.ContentProviderImpl;
import dk.uniga.ecluence.core.cache.ContentQueryImpl;
import dk.uniga.ecluence.core.cache.ContentStoreException;
import dk.uniga.ecluence.core.cache.LabelledContentCacheProvider;
import dk.uniga.ecluence.core.matching.ContentMatcherProvider;
import dk.uniga.ecluence.core.matching.ContentMatcherRegistry;
import dk.uniga.ecluence.core.matching.IndexPageMatcherProvider;
import dk.uniga.ecluence.core.secure.CredentialsSecureStore;

public class Activator extends Plugin implements RestExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(Activator.class);

	private static final String PLUGIN_ID = "dk.uniga.ecluence.core";

	private static final String INDEX_CACHE_NAME = "contentEcluenceIndex";

	private static final String INDEX_CONTENT_LABEL = "ecluence-index";

	private static BundleContext context;

	private static Activator instance;

	private ConfluenceFacade confluenceFacade;

	private CredentialsSecureStore secureStore;

	private ContentMatcherRegistry contentMatcherRegistry;

	private ContentCacheRegistry contentCacheRegistry;

	static BundleContext getContext() {
		return context;
	}

	public static final Activator getDefault() {
		return instance;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		Activator.instance = this;
		secureStore = new CredentialsSecureStore(SecurePreferencesFactory.getDefault());
		confluenceFacade = createConfluenceFacade();
		contentCacheRegistry = createContentCacheRegistry();
		contentMatcherRegistry = createContentMatcherRegistry();
		registerContributions(Platform.getExtensionRegistry());
	}

	private ConfluenceFacade createConfluenceFacade() {
		IPath stateLocation = Activator.getDefault().getStateLocation().addTrailingSeparator();
		return new ConfluenceFacadeImpl(new JobExecutor(), stateLocation, (c) -> notifyContentCacheAdded(c));
	}

	interface Described {
		String getDescription();
	}
	
	/**
	 * Long-running calls to Confluence API are run as job by an executor, which is
	 * injected into the ConfluenceFacade to avoid dependencies
	 */
	class JobExecutor implements Executor {
		@Override
		public void execute(Runnable command) {
			String description = "Requesting Confluence content";
			if (command instanceof Described) {
				Described described = (Described) command;
				description = described.getDescription();
			}
			new Job(description) {
				@Override
				protected IStatus run(IProgressMonitor arg0) {
					try {
						command.run();
					} catch (RuntimeException e) {
						log.error("Runtime exception", e);
						if (e.getCause() instanceof QueryException) {
							log.error("Instance of QueryException");
							notifyProblem((QueryException) e.getCause());
							return Status.OK_STATUS;
						}
						return new Status(Status.ERROR, PLUGIN_ID, e.getMessage(), e);
					}
					
					return Status.OK_STATUS;
				}
			}.schedule();
		}
	}
	
	private void notifyContentCacheAdded(ContentCacheProvider provider) {
		getEventBroker().post(EventConstants.CONTENT_CACHE_ADDED, provider);
	}

	private void notifyProblem(Exception exception) {
		getEventBroker().post(EventConstants.EXCEPTION, exception);
	}

	private IEventBroker getEventBroker() {
		IEclipseContext rootContext = EclipseContextFactory.getServiceContext(getContext());
		return rootContext.get(IEventBroker.class);
	}
	
	private ContentCacheRegistry createContentCacheRegistry() throws QueryException, ContentStoreException {
		ContentCacheRegistry registry = new ContentCacheRegistry();
		confluenceFacade.setContentCacheRegistry(registry);
		ContentCacheProvider cacheProvider = new LabelledContentCacheProvider(
				INDEX_CACHE_NAME, new ContentQueryImpl(Arrays.asList(INDEX_CONTENT_LABEL)),
				CacheContentType.Index);
		registry.addProvider(cacheProvider);
		return registry;
	}
	
	private ContentMatcherRegistry createContentMatcherRegistry() {
		ContentMatcherRegistry registry = new ContentMatcherRegistry();
		ContentProvider indexPageProvider = new ContentProviderImpl(() -> getConfluenceFacade(), Activator.INDEX_CACHE_NAME);
		IndexPageMatcherProvider matcherProvider = new IndexPageMatcherProvider(Activator.INDEX_CONTENT_LABEL);
		matcherProvider.setContentProviderSupplier(() -> indexPageProvider);
		registry.addProvider(matcherProvider);
		return registry;
	}
	
	public void registerContributions(IExtensionRegistry registry) throws QueryException, ContentStoreException {
		log.debug("registerContributions {}", registry);
		ExtensionContributionsProvider contributionsHandler = new ExtensionContributionsProvider(() -> getConfluenceFacade());
		contributionsHandler.execute(registry);
		for (ContentCacheProvider provider : contributionsHandler.getContentCacheProviders()) {
			contentCacheRegistry.addProvider(provider);
		}
		for (ContentMatcherProvider provider : contributionsHandler.getContentMatcherProviders()) {
			contentMatcherRegistry.addProvider(provider);
		}
		log.debug("contentCacheRegistry: {}", Arrays.toString(contentCacheRegistry.getProviders().toArray()));
		log.debug("contentMatcherRegistry: {}", Arrays.toString(contentMatcherRegistry.getContentMatchers().toArray()));
	}

	public void restException(String message, RestException e) {
		log.error(String.format("%s (Reason: '%s' Response: '%s')", message, e.getReasonPhrase(), e.getResponseBody()),
				e);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		secureStore = null;
		Activator.context = null;
		Activator.instance = null;
	}

	public ISecurePreferences getSecurePreferences() {
		return SecurePreferencesFactory.getDefault().node(Activator.PLUGIN_ID);
	}

	public ConfluenceFacade getConfluenceFacade() {
		return confluenceFacade;
	}

	public CredentialsSecureStore getSecureStore() {
		return secureStore;
	}

	public String getPluginId() {
		return PLUGIN_ID;
	}

	public ContentMatcherRegistry getContentMatcherRegistry() {
		return contentMatcherRegistry;
	}
	
	public ContentCacheRegistry getContentCacheRegistry() {
		return contentCacheRegistry;
	}
}
