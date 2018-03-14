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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.matching.IdentifierProvider;
import dk.uniga.ecluence.core.matching.MultipleIdentifierProvider;
import dk.uniga.ecluence.core.matching.SelectionDescription;

/**
 * Checks if the selection is a Java compilation unit and if so, provides
 * multiple identifiers, an identifier for each of the super classes of the
 * given class.
 */
public class SuperClassProvider implements MultipleIdentifierProvider {

	private static final Logger log = LoggerFactory.getLogger(SuperClassProvider.class);

	public Collection<IdentifierProvider> getIdentifierProviders(Object o) {
		if (o instanceof ICompilationUnit) {
			ICompilationUnit c = (ICompilationUnit) o;
			return getSuperClassIdentifiers(c).stream()
					.map((id) -> new InternalIdentifierProvider(id,
							new SelectionExtendsDescription(getCompilationUnitName(c), id)))
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private Collection<String> getSuperClassIdentifiers(ICompilationUnit c) {
		Collection<String> identifiers = new LinkedList<>();
		try {
			for (IType type : c.getTypes()) {
				if (!"null".equals(type.getSuperclassName()))
					add(identifiers, type.getSuperclassName());
				for (String name : type.getSuperInterfaceNames()) {
					add(identifiers, name);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return identifiers;
	}

	private void add(Collection<String> identifiers, String className) {
		if (!"java.lang.Object".equals(className)) {
			if (StringUtils.contains(className, ".")) {
				String name = StringUtils.substringAfterLast(className, ".");
				log.debug("add {} from {}", name, className);
				identifiers.add(name);
			} else if (StringUtils.contains(className, "<"))
				identifiers.add(StringUtils.substringBefore(className, "<"));
		}
	}

	private String getCompilationUnitName(ICompilationUnit c) {
		String name = c.getElementName();
		if (name.endsWith(".java"))
			name = StringUtils.substringBefore(name, ".java");
		return name;
	}

	class InternalIdentifierProvider implements IdentifierProvider {

		private String identifier;
		private SelectionExtendsDescription selectionDescription;

		public InternalIdentifierProvider(String identifier, SelectionExtendsDescription selectionDescription) {
			this.identifier = identifier;
			this.selectionDescription = selectionDescription;
		}

		@Override
		public Optional<String> getIdentifier(Object o) {
			return Optional.of(identifier);
		}

		@Override
		public Optional<SelectionDescription> getSelectionDescription(Object o) {
			return Optional.of(selectionDescription);
		}
	}
}