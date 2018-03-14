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
package dk.uniga.ecluence.ui.preferences;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.IInputValidator;

public class URLValidator implements IInputValidator {
	
	public static boolean validate(String url) {
		try {
			new URL(url);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	@Override
	public String isValid(String url) {
		if (!validate(url))
			return "The entered location is not a valid URL";
		return null;
	}

}