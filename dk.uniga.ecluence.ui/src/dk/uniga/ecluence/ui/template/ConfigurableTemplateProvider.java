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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.preference.IPreferenceStore;

import dk.uniga.ecluence.core.preferences.PreferenceConstants;

public class ConfigurableTemplateProvider implements TemplateProvider {

	private final IPreferenceStore preferenceStore;
	private final TemplateProvider defaultTemplateProvider;

	public ConfigurableTemplateProvider(IPreferenceStore preferenceStore,
			TemplateProvider defaultTemplateProvider) {
				this.preferenceStore = preferenceStore;
				this.defaultTemplateProvider = defaultTemplateProvider;
	}

	@Override
	public Template getTemplate() {
		if (preferenceStore.contains(PreferenceConstants.PREFERENCE_SELECTED_TEMPLATE)) {
			String templateFile = preferenceStore.getString(PreferenceConstants.PREFERENCE_SELECTED_TEMPLATE);
			try {
				String template = IOUtils.toString(new File(templateFile).toURI());
				template = TemplateLinkReplacer.replaceLocalLinks(template);
				return new Template(template, null);
			} catch (IOException e) {
			}
		}
		return defaultTemplateProvider.getTemplate();
	}

}
