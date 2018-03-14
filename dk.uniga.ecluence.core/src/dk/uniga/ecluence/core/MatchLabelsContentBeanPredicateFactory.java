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
package dk.uniga.ecluence.core;

import java.util.function.Predicate;

/**
 * Factory for creating {@link MatchLabelsContentBeanPredicate}s based on a given string predicate.
 */
public class MatchLabelsContentBeanPredicateFactory {

	/**
	 * Returns a content label matching predicate using the given identifier predicate.
	 * 
	 * @param identifierPredicate
	 * @return
	 */
	public MatchLabelsContentBeanPredicate getPredicate(Predicate<String> identifierPredicate) {
		return new MatchLabelsContentBeanPredicate(identifierPredicate);
	}

}
