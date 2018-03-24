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
package dk.uniga.ecluence.jdt.matching;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import dk.uniga.ecluence.core.MatchLabelsContentBeanPredicateFactory;
import dk.uniga.ecluence.core.cache.ContentProvider;
import dk.uniga.ecluence.core.matching.AbstractContentMatcherProvider;
import dk.uniga.ecluence.core.matching.ContentMatcher;
import dk.uniga.ecluence.core.matching.IdentifierEqualsPredicate;
import dk.uniga.ecluence.core.matching.IdentifierLabelContentMatcher;
import dk.uniga.ecluence.core.matching.IdentifierPrefixPredicate;
import dk.uniga.ecluence.core.matching.IdentifierSuffixPredicate;
import dk.uniga.ecluence.core.matching.MultipleIdentifierBasedContentMatcher;

/**
 * Creates {@link ContentMatcher}s that can match content to Java selections in
 * the workspace and the editor.
 */
public class JavaContentMatcherProvider extends AbstractContentMatcherProvider {

	@Override
	protected Collection<ContentMatcher> createContentMatchers(Supplier<ContentProvider> contentProviderSupplier) {
		MatchLabelsContentBeanPredicateFactory predicateFactory = new MatchLabelsContentBeanPredicateFactory();
		return Arrays.asList(new ContentMatcher[] {
				new IdentifierLabelContentMatcher(contentProviderSupplier, new IdentifierPrefixPredicate("code-class-prefix-"),
						predicateFactory, new ClassSelectionIdentifierProvider()),
				new IdentifierLabelContentMatcher(contentProviderSupplier, new IdentifierSuffixPredicate("code-class-suffix-"),
						predicateFactory, new ClassSelectionIdentifierProvider()),
				new MultipleIdentifierBasedContentMatcher(contentProviderSupplier, new IdentifierEqualsPredicate("code-extends-"),
						predicateFactory, new SuperClassProvider()),
				new IdentifierLabelContentMatcher(contentProviderSupplier, new IdentifierPrefixPredicate("code-project-prefix-"),
						predicateFactory, new ProjectSelectionIdentifierProvider()),
				new IdentifierLabelContentMatcher(contentProviderSupplier, new IdentifierSuffixPredicate("code-project-suffix-"),
						predicateFactory, new ProjectSelectionIdentifierProvider())
		});
	}

}
