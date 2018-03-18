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
package dk.uniga.ecluence.ui.parts;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelBean;
import de.itboehmer.confluence.rest.core.domain.content.VersionBean;
import dk.uniga.ecluence.ui.template.Template;
import dk.uniga.ecluence.ui.template.TemplateProvider;

public class ContentFormatter {

	public static final String[] PLACEHOLDERS = new String[] {
			"##TITLE##",
			"##CONTENT##",
			"##AUTHOR_LINK##",
			"##AUTHOR_NAME##",
			"##AUTHOR_ICON##",
			"##LAST_MODIFIED##",
			"##LABELS##"
	};

	private TemplateProvider templateProvider;
	
	public ContentFormatter(TemplateProvider templateProvider) {
		this.templateProvider = templateProvider;
	}

	public String formatContent(ContentBean content) {
		String page = StringUtils.replaceEach(getTemplate().getText(), PLACEHOLDERS, new String[] {
				content.getTitle(),
				content.getBody().getView().getValue(),
				getAuthorLink(content),
				getAuthorName(content),
				getTemplate().getAuthorIconUrl(content),
				getModified(content),
				getLabels(content)
		});
		return page;
	}

	private Template getTemplate() {
		return templateProvider.getTemplate();
	}

	private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
			.withLocale(Locale.getDefault())
			.withZone(ZoneId.of("UTC"));
	
	private static DateTimeFormatter VERSION_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][.S][XXX][X]")
			.withLocale(Locale.getDefault())
			.withZone(ZoneId.of("UTC"));
	
	private String getAuthorName(ContentBean content) {
		String author = null;
		if (content.getHistory() != null) {
			author = content.getHistory().getCreatedBy().getDisplayName();
		}
		return author;
	}
	
	private String getAuthorLink(ContentBean content) {
		String author = null;
		if (content.getHistory() != null) {
			author = "/wiki/display/~" + content.getHistory().getCreatedBy().getUsername();
		}
		return author;
	}
	
	private String getModified(ContentBean content) {
		String author = null;
		TemporalAccessor changedDate = null;
		if (content.getHistory() != null) {
			author = content.getHistory().getCreatedBy().getDisplayName();
			changedDate = content.getHistory().getCreatedDate().toInstant();
		}
		VersionBean version = content.getVersion();
		String changeAuthor = null;
		if (version != null && version.getBy() != null) {
			changeAuthor = version.getBy().getDisplayName();
			changedDate = VERSION_DATE_FORMATTER.parse(version.getWhen());
		}
		String lastModified = "Last modified " + DATE_FORMATTER.format(changedDate);
		if (changeAuthor != null && !StringUtils.equals(author, changeAuthor)) 
			lastModified += " by " + changeAuthor;
		
		return lastModified;
	}

	private String getLabels(ContentBean content) {
		List<LabelBean> labels = content.getMetadata().getLabels().getResults();
		StringBuffer sb = new StringBuffer();
		for (LabelBean label : labels) {
			String li = String.format("<li class=\"Label_label aui-label\">%s</li>", label.getName());
			String a = String.format("<a rel=\"tag\" href=\"wiki/label/%s\">%s</a>", label.getName(), li);
			sb.append(a);
		}
		return sb.toString();
	}

}
