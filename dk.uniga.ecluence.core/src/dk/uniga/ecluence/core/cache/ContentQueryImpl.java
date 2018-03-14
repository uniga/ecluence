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

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

/**
 * Implementation of a ContentQuery that is initialized with a list of labels.
 */
public class ContentQueryImpl implements ContentQuery {

	private static Logger log = LoggerFactory.getLogger(ContentQueryImpl.class);

	private List<String> labels;

	public ContentQueryImpl(List<String> labels) {
		this.labels = requireNonNull(labels);
		if (labels.isEmpty())
			throw new IllegalArgumentException("Must have at least one label");
		if (labels.stream().anyMatch(label -> !label.matches("[\\w-]+"))) {
			throw new IllegalArgumentException(
					"Labels must only contain valid characters (alphanumerics, '_', or '-')");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dk.uniga.ecluence.core.ContentQuery#getCQL()
	 */
	@Override
	public String getCQL() {
		String set = StringUtils.join(labels.stream().map(label -> String.format("\"%s\"", label)).toArray(), ",");
		log.debug("getCQL for labels {}", set);
		return String.format("type = page and label in (%s)", set);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dk.uniga.ecluence.core.ContentQuery#getLabels()
	 */
	@Override
	public Collection<String> getLabels() {
		return Collections.unmodifiableCollection(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dk.uniga.ecluence.core.ContentQuery#matches(de.itboehmer.confluence.rest.core
	 * .domain.content.ContentBean)
	 */
	@Override
	public boolean matches(ContentBean page) {
		requireNonNull(page);
		return page.getMetadata().getLabels().getResults().stream().anyMatch(label -> {
			return labels.contains(label.getName());
		});
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("labels", labels.stream().collect(Collectors.joining(", "))).build();
	}
}
