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
package dk.uniga.ecluence.core.matching;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

public interface MatchExplainer {
	/**
	 * Returns an explanation of the given match, {@link NoSelectionMatchExplanation} if no matches.
	 * @param selection
	 * @param content
	 * @return
	 */
	MatchExplanation getMatchExplanation(SelectionDescription selection, ContentBean content);
}
