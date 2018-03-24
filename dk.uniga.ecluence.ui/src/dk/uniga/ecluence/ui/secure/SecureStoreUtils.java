/*******************************************************************************
 * Modified work Copyright (c) 2017, 2018, Uniga.
 * Original work Copyright (C) 2010, Jens Baumgart <jens.baumgart@sap.com>
 * Original work Copyright (C) 2010, Philipp Thun <philipp.thun@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package dk.uniga.ecluence.ui.secure;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.equinox.security.storage.StorageException;

import dk.uniga.ecluence.core.secure.UsernamePasswordCredentials;
import dk.uniga.ecluence.ui.Activator;

/**
 * Utilities for Ecluence secure store
 */
public class SecureStoreUtils {
	/**
	 * Store credentials for the given uri
	 *
	 * @param credentials
	 * @param uri
	 * @return true if successful
	 */
	public static boolean storeCredentials(UsernamePasswordCredentials credentials,
			URI uri) {
		if (credentials != null && uri != null) {
			try {
				Activator.getDefault().getSecureStore()
						.putCredentials(uri, credentials);
			} catch (StorageException e) {
				Activator.handleError(MessageFormat.format(
						"Failed to write credentials for ''{0}'' to secure store", uri),
//						UIText.SecureStoreUtils_writingCredentialsFailed, uri),
						e, true);
				return false;
			} catch (IOException e) {
				Activator.handleError(MessageFormat.format(
						"Failed to write credentials for ''{0}'' to secure store", uri),
						e, true);
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets credentials stored for the given uri. Logs {@code StorageException}
	 * if thrown by the secure store implementation and removes credentials
	 * which can't be read from secure store
	 *
	 * @param uri
	 * @return credentials stored in secure store for given uri
	 */
	public static UsernamePasswordCredentials getCredentials(
			final URI uri) {
		try {
			return Activator.getDefault()
					.getSecureStore().getCredentials(uri);
		} catch (StorageException e) {
			Activator.logError(MessageFormat.format("Failed to read credentials for ''{0}'' from secure store",
					uri), e);
			clearCredentials(uri);
			return null;
		}
	}

	/**
	 * Clear credentials stored for the given uri if any exist
	 *
	 * @param uri
	 */
	public static void clearCredentials(final URI uri) {
		try {
			Activator.getDefault().getSecureStore()
					.clearCredentials(uri);
		} catch (IOException e) {
			Activator.logError(MessageFormat.format("Failed to clear credentials for ''{0}'' from secure store", uri), e);
		}
	}

}
