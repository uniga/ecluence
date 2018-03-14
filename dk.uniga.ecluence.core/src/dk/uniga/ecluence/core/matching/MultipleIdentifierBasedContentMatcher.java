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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.MatchLabelsContentBeanPredicate;
import dk.uniga.ecluence.core.MatchLabelsContentBeanPredicateFactory;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.cache.ContentProvider;

/**
 * Matches pages to one of several identifiers provided by a
 * {@link MultipleIdentifierProvider}.
 */
public class MultipleIdentifierBasedContentMatcher implements ContentMatcher {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(MultipleIdentifierBasedContentMatcher.class);

	private final IdentifierPredicate identifierPredicate;

	private final Supplier<ContentProvider> contentProviderSupplier;

	private final MatchLabelsContentBeanPredicateFactory predicateFactory;

	private final MultipleIdentifierProvider identifierProvider;

	/**
	 * Constructs a new matcher that produces matches for multiple identifiers
	 * provided by the given {@link MultipleIdentifierProvider}.
	 * 
	 * @param contentProviderSupplier
	 * @param identifierPredicate
	 * @param predicateFactory
	 * @param identifierProvider
	 */
	public MultipleIdentifierBasedContentMatcher(Supplier<ContentProvider> contentProviderSupplier,
			IdentifierPredicate identifierPredicate, MatchLabelsContentBeanPredicateFactory predicateFactory,
			MultipleIdentifierProvider identifierProvider) {
		this.contentProviderSupplier = requireNonNull(contentProviderSupplier);
		this.identifierPredicate = requireNonNull(identifierPredicate);
		this.predicateFactory = requireNonNull(predicateFactory);
		this.identifierProvider = requireNonNull(identifierProvider);
	}

	@Override
	public Collection<ContentMatch> getMatches(Object o) throws QueryException {
		List<ContentMatch> matches = new ArrayList<>();
		ContentProvider contentProvider = contentProviderSupplier.get();
		if (contentProvider != null) {
			Collection<IdentifierProvider> providers = identifierProvider.getIdentifierProviders(o);
			contentProvider.getPages().stream().forEach((page) -> {
				for (IdentifierProvider provider : providers) {
					identifierPredicate.setIdentifier(provider.getIdentifier(o).get());
					MatchLabelsContentBeanPredicate p = predicateFactory.getPredicate(identifierPredicate);
					if (p.test(page))
						matches.add(new ContentMatch(page,
								p.getMatchExplanation(provider.getSelectionDescription(o).get(), page)));
				}
			});
		}
		return matches;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(identifierPredicate)
				.append(identifierProvider).toString();
	}
}
