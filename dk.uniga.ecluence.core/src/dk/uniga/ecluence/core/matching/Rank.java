package dk.uniga.ecluence.core.matching;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Rank defined by an integer level so that a rank is considered higher if it
 * has a lower level.
 * 
 * A {@value #BASE} rank is defined at level 1000.
 */
public final class Rank implements Comparable<Rank> {

	public static final Rank BASE = new Rank(1000);
	
	private int level;

	public Rank(int level) {
		this.level = level;
	}
	
	@Override
	public int compareTo(Rank o) {
		if (o == null)
			return 1;
		return this.level - o.level;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Rank) {
			return this.level == ((Rank) obj).level;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return level;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(level).build();
	}
}
