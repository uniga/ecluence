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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.matching.IdentifierProvider;
import dk.uniga.ecluence.core.matching.MultipleIdentifierProvider;
import dk.uniga.ecluence.core.matching.SelectionDescription;

/**
 * Provides multiple identifiers for java files, an identifier for each of the
 * classes used (that is, if an instance of a class is used) in the given class.
 */
public class ImportClassProvider implements MultipleIdentifierProvider {

	private static final Logger log = LoggerFactory.getLogger(ImportClassProvider.class);

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
			for (IImportDeclaration imp : c.getImports()) {
				String importName = imp.getElementName();
				if (!importName.endsWith("*"))
					add(identifiers, importName);
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