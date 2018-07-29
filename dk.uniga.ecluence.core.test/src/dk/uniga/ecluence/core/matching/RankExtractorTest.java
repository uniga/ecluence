package dk.uniga.ecluence.core.matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelBean;
import de.itboehmer.confluence.rest.core.domain.content.LabelsBean;
import de.itboehmer.confluence.rest.core.domain.content.MetadataBean;

public class RankExtractorTest {

	@Test(expected = NullPointerException.class)
	public void testNullContentBeanFails() throws Exception {
		new RankExtractor().getRank(null);
	}

	@Test
	public void testGetRankFromLabelPlusBaseLevel() throws Exception {
		assertEquals(Rank.BASE.getLevel() + 1, getRank("rank-1").get().getLevel());
	}

	@Test
	public void testGetRankFromNonMatchingLabels() throws Exception {
		assertFalse(getRank("rank-1a").isPresent());
		assertFalse(getRank("1rank-1").isPresent());
	}
	
	@Test
	public void testGetRankWithoutRankLabel() throws Exception {
		Optional<Rank> rank = new RankExtractor().getRank(createPageWithLabel("label"));
		assertFalse(rank.isPresent());
	}

	@Test
	public void testGetRankWithoutLabels() throws Exception {
		Optional<Rank> rank = new RankExtractor().getRank(createPageWithMetadata());
		assertFalse(rank.isPresent());
	}

	@Test
	public void testGetRankWithoutMetadata() throws Exception {
		Optional<Rank> rank = new RankExtractor().getRank(new ContentBean("1"));
		assertFalse(rank.isPresent());
	}

	private Optional<Rank> getRank(String label) {
		return new RankExtractor().getRank(createPageWithLabel(label));
	}
	
	private ContentBean createPageWithLabel(String labelName) {
		ContentBean page1 = createPageWithMetadata();
		LabelsBean labels = new LabelsBean();
		page1.getMetadata().setLabels(labels);
		LabelBean label = new LabelBean("", labelName);
		labels.setResults(Arrays.asList(label));
		return page1;
	}
	
	private ContentBean createPageWithMetadata() {
		ContentBean page1 = new ContentBean("1");
		MetadataBean metadata = new MetadataBean();
		page1.setMetadata(metadata);
		return page1;
	}
	
}
