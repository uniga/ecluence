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
