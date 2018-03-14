package dk.uniga.ecluence.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;

import dk.uniga.ecluence.core.preferences.PreferenceConstants;
import dk.uniga.ecluence.ui.Activator;

public class SitesMenuItem {

	@Execute
	public void execute(MDirectMenuItem item) {
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.PREFERENCE_SELECTED_LOCATION_KEY_NAME, item.getLabel());
	}
}
