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
package dk.uniga.ecluence.jdt.matching;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import dk.uniga.ecluence.core.matching.SelectionDescription;

public class SelectionEnclosingMethodDescription implements SelectionDescription {

	private String typeName;
	private String method;
	private String type;

	public SelectionEnclosingMethodDescription(String typeName, String method, String type) {
		this.typeName = Objects.requireNonNull(typeName);
		this.method = Objects.requireNonNull(method);
		this.type = Objects.requireNonNull(type);
	}

	public String getTypeName() {
		return typeName;
	}
	
	public String getMethod() {
		return method;
	}
	
	@Override
	public String getDefaultText() {
		return String.format("%s %s.%s", type, typeName, method);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SelectionEnclosingMethodDescription) {
			SelectionEnclosingMethodDescription other = (SelectionEnclosingMethodDescription) obj;
			return other.type.equals(type) &&
					other.typeName.equals(typeName) &&
					other.method.equals(method);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("typeName", typeName)
				.append("method", method).append("type", type).build();
	}
}
