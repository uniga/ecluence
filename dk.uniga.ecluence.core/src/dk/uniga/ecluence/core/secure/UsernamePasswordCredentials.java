/*******************************************************************************
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

/**
 * Credentials for authenticating a username with username and password.
 */
public class UsernamePasswordCredentials {

	private final String username;
	private final String password;

	public UsernamePasswordCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return password
	 */
	public String getPassword() {
		return password;
	}
}
