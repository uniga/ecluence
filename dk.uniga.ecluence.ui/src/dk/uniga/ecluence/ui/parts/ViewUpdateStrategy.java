package dk.uniga.ecluence.ui.parts;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.matching.ContentMatch;
import dk.uniga.ecluence.ui.parts.ViewUpdater.SelectionState;

public final class ViewUpdateStrategy implements ViewUpdater.Strategy {

	private final Consumer<Collection<ContentMatch>> viewList;

	private final Consumer<ContentMatch> viewSelection;

	public ViewUpdateStrategy(Consumer<Collection<ContentMatch>> viewList, Consumer<ContentMatch> viewSelection) {
		this.viewList = viewList;
		this.viewSelection = viewSelection;
	}

	public void update(SelectionState oldState, SelectionState newState) {
		viewList.accept(newState.getMatches());
		if (!newState.getMatches().isEmpty()) {
			select(oldState, newState);
		}
	}
	
	private void select(SelectionState oldState, SelectionState newState) {
		if (!isPageAmongMatches(oldState.getUserSelectedPage(), newState.getMatches())) {
			viewSelection.accept(newState.getMatches().iterator().next());
		}
		else {
			selectMatchedPage(newState.getMatches(), oldState.getUserSelectedPage());
			newState.setUserSelectedPage(oldState.getUserSelectedPage());
		}
	}

	private void selectMatchedPage(Collection<ContentMatch> matches, ContentBean page) {
		for (ContentMatch match : matches) {
			if (match.getContent().equals(page)) {
				viewSelection.accept(match);
				return;
			}
		}
	}

	private boolean isPageAmongMatches(ContentBean page, Collection<ContentMatch> matches) {
		return StreamSupport.stream(matches.spliterator(), false)
			.map(match -> match.getContent())
			.anyMatch(p -> p.equals(page));
	}

}
