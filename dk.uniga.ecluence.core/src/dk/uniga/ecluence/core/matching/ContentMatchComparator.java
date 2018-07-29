/*******************************************************************************
 * Copyright (c) 2017, 2018 Uniga.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mikkel R. Jakobsen - initial API and implementation
 *******************************************************************************/
package dk.uniga.ecluence.core.matching;

import java.util.Comparator;

/**
 * Compares two {@link ContentMatch}es by their rank and compares their titles
 * and id if equal rank.
 */
public class ContentMatchComparator implements Comparator<ContentMatch> {
	
	@Override
	public int compare(ContentMatch o1, ContentMatch o2) {
		int compareByRank = o1.getRank().compareTo(o2.getRank());
		if (compareByRank == 0)
			// For equally ranked pages, compare on title and id
			return getTitle(o1).compareTo(getTitle(o2));
		return compareByRank;
	}

	private String getTitle(ContentMatch o1) {
		return String.format("%s %s", o1.getContent().getTitle(), o1.getContent().getId());
	}

}
