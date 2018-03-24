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

/**
 * Explanation of a match of content to a selection.
 */
public interface MatchExplanation {
	
	/**
	 * Returns a default text representation of the explanation in a format readable
	 * to a user.
	 * 
	 * @return
	 */
	String getDefaultText();
}