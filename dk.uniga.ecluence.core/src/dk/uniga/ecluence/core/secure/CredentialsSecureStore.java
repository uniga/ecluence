/*******************************************************************************
 * Derived from EGitSecureStore.
 * Modified work Copyright (c) 2017, 2018 Uniga.
 * Original work Copyright (C) 2010, Jens Baumgart <jens.baumgart@sap.com>
 * Original work Copyright (C) 2010, Edwin Kempin <edwin.kempin@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package dk.uniga.ecluence.core.secure;

import java.io.IOException;
import java.net.URI;

import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * Wraps the Eclipse Secure Store to provide storage of
 * {@link UsernamePasswordCredentials} for a given URI.
 */
public final class CredentialsSecureStore {

	private static final String USER_KEY = "user"; //$NON-NLS-1$

	private static final String PASSWORD_KEY = "password"; //$NON-NLS-1$

	private static final String ECLUENCE_PATH_PREFIX = "/Ecluence/"; //$NON-NLS-1$

	private final ISecurePreferences preferences;

	/**
	 * Constructs an instance.
	 *
	 * @param preferences
	 *            the secure preferences store to keep credentials in
	 */
	public CredentialsSecureStore(ISecurePreferences preferences) {
		this.preferences = preferences;
	}

	/**
	 * Puts credentials for the given URI into the secure store.
	 *
	 * @param uri
	 *            the URI to store credentials for
	 * @param credentials
	 *            the username and password credentials for the given URI
	 * @throws StorageException
	 *             if the credentials cannot be stored
	 * @throws IOException
	 *             if the secure store cannot be flushed
	 */
	public void putCredentials(URI uri, UsernamePasswordCredentials credentials) throws StorageException, IOException {
		String pathName = calcNodePath(uri);
		ISecurePreferences node = preferences.node(pathName);
		node.put(USER_KEY, credentials.getUsername(), false);
		node.put(PASSWORD_KEY, credentials.getPassword(), true);
		node.flush();
	}

	/**
	 * Returns credentials for the given URI.
	 *
	 * @param uri
	 *            the URI to retrieve credentials for
	 * @return the username and password credentials for the given URI or
	 *         <code>null</code> if no credentials associated with the UGI
	 * @throws StorageException
	 *             if the credentials cannot be read
	 */
	public UsernamePasswordCredentials getCredentials(URI uri) throws StorageException {
		String pathName = calcNodePath(uri);
		if (!preferences.nodeExists(pathName))
			return null;
		ISecurePreferences node = preferences.node(pathName);
		String user = node.get(USER_KEY, ""); //$NON-NLS-1$
		String password = node.get(PASSWORD_KEY, ""); //$NON-NLS-1$
		return new UsernamePasswordCredentials(user, password);
	}

	/**
	 * Clear credentials for the given uri.
	 *
	 * @param uri
	 * @throws IOException
	 */
	public void clearCredentials(URI uri) throws IOException {
		String pathName = calcNodePath(uri);
		if (!preferences.nodeExists(pathName))
			return;
		ISecurePreferences node = preferences.node(pathName);
		node.removeNode();
		node.flush();
	}

	static String calcNodePath(URI uri) {
		String pathName = ECLUENCE_PATH_PREFIX + EncodingUtils.encodeSlashes(uri.toString());
		return pathName;
	}

}
