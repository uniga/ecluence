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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.ImageAttachmentExtractor.ImageLink;

public class ImageLinkReplacer implements PageContentProcessor {

	private static final Logger log = LoggerFactory.getLogger(ImageLinkReplacer.class);

	private static final Pattern MODIFICATION_DATE_PATTERN = Pattern.compile("modificationDate=([0-9]+)[^0-9]+");

	private final Function<String, InputStream> attachmentReader;

	private final ImageStore imageStore;

	ImageAttachmentExtractor extractor = new ImageAttachmentExtractor();

	public ImageLinkReplacer(Function<String, InputStream> attachmentReader, ImageStore imageStore) {
		this.attachmentReader = attachmentReader;
		this.imageStore = imageStore;
	}

	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.core.PageContentProcessor#process(java.lang.String)
	 */
	@Override
	public String process(String content) throws PageContentProcessingException {
		List<ImageLink> links = extractor.extractImageLinks(content);
		String modified = content;
		for (ImageLink link : links) {
			try {
				log.debug("replace {} with {} {}", link.getSrc(), link.getLinkedResourceId(),
						link.getLinkedResourceDefaultAlias());

				File file = imageStore.getFile(link.getLinkedResourceDefaultAlias());

				updateFile(file, link);
				
				if (file.exists()) {
					modified = StringUtils.replace(modified, link.getSrc(), "file://" + file.getPath());
					modified = StringUtils.replace(modified, link.getSrcset(), "file://" + file.getPath());
				}
			} catch (IOException e) {
				throw new PageContentProcessingException(e);
			}
		}
		return modified;
	}

	private void updateFile(File file, ImageLink link) throws IOException {
		boolean fetchNeeded = !file.exists() || FileUtils.isFileOlder(file, getModificationDate(link));
		if (fetchNeeded)
			fetchFile(link, file);
	}

	private long getModificationDate(ImageLink link) {
		// Get modification date from src url
		Matcher matcher = MODIFICATION_DATE_PATTERN.matcher(link.getSrc());
		if (matcher.find()) {
			return Long.parseLong(matcher.group(1));
		}
		return 0;
	}

	private boolean fetchFile(ImageLink link, File file) throws IOException {
		InputStream attachment = attachmentReader.apply(link.getLinkedResourceId());
		if (attachment != null)
			imageStore.storeFile(file, attachment);
		return attachment != null;
	}

}
