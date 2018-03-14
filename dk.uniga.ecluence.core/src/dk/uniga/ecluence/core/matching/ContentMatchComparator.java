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
package dk.uniga.ecluence.core.matching;

import java.util.Comparator;

public class ContentMatchComparator implements Comparator<ContentMatch> {
	
	@Override
	public int compare(ContentMatch o1, ContentMatch o2) {
		int compareByRank = o1.getRank().compareTo(o2.getRank());
		if (compareByRank == 0)
			return o1.getExplanation().getDefaultText().compareTo(o2.getExplanation().getDefaultText());
		return compareByRank;
	}

}
