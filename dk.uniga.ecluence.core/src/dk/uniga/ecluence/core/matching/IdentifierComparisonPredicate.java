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

import java.util.function.BiPredicate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testing that a string matches a given prefix and if so, if the part of the
 * string following the prefix matches the given identifier using a delegate
 * predicate. Always evaluates to <code>false</code> if identifier is
 * <code>null</code>.
 * 
 * Replaces all non-word characters in the identifier with dash (-), for example
 * 'dk.uniga.ecluence' matches 'dk-uniga-ecluence'.
 * 
 * For example:
 * 
 * <pre>
 * assertTrue(
 * 		new IdentifierEqualsPredicate("code-project-").setIdentifier("client.api").test("code-project-client-api"));
 * </pre>
 */
public class IdentifierComparisonPredicate implements IdentifierPredicate {

	private static final Logger log = LoggerFactory.getLogger(IdentifierComparisonPredicate.class);
	
	private final BiPredicate<String, String> delegate;

	private final String stringPrefix;

	private String identifier;

	protected IdentifierComparisonPredicate(BiPredicate<String, String> delegate, String stringPrefix) {
		this.delegate = requireNonNull(delegate);
		this.stringPrefix = requireNonNull(stringPrefix);
	}
	
	public IdentifierPredicate setIdentifier(String identifier) {
		this.identifier = identifier;
		return this;
	}

	@Override
	public boolean test(String string) {
		if (!StringUtils.startsWithIgnoreCase(string, stringPrefix) || identifier == null) {
			return false;
		}
		String afterPrefix = StringUtils.substring(string, stringPrefix.length());
		String id = identifier.replaceAll("[\\W]+", "-");
		log.debug("test {} equals identifier {}", afterPrefix, id);
		return delegate.test(id, afterPrefix);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("delegate", delegate)
				.append("stringPrefix", stringPrefix)
				.append("identifier", identifier).toString();
	}
}
