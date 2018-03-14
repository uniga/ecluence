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
package dk.uniga.ecluence.ui.template;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;

public class TemplateStore {

	private File stateLocation;

	public TemplateStore(File location) throws IOException {
		this.stateLocation = location;
		initializeLocation();
	}

	private void initializeLocation() throws IOException {
		Files.createDirectories(stateLocation.toPath());
	}

	public File putFile(String filename, String fileContent) throws IOException {
		File localFile = new File(stateLocation, filename);
		Files.write(localFile.toPath(), fileContent.getBytes("UTF-8"));
		return localFile;
	}

	public String getFile(File file) throws IOException {
		return new String(Files.readAllBytes(file.toPath()), "UTF-8");
	}
	
	public Collection<File> getFiles(FileFilter filter) {
		return Arrays.asList(stateLocation.listFiles(filter));
	}
	
	/**
	 * Downloads the content at the given URL into a file in the store and returns the file.
	 * 
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public File downloadFile(URL url, String filename) throws IOException {
		File file = new File(stateLocation, filename);
		try (InputStream inputStream = url.openStream();
				ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
				FileOutputStream output = new FileOutputStream(file)) {
			output.getChannel().transferFrom(readableByteChannel, 0, 1 << 24);
		}
		return file;
	}

	public void deleteFile(File file) throws IOException {
		Files.delete(file.toPath());
	}
}
