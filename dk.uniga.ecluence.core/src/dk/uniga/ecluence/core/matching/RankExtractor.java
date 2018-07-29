package dk.uniga.ecluence.core.matching;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelBean;

/**
 * Returns an optional {@link Rank} for a {@link ContentBean} with a base rank
 * value {@value Rank#BASE} plus XX, where the page has a label named "rank-XX".
 */
public class RankExtractor {

	private static final int BASE_RANK = Rank.BASE.getLevel();
	private static final Pattern rankPattern = Pattern.compile("rank-([0-9])+");
	
	public Optional<Rank> getRank(ContentBean page) {
		List<LabelBean> labels = getLabels(requireNonNull(page));
		Optional<LabelBean> rankLabel = StreamSupport.stream(labels.spliterator(), false)
				.filter(p -> rankPattern.matcher(p.getName()).matches())
				.findFirst();
		return rankLabel.map(label -> getRank(label));
	}

	private Rank getRank(LabelBean label) {
		return new Rank(BASE_RANK + extractRankFromLabel(label.getName()));
	}

	private List<LabelBean> getLabels(ContentBean page) {
		if (page.getMetadata() == null || page.getMetadata().getLabels() == null
				|| page.getMetadata().getLabels().getResults() == null)
			return Arrays.asList();
		return page.getMetadata().getLabels().getResults();
	}

	private int extractRankFromLabel(String label) {
		Matcher matcher = rankPattern.matcher(label);
		matcher.matches(); // we already know we have a match from above filtering
		return Integer.parseInt(matcher.group(1));
	}

}
