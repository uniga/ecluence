package dk.uniga.ecluence.ui.parts.formatted;

import dk.uniga.ecluence.core.matching.ContentMatch;

public class FormattedMatchFactoryImpl implements FormattedMatchFactory {
	
	public AbstractFormattedMatch createFormattedMatch(ContentMatch match) {
		return new DefaultFormattedMatch(match);
	}
	
}
