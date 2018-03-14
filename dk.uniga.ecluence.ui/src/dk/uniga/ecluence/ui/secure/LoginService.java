/*******************************************************************************
 * Copyright (C) 2010, 2017, Jens Baumgart <jens.baumgart@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package dk.uniga.ecluence.ui.secure;

import java.net.URI;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import dk.uniga.ecluence.core.secure.UsernamePasswordCredentials;

/**
 * This class implements services for interactive login and changing stored
 * credentials.
 */
public class LoginService {

	/**
	 * The method shows a login dialog for a given URI. The user field is taken
	 * from the URI if a user is present in the URI. In this case the user is
	 * not editable.
	 *
	 * @param parent
	 * @param uri
	 * @return credentials, <code>null</code> if the user canceled the dialog.
	 */
	public static UsernamePasswordCredentials login(Shell parent, URI uri) {
		LoginDialog dialog = new LoginDialog(parent, uri);
		if (dialog.open() == Window.OK) {
			UsernamePasswordCredentials credentials = dialog.getCredentials();
			if (credentials != null && dialog.getStoreInSecureStore())
				SecureStoreUtils.storeCredentials(credentials, uri);
			return credentials;
		}
		return null;
	}

	/**
	 * The method shows a change credentials dialog for a given URI. The user
	 * field is taken from the URI if a user is present in the URI. In this case
	 * the user is not editable.
	 *
	 * @param parent
	 * @param uri
	 * @return credentials, <code>null</code> if the user canceled the dialog.
	 */
	public static UsernamePasswordCredentials changeCredentials(Shell parent,
			URI uri) {
		LoginDialog dialog = new LoginDialog(parent, uri);
		dialog.setChangeCredentials(true);
		UsernamePasswordCredentials oldCredentials = SecureStoreUtils
				.getCredentials(uri);
		if (oldCredentials != null)
			dialog.setOldUser(oldCredentials.getUsername());
		if (dialog.open() == Window.OK) {
			UsernamePasswordCredentials credentials = dialog.getCredentials();
			if (credentials != null)
				SecureStoreUtils.storeCredentials(credentials, uri);
			return credentials;
		}
		return null;
	}
}
