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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

/**
 * Implementation of a {@link ContentStore} that stores ContentBeans on disk in
 * JSON format.
 */
public final class ContentStoreImpl implements ContentStore {

	private static final Logger log = LoggerFactory.getLogger(ContentStoreImpl.class);

	private final File stateLocation;

	private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	/**
	 * Constructs a store in which content is stored as files under the given
	 * location.
	 * 
	 * @param location
	 *            path of directory in which to store content as files
	 * @throws IOException
	 *             exception thrown if the store could not be initialized at the
	 *             given directory.
	 */
	public ContentStoreImpl(File location) throws IOException {
		this.stateLocation = location;
		initializeLocation();
	}

	private void initializeLocation() throws IOException {
		FileUtils.forceMkdir(stateLocation);
	}

	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.core.ContentStore#putContent(de.itboehmer.confluence.rest.core.domain.content.ContentBean)
	 */
	@Override
	public void putContent(ContentBean content) throws ContentStoreException {
		String id = content.getId();
		if (id == null)
			throw new IllegalArgumentException("The id property of ContentBean is null");
		File file = getPageFile(id);
		log.debug("putContent {} to file {}", id, file);
		String json = gson.toJson(content);
		try {
			FileUtils.write(file, json, "UTF-8");
		} catch (IOException e) {
			throw new ContentStoreException("Could not write content to file", e);
		}
	}

	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.core.ContentStore#getAll()
	 */
	@Override
	public Collection<ContentBean> getAll() throws ContentStoreException {
		List<ContentBean> beans = new ArrayList<>();
		Collection<File> files = Arrays.asList(stateLocation.listFiles(f -> f.getName().matches("content-.*\\.json")));
		for (File file : files) {
			beans.add(readPage(file));
		}
		return beans;
	}

	@Override
	public Optional<ContentBean> get(String id) throws ContentStoreException {
		File file = getPageFile(id);
		log.debug("get({}): {}", id, file);
		if (file.exists())
			return Optional.of(readPage(file));
		return Optional.empty();
	}

	private File getPageFile(String id) {
		return new File(stateLocation, String.format("content-%s.json", id));
	}

	private ContentBean readPage(File file) throws ContentStoreException {
		try {
			ContentBean content = gson.fromJson(FileUtils.readFileToString(file, "UTF-8"), ContentBean.class);
			log.debug("read page {} from file {}", content.getId(), file);
			return content;
		} catch (IOException e) {
			throw new ContentStoreException("Could not read content from file", e);
		} catch (JsonSyntaxException e) {
			throw new ContentStoreException("Could not parse JSON content from file", e);
		}
	}
}
