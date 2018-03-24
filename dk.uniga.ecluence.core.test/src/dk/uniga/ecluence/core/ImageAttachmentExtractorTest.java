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

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import dk.uniga.ecluence.core.ImageAttachmentExtractor.ImageLink;

public class ImageAttachmentExtractorTest {

	@Test
	public void testExtractImageLinks() throws Exception {
		byte[] file = Files.readAllBytes(Paths.get("/Users/mikkelrj/Downloads/content-bean.html"));
		String content = new String(file, "UTF-8");
		List<ImageLink> links = new ImageAttachmentExtractor().extractImageLinks(content);
		assertEquals(links.size(), 1);
		ImageLink link = links.get(0);
		assertEquals(link.linkedResourceDefaultAlias, "hunt0.JPG");
		assertEquals(link.linkedResourceId, "4620320");
	}

	
}
