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
package dk.uniga.ecluence.core.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelsBean;
import de.itboehmer.confluence.rest.core.domain.content.MetadataBean;
import dk.uniga.ecluence.core.cache.ContentQueryImpl;

public class ContentQueryImplTest {

	@Mock
	ContentBean bean;
	
	@Mock
	MetadataBean metadata;

	@Mock LabelsBean labels;

	LabelBean label1 = new LabelBean("", "a");

	LabelBean label2 = new LabelBean("", "b");

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullLabels() throws Exception {
		new ContentQueryImpl(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructNoLabels() throws Exception {
		new ContentQueryImpl(Arrays.asList());
	}

	@Test(expected = NullPointerException.class)
	public void testMatchesNull() throws Exception {
		new ContentQueryImpl(Arrays.asList("a")).matches(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalLabels() throws Exception {
		new ContentQueryImpl(Arrays.asList("a b")).getCQL();
	}

	@Test
	public void testGetLabels() throws Exception {
		Collection<String> labels = new ContentQueryImpl(Arrays.asList("a")).getLabels();
		assertEquals(labels.size(), 1);
		assertEquals(labels.iterator().next(), "a");
	}

	@Test
	public void testMatchesSingleLabel() throws Exception {
		when(bean.getMetadata()).thenReturn(metadata);
		when(metadata.getLabels()).thenReturn(labels);
		when(labels.getResults()).thenReturn(Arrays.asList(label1));
		boolean matches = new ContentQueryImpl(Arrays.asList("a")).matches(bean);
		assertTrue(matches);
	}

	@Test
	public void testMatchesSingleLabelNoMatch() throws Exception {
		when(bean.getMetadata()).thenReturn(metadata);
		when(metadata.getLabels()).thenReturn(labels);
		when(labels.getResults()).thenReturn(Arrays.asList(label1));
		boolean matches = new ContentQueryImpl(Arrays.asList("b")).matches(bean);
		assertFalse(matches);
	}
	
	@Test
	public void testMatchesMultipleLabels() throws Exception {
		when(bean.getMetadata()).thenReturn(metadata);
		when(metadata.getLabels()).thenReturn(labels);
		when(labels.getResults()).thenReturn(Arrays.asList(label1, label2));
		boolean matches = new ContentQueryImpl(Arrays.asList("b", "c")).matches(bean);
		assertTrue(matches);
	}

	@Test
	public void testGetCQL() throws Exception {
		String cql = new ContentQueryImpl(Arrays.asList("b", "c")).getCQL();
		assertEquals("type = page and label in (\"b\",\"c\")", cql);
	}
	
}
