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

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Registry of {@link ContentMatcher}s that are provided by a one or more
 * ContentMatcherProviders.
 */
public class ContentMatcherRegistry implements ContentMatcherProvider {
	
	private final Set<ContentMatcherProvider> providers = new HashSet<>();
	
	/**
	 * Adds a provider for which this registry should retrieve
	 * {@link ContentMatcher}s. Does nothing if the provider is already in the
	 * registry.
	 * 
	 * @param provider
	 */
	public void addProvider(ContentMatcherProvider provider) {
		providers.add(Objects.requireNonNull(provider));
	}
	
	/**
	 * Removes a provider for which this registry should no longer retrieve
	 * {@link ContentMatcher}s. Does nothing if the provider is not already in the
	 * registry.
	 * 
	 * @param provider
	 */
	public void removeProvider(ContentMatcherProvider provider) {
		providers.remove(Objects.requireNonNull(provider));
	}
	
	@Override
	public Collection<ContentMatcher> getContentMatchers() {
		return providers.stream()
				.flatMap((provider) -> provider.getContentMatchers().stream())
				.collect(Collectors.toList());
	}
}
