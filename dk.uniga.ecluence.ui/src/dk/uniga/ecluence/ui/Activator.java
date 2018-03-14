/*******************************************************************************
 * Contains original work from EGit.
 * Copyright (c) 2017 Uniga.
 * Original work Copyright (c) 2007,2010 Robin Rosenberg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mikkel R. Jakobsen - initial API and implementation
 *******************************************************************************/
package dk.uniga.ecluence.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.RestException;
import dk.uniga.ecluence.core.ConfluenceFacade;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.cache.ContentStoreException;
import dk.uniga.ecluence.core.secure.CredentialsSecureStore;
import dk.uniga.ecluence.ui.parts.EditorSelectionAdapter;
import dk.uniga.ecluence.ui.template.TemplateStore;

public class Activator extends AbstractUIPlugin {

	private static final Logger log = LoggerFactory.getLogger(Activator.class);

	public static final String PLUGIN_ID = "dk.uniga.ecluence.ui";

	private static BundleContext context;

	private static Activator instance;

	static BundleContext getContext() {
		return context;
	}

	public static final Activator getDefault() {
		return instance;
	}

	private ConfluenceConnector confluenceConnector;

	private TemplateStore templateStore;

	private EditorSelectionAdapterRegistry editorSelectionAdapterRegistry = new EditorSelectionAdapterRegistry();
	
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		Activator.instance = this;
		log.debug("Starting plugin: " + getBundle().getSymbolicName());
		createTemplateStore();
		createConnector();
		registerContributions(Platform.getExtensionRegistry());
	}

    private void createConnector() {
		confluenceConnector = new ConfluenceConnector(getPreferenceStore(), getSecureStore(), 
				getConfluenceFacade(), getEventBroker());
		confluenceConnector.connectAPI();
	}

	public TemplateStore getTemplateStore() {
		log.debug("templateStore " + templateStore);
		return templateStore;
	}
	
	private void createTemplateStore() {
		IPath stateLocation = Activator.getDefault().getStateLocation().addTrailingSeparator();
		File templateStoreLocation = stateLocation.append("templates").toFile();
		log.debug("createTemplateStore " + templateStoreLocation);
		try {
			templateStore = new TemplateStore(templateStoreLocation);
		} catch (IOException e) {
			handleError("Could not create template store in location " + templateStoreLocation, e, true);
		}
	}
	
	public void registerContributions(IExtensionRegistry registry) throws QueryException, ContentStoreException {
		log.debug("registerContributions {}", registry);
		ExtensionEditorSelectionAdapterProvider contributionsHandler = new ExtensionEditorSelectionAdapterProvider();
		contributionsHandler.execute(registry);
		for (EditorSelectionAdapter adapter : contributionsHandler.getEditorSelectionAdapters()) {
			editorSelectionAdapterRegistry.addAdapter(adapter);
		}
		log.debug("editorSelectionAdapters: {}", Arrays.toString(contributionsHandler.getEditorSelectionAdapters().toArray()));
	}
	
	public EditorSelectionAdapterRegistry getEditorSelectionAdapterRegistry() {
		return editorSelectionAdapterRegistry;
	}

	private ConfluenceFacade getConfluenceFacade() {
		return dk.uniga.ecluence.core.Activator.getDefault().getConfluenceFacade();
	}

	public IEventBroker getEventBroker() {
		IEclipseContext rootContext = EclipseContextFactory.getServiceContext(getContext());
		return rootContext.get(IEventBroker.class);
	}
	
	public static String formatRestException(String message, RestException e) {
		return String.format("%s (Reason: '%s' Response: '%s')", message, e.getReasonPhrase(), e.getResponseBody());
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		Activator.instance = null;
	}

	public ISecurePreferences getSecurePreferences() {
		return dk.uniga.ecluence.core.Activator.getDefault().getSecurePreferences();
	}

	public CredentialsSecureStore getSecureStore() {
		return dk.uniga.ecluence.core.Activator.getDefault().getSecureStore();
	}
	
	public static void logError(String message, Throwable e) {
		handleError(message, e, false);
	}

	/**
	 * Handle an error. The error is logged. If <code>show</code> is
	 * <code>true</code> the error is shown to the user.
	 *
	 * @param message
	 *            a localized message
	 * @param throwable
	 * @param show
	 */
	public static void handleError(String message, Throwable throwable, boolean show) {
		handleIssue(IStatus.ERROR, message, throwable, show);
	}

	/**
	 * Handle an issue. The issue is logged. If <code>show</code> is
	 * <code>true</code> the issue is shown to the user.
	 *
	 * @param severity
	 *            status severity, use constants defined in {@link IStatus}
	 * @param message
	 *            a localized message
	 * @param throwable
	 * @param show
	 * @since 2.2
	 */
	public static void handleIssue(int severity, String message, Throwable throwable, boolean show) {
		IStatus status = toStatus(severity, message, throwable);
		int style = StatusManager.LOG;
		if (show)
			style |= StatusManager.SHOW;
		StatusManager.getManager().handle(status, style);
	}

	/**
	 * Creates an {@link IStatus} from the parameters. If the throwable is an
	 * {@link InvocationTargetException}, the status is created from the first
	 * exception that is either not an InvocationTargetException or that has a
	 * message. If the message passed is empty, tries to supply a message from that
	 * exception.
	 *
	 * @param severity
	 *            of the {@link IStatus}
	 * @param message
	 *            for the status
	 * @param throwable
	 *            that caused the status, may be {@code null}
	 * @return the status
	 */
	private static IStatus toStatus(int severity, String message, Throwable throwable) {
		Throwable exc = throwable;
		while (exc instanceof InvocationTargetException) {
			String msg = exc.getLocalizedMessage();
			if (msg != null && !msg.isEmpty()) {
				break;
			}
			Throwable cause = exc.getCause();
			if (cause == null) {
				break;
			}
			exc = cause;
		}
		if (exc != null && (message == null || message.isEmpty())) {
			message = exc.getLocalizedMessage();
		}
		return new Status(severity, getPluginId(), message, exc);
	}

	private static String getPluginId() {
		return PLUGIN_ID;
	}
}
