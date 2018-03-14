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
package dk.uniga.ecluence.core.cache;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.itboehmer.confluence.rest.client.ContentClient;
import de.itboehmer.confluence.rest.core.RestException;
import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.QueryException;

public class ContentExpanderImplTest {

	@Mock ContentClient client;
	@Mock ContentBean bean;
	@Mock RestException restException;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test(expected = NullPointerException.class)
	public void testConstructorNullClient() throws Exception {
		new ContentExpanderImpl(null, Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullList() throws Exception {
		new ContentExpanderImpl(client, null);
	}

	@Test(expected = NullPointerException.class)
	public void testExpandNullBean() throws Exception {
		ContentExpander expander = new ContentExpanderImpl(client, Collections.emptyList());
		expander.expand(null);
	}

	@Test
	public void testExpand() throws Exception {
		ContentExpander expander = new ContentExpanderImpl(client, Collections.emptyList());
		when(bean.getId()).thenReturn("a");
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		when(client.getContentById(eq("a"), eq(0), eq(Collections.emptyList())))
			.thenReturn(executorService.submit(() -> bean));
		expander.expand(bean);
	}

	@Test(expected = QueryException.class)
	public void testExpandRestException() throws Exception {
		ContentExpander expander = new ContentExpanderImpl(client, Collections.emptyList());
		when(bean.getId()).thenReturn("a");
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		when(client.getContentById(eq("a"), eq(0), eq(Collections.emptyList())))
			.thenReturn(executorService.submit(() -> { throw restException; }));
		expander.expand(bean);
	}

}
