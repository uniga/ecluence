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
package dk.uniga.ecluence.ui.template;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.FileLocator;

import dk.uniga.ecluence.ui.Activator;

public class BuiltinTemplateProvider implements TemplateProvider {

	private static final String TEMPLATE = "platform:/plugin/dk.uniga.ecluence.ui/files/template.html";

	private static final String AUTHOR_ICON = "platform:/plugin/dk.uniga.ecluence.ui/files/default-avatar.png";

	@Override
	public Template getTemplate() {
		try {
			String template = IOUtils.toString(new URL(TEMPLATE));
			template = TemplateLinkReplacer.replaceLocalLinks(template);
			URL iconUrl = FileLocator.toFileURL(new URL(AUTHOR_ICON));
			return new Template(template, iconUrl);
		} catch (IOException e) {
			Activator.handleError("Internal error: cannot find files linked from template", e, true);
		}
		return new Template("<html><body>##CONTENT##</body></html>", null);
	}

}
