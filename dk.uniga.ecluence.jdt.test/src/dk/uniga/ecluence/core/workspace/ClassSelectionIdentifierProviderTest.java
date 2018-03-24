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
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.eclipse.jdt.core.ICompilationUnit;
import org.junit.Before;
import org.junit.Test;

import dk.uniga.ecluence.jdt.matching.ClassSelectionIdentifierProvider;

public class ClassSelectionIdentifierProviderTest {

	ClassSelectionIdentifierProvider provider = new ClassSelectionIdentifierProvider();
	
	@Before
	public void setup() {
	}

	@Test
	public void testHandleSelectionNull() throws Exception {
		assertEquals(provider.getIdentifier(null), Optional.empty());
	}
	
	@Test
	public void testHandleSelectionUnknownObject() throws Exception {
		assertEquals(provider.getIdentifier(""), Optional.empty());
	}
	
	@Test
	public void testHandleSelectionCompilationUnit() throws Exception {
		ICompilationUnit compilationUnit = mock(ICompilationUnit.class);
		when(compilationUnit.getElementName()).thenReturn("name");
		Optional<String> identifier = provider.getIdentifier(compilationUnit);
		assertEquals(identifier, Optional.of("name"));
	}

	@Test
	public void testHandleSelectionJavaFile() throws Exception {
		ICompilationUnit compilationUnit = mock(ICompilationUnit.class);
		when(compilationUnit.getElementName()).thenReturn("name.java");
		Optional<String> identifier = provider.getIdentifier(compilationUnit);
		assertEquals(identifier, Optional.of("name"));
	}

}
