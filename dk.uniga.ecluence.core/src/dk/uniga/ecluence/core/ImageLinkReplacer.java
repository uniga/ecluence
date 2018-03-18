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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.ImageAttachmentExtractor.ImageLink;

/**
 * Replaces links to attached images in a page with images in an ImageStore. If
 * images are not in the store, they are retrieved from a PageAttachmentProvider
 * which is responsible for replacing the link later.
 */
public class ImageLinkReplacer implements PageContentProcessor {

	private static final Logger log = LoggerFactory.getLogger(ImageLinkReplacer.class);

	private final AttachmentDownloader attachmentDownloader;

	private final ImageStore imageStore;

	ImageAttachmentExtractor extractor = new ImageAttachmentExtractor();

	public ImageLinkReplacer(AttachmentDownloader downloader, ImageStore imageStore) {
		this.attachmentDownloader = downloader;
		this.imageStore = imageStore;
	}

	/* (non-Javadoc)
	 * @see dk.uniga.ecluence.core.PageContentProcessor#process(java.lang.String)
	 */
	@Override
	public void process(PageContent content) throws PageContentProcessingException {
		List<ImageLink> links = extractor.extractImageLinks(content.getContent());
		String modified = content.getContent();
		PageAttachmentProvider attachmentprovider = PageAttachmentProvider.get(attachmentDownloader, content);
		for (ImageLink link : links) {
			modified = replaceLink(modified, link, attachmentprovider);
		}
		attachmentprovider.close();
		content.setContent(modified);
	}

	private String replaceLink(String modified, ImageLink link, PageAttachmentProvider attachmentProvider)
			throws PageContentProcessingException {
		try {
			log.debug("replace {} with {} {}", link.getSrc(), link.getLinkedResourceId(),
					link.getLinkedResourceDefaultAlias());
			File file = imageStore.getFile(link.getLinkedResourceDefaultAlias());
			String replacement = attachmentProvider.replaceLink(file, link);
			return replaceLink(modified, link, replacement);
		} catch (IOException e) {
			throw new PageContentProcessingException(e);
		}
	}
	
	private String replaceLink(String content, ImageLink link, String replacement) {
		return StringUtils.replaceEach(content, 
				new String[] {link.getSrc(), link.getSrcset()}, 
				new String[] {replacement, replacement});
	}
	
}
