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
package dk.uniga.ecluence.core.workspace;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.Test;

import dk.uniga.ecluence.jdt.matching.ProjectSelectionIdentifierProvider;

public class ProjectSelectionIdentifierProviderTest {

	ProjectSelectionIdentifierProvider provider = new ProjectSelectionIdentifierProvider();
	
	@Test
	public void testHandleSelectionNull() throws Exception {
		assertEquals(provider.getIdentifier(null), Optional.empty());
	}
	
	@Test
	public void testHandleSelectionProject() throws Exception {
		IProject project = mock(IProject.class);
		when(project.getName()).thenReturn("name");
		assertEquals(provider.getIdentifier(project), Optional.of("name"));
		verify(project).getName();
	}

	@Test
	public void testHandleSelectionResource() throws Exception {
		IProject project = mock(IProject.class);
		IResource resource = mock(IResource.class);
		when(resource.getProject()).thenReturn(project);
		when(project.getName()).thenReturn("name");
		assertEquals(provider.getIdentifier(resource), Optional.of("name"));
		verify(project).getName();
	}

	@Test
	public void testHandleSelectionCompilationUnit() throws Exception {
		ICompilationUnit compilationUnit = mock(ICompilationUnit.class);
		IJavaProject javaProject = mock(IJavaProject.class);
		when(compilationUnit.getJavaProject()).thenReturn(javaProject);
		when(javaProject.getElementName()).thenReturn("name");
		assertEquals(provider.getIdentifier(compilationUnit), Optional.of("name"));
		verify(javaProject).getElementName();
	}

}
