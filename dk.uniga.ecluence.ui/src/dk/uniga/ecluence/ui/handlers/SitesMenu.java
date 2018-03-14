package dk.uniga.ecluence.ui.handlers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.preference.IPreferenceStore;

import dk.uniga.ecluence.core.preferences.PreferenceConstants;
import dk.uniga.ecluence.ui.Activator;

public class SitesMenu {
	
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items, EModelService modelService) {
		String locations = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.PREFERENCE_LOCATION_KEY_NAME);
		String[] locs = StringUtils.split(locations, ';');
		for (String location : locs) {
			items.add(createMenuItem(location, modelService, isSelectedLocation(location)));
		}
	}
	
	private boolean isSelectedLocation(String location) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return (store.contains(PreferenceConstants.PREFERENCE_SELECTED_LOCATION_KEY_NAME) &&
				store.getString(PreferenceConstants.PREFERENCE_SELECTED_LOCATION_KEY_NAME).equals(location));
	}

	private MMenuElement createMenuItem(String location, EModelService modelService, boolean selected) {
		MDirectMenuItem item = modelService.createModelElement(MDirectMenuItem.class);
	    item.setType(ItemType.CHECK);
	    item.setSelected(selected);
	    item.setLabel(location);
	    item.setContributorURI("platform:/plugin/dk.uniga.ecluence.ui");
	    item.setContributionURI("bundleclass://dk.uniga.ecluence.ui/dk.uniga.ecluence.ui.handlers.SitesMenuItem");
	    return item;
	}
}
