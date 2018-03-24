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
package dk.uniga.ecluence.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ImageStore {

	private static final Logger log = LoggerFactory.getLogger(ImageStore.class);
	
	private final File stateLocation;

	public ImageStore(File location) throws IOException {
		log.debug("initialize in location: {}", location);
		this.stateLocation = location;
		initializeLocation();
	}

	private void initializeLocation() throws IOException {
		FileUtils.forceMkdir(stateLocation);
	}

	public File getFile(String filename) {
		return new File(stateLocation, filename);
	}

	public void storeFile(File file, InputStream attachment) throws IOException {
		Files.copy(attachment, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
}
