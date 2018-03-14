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
package dk.uniga.ecluence.ui.template;

import java.net.URL;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

public class Template {

	private String text;
	private URL iconUrl;
	
	public Template(String text, URL iconUrl) {
		this.text = text;
		this.iconUrl = iconUrl;
	}

	public String getText() {
		return text;
	}

	public String getAuthorIconUrl(ContentBean content) {
		return iconUrl != null ? iconUrl.toString() : "";
	}
}
