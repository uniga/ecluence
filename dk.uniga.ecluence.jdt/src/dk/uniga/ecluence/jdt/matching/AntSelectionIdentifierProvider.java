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

import org.eclipse.ant.internal.ui.model.AntElementNode;
import org.eclipse.ant.internal.ui.model.AntProjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.matching.IdentifierProvider;
import dk.uniga.ecluence.core.matching.SelectionDescription;

@SuppressWarnings("restriction")
public class AntSelectionIdentifierProvider implements IdentifierProvider {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(AntSelectionIdentifierProvider.class);

	@Override
	public Optional<String> getIdentifier(Object o) {
		if (o instanceof AntProjectNode) {
			return getAntProject((AntProjectNode) o);
		}
		if (o instanceof AntElementNode) {
			AntProjectNode projectNode = ((AntElementNode) o).getProjectNode();
			if (projectNode != null)
				return getAntProject(projectNode);
		}
		return Optional.empty();
	}

	private Optional<String> getAntProject(AntProjectNode o) {
		return Optional.of(((AntProjectNode) o).getName());
	}

	@Override
	public Optional<SelectionDescription> getSelectionDescription(Object o) {
		if (o instanceof AntProjectNode) {
			AntProjectNode p = ((AntProjectNode) o);
			return Optional.of(new SelectionOfTypeDescription(p.getName(), "Ant project"));
		}
		if (o instanceof AntElementNode) {
			AntProjectNode p = ((AntElementNode) o).getProjectNode();
			if (p != null)
				return Optional.of(new SelectionOfTypeDescription(p.getName(), "Ant project"));
		}
		return Optional.empty();
	}
}