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

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.matching.IdentifierProvider;
import dk.uniga.ecluence.core.matching.SelectionDescription;

/**
 * Checks if the selection is a Java compilation unit and if so, provides a
 * string identifier and {@link SelectionDescription} for the class.
 */
public class ClassSelectionIdentifierProvider implements IdentifierProvider {

	private static final Logger log = LoggerFactory.getLogger(ClassSelectionIdentifierProvider.class);

	@Override
	public Optional<String> getIdentifier(Object o) {
		log.debug("getIdentifier({})", o == null ? null : o.getClass());
		if (o instanceof IType) {
			log.debug("type", o);
			return Optional.of(((IType) o).getElementName());
		}
		if (o instanceof IMember) {
			ICompilationUnit cu = ((IMember) o).getCompilationUnit();
			log.debug("member");
			if (cu != null)
				return Optional.of(getCompilationUnitName(cu));
		}
		if (o instanceof ICompilationUnit) {
			ICompilationUnit c = (ICompilationUnit) o;
			String name = getCompilationUnitName(c);
			log.debug("compilation unit {}", name);
			return Optional.of(name);
		}
		return Optional.empty();
	}

	@Override
	public Optional<SelectionDescription> getSelectionDescription(Object o) {
		if (o instanceof IType) {
			return Optional.of(new SelectionOfTypeDescription(((IType) o).getElementName(), "Java type"));
		}
		if (o instanceof IMember) {
			ICompilationUnit cu = ((IMember) o).getCompilationUnit();
			if (cu != null)
				return Optional.of(new SelectionOfTypeDescription(getCompilationUnitName(cu), "Java class"));
		}
		if (o instanceof ICompilationUnit) {
			return Optional.of(new SelectionOfTypeDescription(getCompilationUnitName(((ICompilationUnit) o)), "Java class"));
		}
		return Optional.empty();
	}
	
	private String getCompilationUnitName(ICompilationUnit c) {
		String name = c.getElementName();
		if (name.endsWith(".java"))
			name = StringUtils.substringBefore(name, ".java");
		return name;
	}
}