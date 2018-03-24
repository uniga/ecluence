/*******************************************************************************
 * Derived from EGitCredentialsProvider.
 * 
 * Modified work Copyright (c) 2017, 2018, Uniga.
 * Original work Copyright (C) 2010, Jens Baumgart <jens.baumgart@sap.com>
 * Original work Copyright (C) 2010, Edwin Kempin <edwin.kempin@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package dk.uniga.ecluence.ui.secure;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import dk.uniga.ecluence.core.secure.UsernamePasswordCredentials;

/**
 * Provides username name and password credentials for a given URI from the
 * secure store; and shows a login window if no credentials are available.
 */
public class CredentialsProvider {

	private String username;
	private String password;

	/**
	 * Default constructor
	 */
	public CredentialsProvider() {
		// empty
	}

	/**
	 * @param username
	 * @param password
	 */
	public CredentialsProvider(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Retrieves the credential item(s) for the given URI.
	 * 
	 * @param uri
	 * @param items
	 * @return true if successful
	 * @throws IllegalArgumentException
	 */
	public boolean get(final URI uri, final CredentialItem... items) throws IllegalArgumentException {

		if (items.length == 0) {
			return true;
		}

		CredentialItem.Username userItem = null;
		CredentialItem.Password passwordItem = null;
		boolean isSpecial = false;

		for (CredentialItem item : items) {
			if (item instanceof CredentialItem.Username)
				userItem = (CredentialItem.Username) item;
			else if (item instanceof CredentialItem.Password)
				passwordItem = (CredentialItem.Password) item;
			else
				isSpecial = true;
		}

		if (!isSpecial && (userItem != null || passwordItem != null)) {
			UsernamePasswordCredentials credentials = null;
			if ((username != null) && (password != null))
				credentials = new UsernamePasswordCredentials(username, password);
			else
				credentials = SecureStoreUtils.getCredentials(uri);

			if (credentials == null) {
				credentials = getCredentialsFromUser(uri);
				if (credentials == null)
					return false;
			}
			if (userItem != null)
				userItem.setValue(credentials.getUsername());
			if (passwordItem != null)
				passwordItem.setValue(credentials.getPassword().toCharArray());
			return true;
		}
		
		return getUserConfirmation(uri, items);
	}

	private boolean getUserConfirmation(final URI uri, final CredentialItem... items) {
		// special handling for non-username,non-password type items
		final boolean[] result = new boolean[1];

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

				if (items.length == 1) {
					CredentialItem item = items[0];
					result[0] = getSingleSpecial(shell, uri, item);
				} else {
					result[0] = getMultiSpecial(shell, uri, items);
				}
			}
		});

		return result[0];
	}

	public void reset(URI uri) {
		SecureStoreUtils.clearCredentials(uri);
		username = null;
		password = null;
	}

	/**
	 * Opens a dialog for a single non-username, non-password type item.
	 * 
	 * @param shell
	 *            the shell to use
	 * @param uri
	 *            the uri of the get request
	 * @param item
	 *            the item to handle
	 * @return <code>true</code> if the request was successful and values were
	 *         supplied; <code>false</code> if the username canceled the request and
	 *         did not supply all requested values.
	 */
	private boolean getSingleSpecial(Shell shell, URI uri, CredentialItem item) {
		if (item instanceof CredentialItem.InformationalMessage) {
			MessageDialog.openInformation(shell, "Information", item.getPromptText());
			return true;
		} else if (item instanceof CredentialItem.YesNoType) {
			CredentialItem.YesNoType v = (CredentialItem.YesNoType) item;
			String[] labels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
					IDialogConstants.CANCEL_LABEL };
			int[] resultIDs = new int[] { IDialogConstants.YES_ID, IDialogConstants.NO_ID, IDialogConstants.CANCEL_ID };

			MessageDialog dialog = new MessageDialog(shell, "Question", null, item.getPromptText(),
					MessageDialog.QUESTION_WITH_CANCEL, labels, 0);
			dialog.setBlockOnOpen(true);
			int r = dialog.open();
			if (r < 0) {
				return false;
			}

			switch (resultIDs[r]) {
			case IDialogConstants.YES_ID: {
				v.setValue(true);
				return true;
			}
			case IDialogConstants.NO_ID: {
				v.setValue(false);
				return true;
			}
			default:
				// abort
				return false;
			}
		} else {
			// generically handles all other types of items
			return getMultiSpecial(shell, uri, item);
		}
	}

	/**
	 * Opens a generic dialog presenting all CredentialItems to the username.
	 * 
	 * @param shell
	 *            the shell to use
	 * @param uri
	 *            the uri of the get request
	 * @param items
	 *            the items to handle
	 * @return <code>true</code> if the request was successful and values were
	 *         supplied; <code>false</code> if the username canceled the request and
	 *         did not supply all requested values.
	 */
	private boolean getMultiSpecial(Shell shell, URI uri, CredentialItem... items) {
		CustomPromptDialog dialog = new CustomPromptDialog(shell, uri, "Information", items);
		dialog.setBlockOnOpen(true);
		int r = dialog.open();
		if (r == Window.OK) {
			return true;
		}
		return false;
	}

	private UsernamePasswordCredentials getCredentialsFromUser(final URI uri) {
		final AtomicReference<UsernamePasswordCredentials> aRef = new AtomicReference<>(null);
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				aRef.set(LoginService.login(shell, uri));
			}
		});
		return aRef.get();
	}
}
