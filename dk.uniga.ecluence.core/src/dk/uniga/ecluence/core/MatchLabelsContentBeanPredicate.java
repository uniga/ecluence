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
package dk.uniga.ecluence.core;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelsBean;
import dk.uniga.ecluence.core.matching.MatchExplainer;
import dk.uniga.ecluence.core.matching.MatchExplanation;
import dk.uniga.ecluence.core.matching.MatchIdentifierByLabelExplanation;
import dk.uniga.ecluence.core.matching.NoSelectionMatchExplanation;
import dk.uniga.ecluence.core.matching.SelectionDescription;

/**
 * Tests labels of a given {@link ContentBean} against a predicate.
 */
// TODO The predicate and MatchExplainer responsibilities should probably be separated.
public class MatchLabelsContentBeanPredicate implements Predicate<ContentBean>, MatchExplainer {

	private final Predicate<String> labelPredicate;

	public MatchLabelsContentBeanPredicate(Predicate<String> labelPredicate) {
		this.labelPredicate = requireNonNull(labelPredicate);
	}

	@Override
	public boolean test(ContentBean content) {
		Stream<String> labelStream = getLabelStream(content);
		return labelStream.anyMatch(labelPredicate);
	}

	@Override
	public MatchExplanation getMatchExplanation(SelectionDescription selection, ContentBean content) {
		List<String> matchingLabels = getLabelStream(content).filter(labelPredicate).collect(Collectors.toList());
		if (matchingLabels.isEmpty())
			return new NoSelectionMatchExplanation();
		return new MatchIdentifierByLabelExplanation(selection, matchingLabels);
	}

	private Stream<String> getLabelStream(ContentBean content) {
		LabelsBean labels = content.getMetadata().getLabels();
		return labels.getResults().stream().map(label -> label.getName());
	}
}