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

import java.util.Objects;

public class IndexPageMatchExplanation implements MatchExplanation {
	
	private final String label;

	public IndexPageMatchExplanation(String label) {
		this.label = Objects.requireNonNull(label);
	}
	
	@Override
	public String getDefaultText() {
		return String.format("Index page identified by label '%s'", label);
	}
	
	@Override
	public String toString() {
		return String.format("%s[%s]", IndexPageMatchExplanation.class.getSimpleName(), label);
	}
}
