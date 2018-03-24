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
package dk.uniga.ecluence.core.matching;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MatchIdentifierByLabelExplanation implements MatchExplanation {

	private final SelectionDescription selection;
	private final List<String> matchingLabels;

	public MatchIdentifierByLabelExplanation(SelectionDescription selection, List<String> matchingLabels) {
		this.selection = selection;
		this.matchingLabels = matchingLabels;
	}

	@Override
	public String getDefaultText() {
		return String.format("Matches %s by label '%s'", selection.getDefaultText(), matchingLabels.get(0));
	}

	public SelectionDescription getSelection() {
		return selection;
	}

	public List<String> getMatchingLabels() {
		return matchingLabels;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(selection)
				.append(StringUtils.join(matchingLabels, ',')).build();
	}
}
