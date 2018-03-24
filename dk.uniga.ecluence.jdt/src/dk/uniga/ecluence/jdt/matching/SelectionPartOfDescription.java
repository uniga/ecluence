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

public class SelectionPartOfDescription implements SelectionDescription {

	public String objectType;
	public String object;
	public String parentType;
	public String parent;

	public SelectionPartOfDescription(String objectType, String object, String parentType, String parent) {
		this.objectType = Objects.requireNonNull(objectType, "objectType");
		this.object = Objects.requireNonNull(object, "object");
		this.parentType = Objects.requireNonNull(parentType, "parentType");
		this.parent = Objects.requireNonNull(parent, "parent");
	}

	@Override
	public String getDefaultText() {
		return String.format("[%s] %s", parentType, parent);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SelectionPartOfDescription) {
			SelectionPartOfDescription other = (SelectionPartOfDescription) obj;
			return object.equals(other.object) && objectType.equals(other.objectType) && 
					parent.equals(other.parent) && parentType.equals(other.parentType);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("object", object)
				.append("parent", parent)
				.build();
	}
}