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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.matching.IdentifierProvider;
import dk.uniga.ecluence.core.matching.SelectionDescription;

public class ProjectSelectionIdentifierProvider implements IdentifierProvider {

	private static final Logger log = LoggerFactory.getLogger(ProjectSelectionIdentifierProvider.class);

	@Override
	public Optional<String> getIdentifier(Object o) {
		return Optional.ofNullable(getName(o));
	}

	private String getName(Object o) {
		if (o instanceof IProject) {
			String name = ((IProject) o).getName();
			log.debug("IProject {}", name);
			return name;
		}
		if (o instanceof IResource) {
			log.debug("IResource {}", ((IResource) o).getName());
			IProject project = ((IResource) o).getProject();
			return project.getName();
		}
		if (o instanceof ICompilationUnit) {
			ICompilationUnit c = (ICompilationUnit) o;
			log.debug("Compilation unit {}", c.getElementName());
			return c.getJavaProject().getElementName();
		}
		if (o instanceof IJavaProject) {
			IJavaProject p = (IJavaProject) o;
			log.debug("Java project {}", p.getElementName());
			return p.getElementName();
		}
		if (o instanceof IJavaElement) {
			IJavaElement e = (IJavaElement) o;
			log.debug("Java element {}", e.getElementName());
			IJavaProject p = e.getJavaProject();
			return p.getElementName();
		}
		return null;
	}

	@Override
	public Optional<SelectionDescription> getSelectionDescription(Object o) {
		if (o instanceof IProject) {
			return Optional.of(new SelectionOfTypeDescription(((IProject) o).getName(), "Project"));
		}
		if (o instanceof IResource) {
			IProject project = ((IResource) o).getProject();
			return Optional.of(new SelectionPartOfDescription("Resource", ((IResource) o).getName(), "Project",
					project.getName()));
		}
		if (o instanceof ICompilationUnit) {
			ICompilationUnit c = (ICompilationUnit) o;
			return Optional.of(new SelectionPartOfDescription("Class", c.getElementName(), "Project",
					c.getJavaProject().getElementName()));
		}
		if (o instanceof IJavaProject) {
			IJavaProject p = (IJavaProject) o;
			return Optional.of(new SelectionOfTypeDescription(p.getElementName(), "Project"));
		}
		if (o instanceof IJavaElement) {
			IJavaElement e = (IJavaElement) o;
			IJavaProject p = e.getJavaProject();
			return Optional.of(new SelectionPartOfDescription("Java element", e.getElementName(), "Project",
					p.getElementName()));
		}
		return null;
	}
}
