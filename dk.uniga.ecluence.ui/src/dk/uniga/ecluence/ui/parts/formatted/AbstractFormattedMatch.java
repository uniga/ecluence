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
package dk.uniga.ecluence.ui.parts.formatted;

import java.util.List;

import org.eclipse.swt.custom.StyleRange;

import dk.uniga.ecluence.core.matching.ContentMatch;

/**
 * Base class of a formatted {@link ContentMatch} as a {@link StyledText}. Sub
 * classes should implement {@link #format(ContentMatch)} to turn the content
 * match into a styled text.
 */
abstract class AbstractFormattedMatch implements StyledText {
	
	protected String text;
	protected List<StyleRange> styleRanges;

	public AbstractFormattedMatch(ContentMatch match) {
		format(match);
	}
	
	/**
	 * Produces the {@link #text} and {@link #styleRanges} for the given match.
	 * @param match
	 */
	protected abstract void format(ContentMatch match);
	
	public String getText() {
		return text;
	}
	
	public List<StyleRange> getStyle() {
		return styleRanges;
	}
}