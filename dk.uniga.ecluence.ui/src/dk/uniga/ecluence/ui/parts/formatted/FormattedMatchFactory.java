package dk.uniga.ecluence.ui.parts.formatted;

import dk.uniga.ecluence.core.matching.ContentMatch;

public interface FormattedMatchFactory {

	AbstractFormattedMatch createFormattedMatch(ContentMatch match);
}