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

public class EmptyExplanation implements MatchExplanation {
	@Override
	public String getDefaultText() {
		return "";
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof EmptyExplanation;
	}
}
