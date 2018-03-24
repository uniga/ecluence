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

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.MatchLabelsContentBeanPredicate;
import dk.uniga.ecluence.core.MatchLabelsContentBeanPredicateFactory;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.cache.ContentProvider;

public class IdentifierLabelContentMatcher implements ContentMatcher {

	private static final Rank RANK = new Rank(10);

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(IdentifierLabelContentMatcher.class);

	private final IdentifierPredicate identifierPredicate;

	private final Supplier<ContentProvider> contentProviderSupplier;

	private final MatchLabelsContentBeanPredicateFactory predicateFactory;

	private final IdentifierProvider identifierProvider;

	public IdentifierLabelContentMatcher(Supplier<ContentProvider> contentProviderSupplier,
			IdentifierPredicate identifierPredicate, MatchLabelsContentBeanPredicateFactory predicateFactory,
			IdentifierProvider identifierProvider) {
		this.contentProviderSupplier = requireNonNull(contentProviderSupplier);
		this.identifierPredicate = requireNonNull(identifierPredicate);
		this.predicateFactory = requireNonNull(predicateFactory);
		this.identifierProvider = requireNonNull(identifierProvider);
	}

	@Override
	public Collection<ContentMatch> getMatches(Object o) throws QueryException {
		Optional<String> identifier = identifierProvider.getIdentifier(o);
		if (identifier.isPresent())
			return findMatching(identifier.get(), identifierProvider.getSelectionDescription(o).get());
		return Collections.emptyList();
	}

	private Collection<ContentMatch> findMatching(String identifier, SelectionDescription selectionDescription)
			throws QueryException {
		ContentProvider pageProvider = contentProviderSupplier.get();
		if (pageProvider != null) {
			identifierPredicate.setIdentifier(identifier);
			MatchLabelsContentBeanPredicate matchPredicate = predicateFactory.getPredicate(identifierPredicate);
			return pageProvider.getPages().stream()
					.filter(matchPredicate)
					.map(getExplainer(matchPredicate, selectionDescription))
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private Function<ContentBean, ContentMatch> getExplainer(MatchLabelsContentBeanPredicate matchPredicate,
			SelectionDescription description) {
		return (page) -> {
			MatchExplanation explanation = matchPredicate.getMatchExplanation(description, page);
			return new ContentMatch(page, explanation, RANK);
		};
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(identifierPredicate)
				.append(identifierProvider).toString();
	}
}
