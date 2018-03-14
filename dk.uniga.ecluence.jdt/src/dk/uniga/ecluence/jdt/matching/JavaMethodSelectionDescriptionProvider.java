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
package dk.uniga.ecluence.jdt.matching;

import java.util.Optional;

import org.eclipse.jdt.core.IJavaElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a {@link SelectionEnclosingMethodDescription} for the method that
 * encloses the current selection (e.g., object at the current caret position),
 * if currently inside a method.
 */
public class JavaMethodSelectionDescriptionProvider {

	private static final Logger log = LoggerFactory.getLogger(JavaMethodSelectionDescriptionProvider.class);

	public Optional<SelectionEnclosingMethodDescription> getSelectionDescription(Object o) {
		log.debug("getSelectionDescription({})", o == null ? null : o.getClass());
		if (o instanceof IJavaElement) {
			IJavaElement element = ((IJavaElement) o);
			log.debug("Java element at caret position: {} {}", element.getClass(), element.getElementName());
			IJavaElement method = getEnclosingMethod(element, IJavaElement.METHOD);
			if (method != null) {
				log.debug("  in enclosing method {} {}.{}", method, method.getParent().getElementName(),
						method.getElementName());
				return Optional.of(new SelectionEnclosingMethodDescription(method.getParent().getElementName(),
						method.getElementName(), "Java method"));
			}
		}
		return Optional.empty();
	}

	private IJavaElement getEnclosingMethod(IJavaElement element, int type) {
		if (element == null)
			return null;
		if (element.getElementType() == type)
			return element;
		return getEnclosingMethod(element.getParent(), type);
	}

}