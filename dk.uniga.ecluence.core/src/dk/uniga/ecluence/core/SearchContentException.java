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
package dk.uniga.ecluence.core;

public class SearchContentException extends QueryException {

	private static final long serialVersionUID = -4044020225433745061L;

	private String query;

	public SearchContentException(String message, Throwable cause, String query) {
		super(message, cause);
		this.query = query;
	}

	public String getQuery() {
		return query;
	}
}
