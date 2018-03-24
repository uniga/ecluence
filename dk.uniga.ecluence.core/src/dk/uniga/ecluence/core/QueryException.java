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

public class QueryException extends Exception {

	private static final long serialVersionUID = 3595111406613878594L;
	
	public QueryException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public QueryException(String message) {
		super(message);
	}

}
