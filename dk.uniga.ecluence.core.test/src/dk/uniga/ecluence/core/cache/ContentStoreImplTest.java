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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

public class ContentStoreImplTest {

	private static final String CONTENT_JSON = "{\"status\":\"current\",\"title\":\"Test\",\"id\":\"2097153\",\"type\":\"page\"}";
	private static final String INVALID_JSON = "{invalid-json";
	
	Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	
	File directoryPath;
			
	@Before
	public void setup() {
	}
	
	@After
	public void teardown() {
		try {
			if (directoryPath != null)
				Files.delete(directoryPath.toPath());
		} catch (IOException e) {
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void testCreateNullLocation() throws Exception {
		new ContentStoreImpl(null);
	}

	@Test(expected = IOException.class)
	public void testCreateIllegalLocation() throws Exception {
		new ContentStoreImpl(new File(""));
	}
	
	@Test
	public void testPutBean() throws Exception {
		directoryPath = createTestDirectory();
		ContentStoreImpl store = new ContentStoreImpl(directoryPath);
		store.putContent(new ContentBean("1"));
		assertTrue(Files.exists(new File(directoryPath, "content-1.json").toPath()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testPutBeanNoId() throws Exception {
		directoryPath = createTestDirectory();
		ContentStoreImpl store = new ContentStoreImpl(directoryPath);
		store.putContent(new ContentBean(null));
	}
	
	@Test(expected = NullPointerException.class)
	public void testPutNull() throws Exception {
		directoryPath = createTestDirectory();
		ContentStore store = new ContentStoreImpl(directoryPath);
		store.putContent(null);
	}

	@Test
	public void testGetEmpty() throws Exception {
		directoryPath = createTestDirectory();
		ContentStoreImpl store = new ContentStoreImpl(directoryPath);
		assertTrue(store.getAll().isEmpty());
	}

	@Test
	public void testGetOne() throws Exception {
		directoryPath = createTestDirectory();
		FileUtils.write(new File(directoryPath, "content-2097153.json"), CONTENT_JSON);
		ContentStoreImpl store = new ContentStoreImpl(directoryPath);
		assertEquals("2097153", store.getAll().iterator().next().getId());
	}

	@Test(expected = ContentStoreException.class)
	public void testGetOneInvalid() throws Exception {
		directoryPath = createTestDirectory();
		FileUtils.write(new File(directoryPath, "content-2097153.json"), INVALID_JSON);
		ContentStoreImpl store = new ContentStoreImpl(directoryPath);
		store.getAll();
	}

	@Test(expected = ContentStoreException.class)
	public void testGetOneReadProblem() throws Exception {
		directoryPath = createTestDirectory();
		File file = new File(directoryPath, "content-2097153.json");
		FileUtils.write(file, CONTENT_JSON);
		file.setReadable(false, false);
		ContentStoreImpl store = new ContentStoreImpl(directoryPath);
		store.getAll();
	}

	@Test
	public void testGetById() throws Exception {
		directoryPath = createTestDirectory();
		FileUtils.write(new File(directoryPath, "content-2097153.json"), CONTENT_JSON);
		ContentStoreImpl store = new ContentStoreImpl(directoryPath);
		assertTrue(store.get("2097153").isPresent());
		assertEquals("2097153", store.get("2097153").get().getId());
	}

	@Test
	public void testGetByIdUnknown() throws Exception {
		directoryPath = createTestDirectory();
		FileUtils.write(new File(directoryPath, "content-2097153.json"), CONTENT_JSON);
		ContentStoreImpl store = new ContentStoreImpl(directoryPath);
		assertFalse(store.get("1").isPresent());
	}

	private File createTestDirectory() throws IOException {
		return Files.createTempDirectory("").toFile();
	}

}
