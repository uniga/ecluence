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
package dk.uniga.ecluence.ui.parts.formatted;

import java.util.List;

import org.eclipse.swt.custom.StyleRange;

/**
 * A text with a associated {@link StyleRange}s for styled presentation.
 */
public interface StyledText {
	String getText();
	List<StyleRange> getStyle();
}