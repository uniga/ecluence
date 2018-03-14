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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testing that a string matches a given prefix and if so, if the part of the
 * string following the prefix is the suffix of the given identifier.
 * 
 * Replaces all non-word characters in the identifier with dash (-), for example
 * 'dk.uniga.ecluence' matches 'dk-uniga-ecluence'.
 * 
 * For example: 
 * <pre>
 * assertTrue(new IdentifierSuffixPredicate("code-class-suffix-")
 *     .setIdentifier("LoginFacade")
 *     .test("code-class-suffix-facade"));
 * </pre>
 */
public class IdentifierSuffixPredicate implements IdentifierPredicate {

	private static final Logger log = LoggerFactory.getLogger(IdentifierSuffixPredicate.class);

	private final String stringPrefix;

	private String identifier;
	
	public IdentifierSuffixPredicate(String stringPrefix) {
		this.stringPrefix = requireNonNull(stringPrefix);
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public boolean test(String string) {
		if (!StringUtils.startsWithIgnoreCase(string, stringPrefix) || identifier == null) {
			return false;
		}
		String suffix = StringUtils.substringAfter(string, stringPrefix);
		String id = identifier.replaceAll("[\\W]+", "-");
		log.debug("test suffix {} of identifier {}", suffix, id);
		return StringUtils.endsWithIgnoreCase(id, suffix);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("stringPrefix", stringPrefix)
				.append("identifier", identifier).toString();
	}
}