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
package dk.uniga.ecluence.ui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.RestException;
import de.itboehmer.confluence.rest.core.impl.APIAuthConfig;
import dk.uniga.ecluence.core.ConfluenceFacade;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.cache.ContentStoreException;
import dk.uniga.ecluence.core.preferences.PreferenceConstants;
import dk.uniga.ecluence.core.secure.CredentialsSecureStore;
import dk.uniga.ecluence.core.secure.UsernamePasswordCredentials;
import dk.uniga.ecluence.ui.handlers.EventConstants;
import dk.uniga.ecluence.ui.secure.LoginService;

/**
 * Creates a connection to the selected Confluence location or to the first
 * location if no location has been selected; re-connects if the selection
 * changes or the location changes.
 */
public final class ConfluenceConnector {
	
	private static final Logger log = LoggerFactory.getLogger(ConfluenceConnector.class);
	
	private final IPreferenceStore preferenceStore;

	private final CredentialsSecureStore secureStore;

	private final ConfluenceFacade confluenceFacade;

	private final IEventBroker eventBroker;

	public ConfluenceConnector(IPreferenceStore preferenceStore, CredentialsSecureStore credentialsSecureStore, ConfluenceFacade confluenceFacade, IEventBroker eventBroker) {
		this.preferenceStore = preferenceStore;
		this.secureStore = credentialsSecureStore;
		this.confluenceFacade = confluenceFacade;
		this.eventBroker = eventBroker;
		registerPreferencesListener();
	}

	private void registerPreferencesListener() {
		getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (PreferenceConstants.PREFERENCE_SELECTED_LOCATION_KEY_NAME.equals(event.getProperty())) {
					selectedLocationChanged();
				}
				if (PreferenceConstants.PREFERENCE_LOCATION_KEY_NAME.equals(event.getProperty())) {
					locationsChanged();
				}
			}
		});
	}

	protected void locationsChanged() {
		if (initializeSelectedLocation())
			scheduleConnectJob();
	}

	private boolean initializeSelectedLocation() {
		if (!getPreferenceStore().contains(PreferenceConstants.PREFERENCE_SELECTED_LOCATION_KEY_NAME) || 
				StringUtils.isBlank(getPreferenceStore().getString(PreferenceConstants.PREFERENCE_SELECTED_LOCATION_KEY_NAME))) {
			String locations = getPreferenceStore().getString(PreferenceConstants.PREFERENCE_LOCATION_KEY_NAME);
			String[] locs = StringUtils.split(locations, ';');
			log.debug("got locations '{}'", locations);
			if (locs.length > 0) {
				getPreferenceStore().setValue(PreferenceConstants.PREFERENCE_SELECTED_LOCATION_KEY_NAME, locs[0]);
				return true;
			}
		}
		return false;
	}

	protected void selectedLocationChanged() {
		scheduleConnectJob();
	}

	public void scheduleConnectJob() {
		new Job("Connecting to Confluence") {
			@Override
			protected IStatus run(IProgressMonitor arg0) {
				return connectAPI();
			}
		}.schedule();
	}

	private IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	private CredentialsSecureStore getSecureStore() {
		return secureStore;
	}

	private ConfluenceFacade getConfluenceFacade() {
		return confluenceFacade;
	}
	
	public synchronized IStatus connectAPI() {
		initializeSelectedLocation();
		String location = getPreferenceStore().getString(PreferenceConstants.PREFERENCE_SELECTED_LOCATION_KEY_NAME);
		log.debug("connectAPI: selected location '{}'", location);
		if (!StringUtils.isBlank(location)) {
			try {
				UsernamePasswordCredentials credentials = getCredentials(location);
				getConfluenceFacade().connect(new APIAuthConfig(location, credentials.getUsername(), credentials.getPassword()));
				notifyConnection();
			} catch (URISyntaxException e) {
				return handleError("Location URL not valid", e);
			} catch (StorageException e) {
				return handleError("Error getting credentials for URI from secure store", e);
			} catch (RestException e) {
				return handleError(Activator.formatRestException("Error connecting to Confluence API", e), e);
			} catch (QueryException | IOException e) {
				return handleError("Error connecting to Confluence API", e);
			} catch (ContentStoreException e) {
				return handleError("Error reading content from local store", e);
			}
		}
		return Status.OK_STATUS;
	}

	private UsernamePasswordCredentials getCredentials(String location) throws StorageException, URISyntaxException {
		URI uri = new URI(location);
		UsernamePasswordCredentials credentials = getSecureStore().getCredentials(uri);
		if (credentials == null) {
			log.debug("Credentials for '{}' not found", location);
			credentials = LoginService.login(PlatformUI.getWorkbench().getDisplay().getActiveShell(), uri);
		}
		return credentials;
	}

	private void notifyConnection() {
		eventBroker.post(EventConstants.CONNECTED_FACADE, getConfluenceFacade());
	}

	private Status handleError(String message, Exception e) {
		Activator.handleError(message, e, true);
		return new Status(Status.ERROR, Activator.PLUGIN_ID, message, e);
	}

}
