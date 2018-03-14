package dk.uniga.ecluence.core.cache;

import java.util.ArrayList;
import java.util.List;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.misc.ExpandField;

public class ExpandFields {

	/**
	 * Set of fields to expand for {@link ContentBean}s that stored for
	 * presentation.
	 * 
	 * @return
	 */
	public static List<String> getFullExpandFields() {
		List<String> expand = new ArrayList<>();
		expand.add(ExpandField.BODY_VIEW.getName());
		expand.add(ExpandField.METADATA_LABELS.getName());
		expand.add(ExpandField.VERSION.getName());
		expand.add(ExpandField.HISTORY.getName());
		expand.add(ExpandField.ANCESTORS.getName());
		expand.add(ExpandField.CONTAINER.getName());
		expand.add(ExpandField.SPACE.getName());
		expand.add(ExpandField.CHILDREN.getName());
		return expand;
	}

}
