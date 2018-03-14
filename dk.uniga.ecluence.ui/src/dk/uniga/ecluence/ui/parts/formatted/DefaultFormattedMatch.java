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

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import dk.uniga.ecluence.core.matching.ContentMatch;

/**
 * Basic formatting of a ContentMatch as "Page title [Explanation]" where the
 * explanation, which is the default text of the MatchExplanation obtained
 * from {@link ContentMatch#getExplanation()}, is shown in gray color.
 */
public final class DefaultFormattedMatch extends AbstractFormattedMatch {
	
	static final Color EXPLANATION_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	
	public DefaultFormattedMatch(ContentMatch match) {
		super(match);
	}

	protected void format(ContentMatch match) {
		String title = match.getContent().getTitle();
		String explanation = match.getExplanation().getDefaultText();
		text = String.format("%s [%s]", title, explanation);
		
		styleRanges = new ArrayList<StyleRange>();
		styleRanges.add(new StyleRange(0, title.length(), null, null));
		styleRanges.add(new StyleRange(title.length(), text.length() - title.length(), EXPLANATION_COLOR, null));
	}
}