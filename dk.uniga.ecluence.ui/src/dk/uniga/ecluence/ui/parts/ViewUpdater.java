package dk.uniga.ecluence.ui.parts;

import java.util.Collection;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.matching.ContentMatch;
import dk.uniga.ecluence.core.matching.SelectionSource;

/**
 * Keeps track of the state of user's selection, content matches, and uses a
 * delegates to a {@link Strategy} to update views according to state changes.
 * 
 * This allows different strategies for updating selections, for example keeping
 * the currently shown page if it has been selected by the user.
 * 
 * It also allows different strategies for different configurations of viewers,
 * for example, for supporting a browser with tabs.
 */
public class ViewUpdater {
	
	private final EclipseSelectionContentMatcher selectionContentMatcher;

	private SelectionState state = new SelectionState(null, null, null, null);

	private Strategy strategy;

	public ViewUpdater(EclipseSelectionContentMatcher selectionContentMatcher, Strategy strategy) {
		this.selectionContentMatcher = selectionContentMatcher;
		this.strategy = strategy;
	}

	public void userSelectedPage(ContentBean page) {
		state.userSelectedPage = page;
	}
	
	public void refreshSelection() {
		if (state.selection != null)
			selectionContentMatcher.selectionChanged(state.source, state.selection);
	}
	
	public void updateMatches(SelectionSource source, Object o, Collection<ContentMatch> matches) {
		SelectionState newState = new SelectionState(o, source, matches, null);
		strategy.update(state, newState);
		this.state = newState;
	}
	
	interface Strategy {
		void update(SelectionState oldState, SelectionState newState);
	}
	
	public class SelectionState {
		
		/** Last object matched by {@link #selectionContentMatcher} */
		private final Object selection;
		
		private final SelectionSource source;

		private final Collection<ContentMatch> matches;
		
		private ContentBean userSelectedPage;
		
		public SelectionState(Object selection, SelectionSource source, Collection<ContentMatch> matches, ContentBean shownPage) {
			this.selection = selection;
			this.source = source;
			this.matches = matches;
			this.userSelectedPage = shownPage;
		}

		public Collection<ContentMatch> getMatches() {
			return matches;
		}

		public ContentBean getUserSelectedPage() {
			return userSelectedPage;
		}

		public void setUserSelectedPage(ContentBean selectedPage) {
			userSelectedPage = selectedPage;
		}
	}
}
