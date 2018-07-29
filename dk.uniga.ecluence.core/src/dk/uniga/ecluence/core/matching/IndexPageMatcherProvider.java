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
import java.util.function.Supplier;

import dk.uniga.ecluence.core.cache.ContentProvider;

public final class IndexPageMatcherProvider extends AbstractContentMatcherProvider {

	private final String label;

	public IndexPageMatcherProvider(String label) {
		this.label = requireNonNull(label);
	}

	@Override
	protected Collection<ContentMatcher> createContentMatchers(Supplier<ContentProvider> contentProviderSupplier) {
		return Collections.singleton(new IndexPageMatcher(contentProviderSupplier, label));
	}

}
