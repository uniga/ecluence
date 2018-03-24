/*******************************************************************************
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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import dk.uniga.ecluence.core.preferences.PreferenceConstants;
import dk.uniga.ecluence.core.secure.UsernamePasswordCredentials;
import dk.uniga.ecluence.ui.Activator;

/**
 * Dialog asking for user and password for a given URI.
 */
final class LoginDialog extends Dialog {

	private Text user;

	private Text password;

	private Button storeCheckbox;

	private UsernamePasswordCredentials credentials;

	private boolean storeInSecureStore;

	private final URI uri;

	private boolean changeCredentials = false;

	private String oldUser;

	LoginDialog(Shell shell, URI uri) {
		super(shell);
		this.uri = uri;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		String text = changeCredentials ? Messages.LoginDialog_changeStoredCredentials 
				: Messages.LoginDialog_login;
		getShell().setText(text);

		Label uriLabel = new Label(composite, SWT.NONE);
		uriLabel.setText(Messages.LoginDialog_repository);
		Text uriText = new Text(composite, SWT.READ_ONLY);
		uriText.setText(uri.toString());

		Label userLabel = new Label(composite, SWT.NONE);
		userLabel.setText(Messages.LoginDialog_user);
		user = new Text(composite, SWT.BORDER);
		if (oldUser != null)
			user.setText(oldUser);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(user);

		Label passwordLabel = new Label(composite, SWT.NONE);
		passwordLabel.setText(Messages.LoginDialog_password);
		password = new Text(composite, SWT.PASSWORD | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(password);

		if (!changeCredentials) {
			Label storeLabel = new Label(composite, SWT.NONE);
			storeLabel.setText(Messages.LoginDialog_storeInSecureStore);
			storeCheckbox = new Button(composite, SWT.CHECK);
			storeCheckbox.setSelection(Activator.getDefault().getPreferenceStore()
					.getBoolean(PreferenceConstants.PREFERENCES_STORE_SECURESTORE));
		}

		user.setFocus();

		return composite;
	}

	UsernamePasswordCredentials getCredentials() {
		return credentials;
	}

	void setChangeCredentials(boolean changeCredentials) {
		this.changeCredentials = changeCredentials;
	}

	boolean getStoreInSecureStore() {
		return storeInSecureStore;
	}

	public void setOldUser(String oldUser) {
		this.oldUser = oldUser;
	}

	@Override
	protected void okPressed() {
		if (user.getText().length() > 0) {
			credentials = new UsernamePasswordCredentials(user.getText(), password.getText());
			if (!changeCredentials)
				storeInSecureStore = storeCheckbox.getSelection();
		}
		super.okPressed();
	}

}
