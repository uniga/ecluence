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
package dk.uniga.ecluence.jdt.matching;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import dk.uniga.ecluence.core.MatchLabelsContentBeanPredicateFactory;
import dk.uniga.ecluence.core.cache.ContentProvider;
import dk.uniga.ecluence.core.matching.AbstractContentMatcherProvider;
import dk.uniga.ecluence.core.matching.ContentMatcher;
import dk.uniga.ecluence.core.matching.IdentifierLabelContentMatcher;
import dk.uniga.ecluence.core.matching.IdentifierPrefixPredicate;
import dk.uniga.ecluence.core.matching.IdentifierSuffixPredicate;

public class AntContentMatcherProvider extends AbstractContentMatcherProvider {

	@Override
	protected Collection<ContentMatcher> createContentMatchers(Supplier<ContentProvider> contentProviderSupplier) {
		MatchLabelsContentBeanPredicateFactory predicateFactory = new MatchLabelsContentBeanPredicateFactory();
		return Arrays.asList(new ContentMatcher[] {
				new IdentifierLabelContentMatcher(contentProviderSupplier, new IdentifierPrefixPredicate("code-ant-prefix-"),
						predicateFactory, new AntSelectionIdentifierProvider()),
				new IdentifierLabelContentMatcher(contentProviderSupplier, new IdentifierSuffixPredicate("code-ant-suffix-"),
						predicateFactory, new AntSelectionIdentifierProvider())
		});
	}

}
