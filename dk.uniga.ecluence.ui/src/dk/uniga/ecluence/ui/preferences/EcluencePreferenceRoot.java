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
package dk.uniga.ecluence.ui.preferences;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import dk.uniga.ecluence.core.preferences.PreferenceConstants;
import dk.uniga.ecluence.ui.Activator;

public class EcluencePreferenceRoot extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public EcluencePreferenceRoot() {
		super(GRID);
		setDescription("Manage the Confluence sites to get content from.");
	}

	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		final ListEditor locations = new ListEditor(PreferenceConstants.PREFERENCE_LOCATION_KEY_NAME, "Confluence locations:",
				parent) {
			@Override
			protected String getNewInputObject() {
				final InputDialog dialog = new InputDialog(getShell(), "Add a Confluence location",
						"Enter the URL of the Confluence site", null, new URLValidator());
				if (dialog.open() != Window.OK) {
					return null;
				}

				final String newRepositoryLocation = dialog.getValue();
				if (!StringUtils.isNotBlank(newRepositoryLocation)) {
					return null;
				}

				return newRepositoryLocation;
			}

			@Override
			protected String[] parseString(String stringList) {
				return StringUtils.split(stringList, ';');
			}

			@Override
			protected String createList(String[] items) {
				return StringUtils.join(items, ';');
			}
		};

		locations.loadDefault();
		addField(locations);
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

}