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
package dk.uniga.ecluence.core.matching;

import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;

/**
 * Match of a Confluence ContentBean to a selection, providing a
 * MatchExplanation to explain the match, and a rank of the match relative to
 * other matches.
 */
public final class ContentMatch {
	
	private final ContentBean content;
	private final MatchExplanation explanation;
	private final Rank rank;
	
	/**
	 * Constructs a content match with a {@value Rank#BASE} rank for the given
	 * content and explanation.
	 * 
	 * @param content
	 *            the matched page
	 * @param explanation
	 *            the explanation of this match
	 */
	public ContentMatch(ContentBean content, MatchExplanation explanation) {
		this(content, explanation, Rank.BASE);
	}
	
	/**
	 * Constructs a content match with the given rank for the given content and
	 * explanation.
	 * 
	 * @param content
	 *            the matched page
	 * @param explanation
	 *            the explanation of this match
	 * @param rank
	 *            the rank of this match
	 */
	public ContentMatch(ContentBean content, MatchExplanation explanation, Rank rank) {
		this.content = Objects.requireNonNull(content, "content");
		this.explanation = Objects.requireNonNull(explanation, "explanation");
		this.rank = Objects.requireNonNull(rank, "rank");
	}
	
	/**
	 * Returns the matched page.
	 * 
	 * @return the matched page
	 */
	public ContentBean getContent() {
		return content;
	}
	
	/**
	 * Returns the explanation of this match.
	 * 
	 * @return the match explanation
	 */
	public MatchExplanation getExplanation() {
		return explanation;
	}
	
	/**
	 * Returns the rank of this match.
	 * 
	 * @return the rank of this match
	 */
	public Rank getRank() {
		return rank;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ContentMatch) {
			ContentMatch other = (ContentMatch) obj;
			return content.getId().equals(other.content.getId()) &&
					explanation.equals(other.explanation) &&
					rank.equals(other.rank);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("content", content.getTitle())
				.append("explanation", explanation)
				.append("rank", rank)
				.toString();
	}
}
