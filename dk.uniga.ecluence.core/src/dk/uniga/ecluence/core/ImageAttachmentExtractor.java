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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageAttachmentExtractor {
	
	public List<ImageLink> extractImageLinks(String content) {
		
		List<ImageLink> links = new ArrayList<>();
		
		Pattern pattern = Pattern.compile("<img([\\w\\W]+?)>");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String group = matcher.group(0);
			HashMap<String, String> attributes = findAttributes(group);
			ImageLink link = createAttachmentLink(group, attributes);
			// Only store img tags linking attachments
			if (link.linkedResourceDefaultAlias != null)
				links.add(link);
		}
		return links;
	}
	
	private ImageLink createAttachmentLink(String imgTag, HashMap<String, String> attributes) {
		ImageLink link = new ImageLink();
		link.imgTag = imgTag;
		link.linkedResourceDefaultAlias = attributes.get("data-linked-resource-default-alias");
		link.linkedResourceId = attributes.get("data-linked-resource-id");
		link.src = attributes.get("src");
		link.srcset = attributes.get("srcset");
		return link;
	}
	
	Pattern pattern = Pattern.compile("([\\w-]+)\\s*=\\s*\\\"([^\"]*)\\\"");
	private HashMap<String, String> findAttributes(String element) {
		Matcher matcher = pattern.matcher(element);
		HashMap<String, String> attributes = new HashMap<>();
		while (matcher.find()) {
			attributes.put(matcher.group(1), matcher.group(2));
		}
		return attributes;
	}

	public class ImageLink {
		String src;
		String srcset;
		String imgTag; // to replace entire tag
		String linkedResourceDefaultAlias;
		String linkedResourceId;
		public String getImgTag() {
			return imgTag;
		}
		public String getSrc() {
			return src;
		}
		public String getLinkedResourceId() {
			return linkedResourceId;
		}
		public String getLinkedResourceDefaultAlias() {
			return linkedResourceDefaultAlias;
		}
		public String getSrcset() {
			return srcset;
		}
	}
}
