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
package dk.uniga.ecluence.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelsBean;
import de.itboehmer.confluence.rest.core.domain.content.MetadataBean;
import dk.uniga.ecluence.core.matching.IdentifierPredicate;
import dk.uniga.ecluence.core.matching.MatchExplanation;
import dk.uniga.ecluence.core.matching.MatchIdentifierByLabelExplanation;
import dk.uniga.ecluence.core.matching.NoSelectionMatchExplanation;
import dk.uniga.ecluence.core.matching.SelectionDescription;

public class MatchLabelsContentBeanPredicateTest {

	@Mock IdentifierPredicate identifierPredicate;
	@Mock ContentBean bean;
	@Mock MetadataBean metadata;
	@Mock LabelsBean labels;
	@Mock LabelBean label;
	@Mock SelectionDescription selectionDescription;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullPredicate() throws Exception {
		new MatchLabelsContentBeanPredicate(null);
	}

	@Test(expected = NullPointerException.class)
	public void testTestNullContent() throws Exception {
		MatchLabelsContentBeanPredicate predicate = new MatchLabelsContentBeanPredicate(identifierPredicate);
		predicate.test(null);
	}

	@Test
	public void testTestMatchingContent() throws Exception {
		MatchLabelsContentBeanPredicate predicate = new MatchLabelsContentBeanPredicate(identifierPredicate);
		when(bean.getMetadata()).thenReturn(metadata);
		when(metadata.getLabels()).thenReturn(labels);
		when(labels.getResults()).thenReturn(Collections.singletonList(label));
		when(label.getName()).thenReturn("a");
		when(identifierPredicate.test(anyString())).thenReturn(true);
		
		assertTrue(predicate.test(bean));
		verify(identifierPredicate).test(eq("a"));
	}

	@Test
	public void testTestNoMatchingContent() throws Exception {
		MatchLabelsContentBeanPredicate predicate = new MatchLabelsContentBeanPredicate(identifierPredicate);
		when(bean.getMetadata()).thenReturn(metadata);
		when(metadata.getLabels()).thenReturn(labels);
		when(labels.getResults()).thenReturn(Collections.singletonList(label));
		when(label.getName()).thenReturn("a");
		when(identifierPredicate.test(anyString())).thenReturn(false);
		
		assertFalse(predicate.test(bean));
		verify(identifierPredicate).test(eq("a"));
	}

	@Test
	public void testGetMatchExplanationMatchingContent() throws Exception {
		MatchLabelsContentBeanPredicate predicate = new MatchLabelsContentBeanPredicate(identifierPredicate);
		when(bean.getMetadata()).thenReturn(metadata);
		when(metadata.getLabels()).thenReturn(labels);
		when(labels.getResults()).thenReturn(Collections.singletonList(label));
		when(label.getName()).thenReturn("a");
		when(identifierPredicate.test(anyString())).thenReturn(true);
		when(selectionDescription.getDefaultText()).thenReturn("sel");
		
		MatchExplanation explanation = predicate.getMatchExplanation(selectionDescription, bean);
		assertTrue(explanation instanceof MatchIdentifierByLabelExplanation);
		MatchIdentifierByLabelExplanation mible = (MatchIdentifierByLabelExplanation) explanation;
		assertEquals("Matches sel by label 'a'", mible.getDefaultText());
		assertEquals("sel", mible.getSelection().getDefaultText());
		assertEquals(Collections.singletonList("a"), mible.getMatchingLabels());

		verify(identifierPredicate).test(eq("a"));
	}

	@Test
	public void testGetMatchExplanationNoMatchingContent() throws Exception {
		MatchLabelsContentBeanPredicate predicate = new MatchLabelsContentBeanPredicate(identifierPredicate);
		when(bean.getMetadata()).thenReturn(metadata);
		when(metadata.getLabels()).thenReturn(labels);
		when(labels.getResults()).thenReturn(Collections.singletonList(label));
		when(label.getName()).thenReturn("a");
		when(identifierPredicate.test(anyString())).thenReturn(false);
		
		MatchExplanation explanation = predicate.getMatchExplanation(selectionDescription, bean);
		assertTrue(explanation instanceof NoSelectionMatchExplanation);
		
		verify(identifierPredicate).test(eq("a"));
	}

}
