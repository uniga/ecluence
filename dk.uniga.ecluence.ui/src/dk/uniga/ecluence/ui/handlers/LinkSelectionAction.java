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
 
package dk.uniga.ecluence.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.jface.preference.IPreferenceStore;

import dk.uniga.ecluence.core.preferences.PreferenceConstants;
import dk.uniga.ecluence.ui.Activator;

public class LinkSelectionAction {
	
	@Execute
	public void execute(MToolItem item) {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.setValue(PreferenceConstants.PREFERENCE_LINK_SELECTION, item.isSelected());
	}
	
}