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
package dk.uniga.ecluence.ui.parts;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.uniga.ecluence.core.matching.ContentMatch;
import dk.uniga.ecluence.ui.parts.formatted.FormattedMatchFactoryImpl;
import dk.uniga.ecluence.ui.parts.formatted.StyledText;

/**
 * Viewer component that shows a list of {@link ContentMatch}es. To update the
 * content, clients should call {@link #setInput(List)}.
 */
public final class PageListViewer {

	private static final Logger log = LoggerFactory.getLogger(PageListViewer.class);
	
	private final TableViewer viewer;

	private final Consumer<ContentMatch> openCommand;

	private final Consumer<ContentMatch> openInExternalBrowserCommand;

	private final FormattedMatchFactoryImpl formattedMatchFactory;

	/**
	 * Creates a viewer instance in the given parent composite and with the given
	 * open commands.
	 * 
	 * @param parent
	 *            Parent composite to contain this viewer
	 * @param open
	 *            A command for opening a ContentMatch
	 * @param openInExternalBrowser
	 *            A command for opening a ContentMatch in an external browser
	 */
	public PageListViewer(Composite parent, Consumer<ContentMatch> open, Consumer<ContentMatch> openInExternalBrowser, FormattedMatchFactoryImpl formattedMatchFactory) {
		this.openCommand = Objects.requireNonNull(open, "open");
		this.openInExternalBrowserCommand = Objects.requireNonNull(openInExternalBrowser, "openInExternalBrowser");
		this.viewer = createViewer(parent);
		this.formattedMatchFactory = formattedMatchFactory;
	}

	public void setInput(List<ContentMatch> input) {
		Objects.requireNonNull(input);
		synchronized (viewer) {
			viewer.setInput(input);
		}
	}

	private TableViewer createViewer(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		TableViewer viewer = new TableViewer(composite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL);
		composite.setLayout(createTableColumnLayout(viewer));
		viewer.setContentProvider(new ObservableListContentProvider());
		viewer.addSelectionChangedListener(createSelectionChangedListener());
		createContextMenu(viewer);
		return viewer;
	}

	private TableColumnLayout createTableColumnLayout(TableViewer viewer) {
		TableColumnLayout layout = new TableColumnLayout();
		TableViewerColumn pageColumn = new TableViewerColumn(viewer, SWT.NONE);
		pageColumn.setLabelProvider(createLabelProvider());
		layout.setColumnData(pageColumn.getColumn(), new ColumnWeightData(100));
		return layout;
	}

	private ISelectionChangedListener createSelectionChangedListener() {
		return (event) -> {
			IStructuredSelection sel = event.getStructuredSelection();
			if (sel.size() > 0)
				openCommand.accept(((ContentMatch) sel.getFirstElement()));
		};
	}

	private StyledCellLabelProvider createLabelProvider() {
		return new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				ContentMatch match = (ContentMatch) cell.getElement();
				StyledText formatted = getFormattedMatchExplanation(match);
				cell.setText(formatted.getText());
				cell.setStyleRanges(formatted.getStyle().toArray(new StyleRange[0]));
				super.update(cell);
			}
		};
	}

	/**
	 * Creates the context menu
	 *
	 * @param viewer
	 */
	protected void createContextMenu(Viewer viewer) {
		MenuManager contextMenu = new MenuManager("#ViewerMenu"); //$NON-NLS-1$
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(mgr);
			}
		});
		Menu menu = contextMenu.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	/**
	 * Fill dynamic context menu
	 *
	 * @param contextMenu
	 */
	protected void fillContextMenu(IMenuManager contextMenu) {
		contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		contextMenu.add(new Action("Open in external browser") {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				openInExternalBrowserCommand.accept(((ContentMatch) selection.getFirstElement()));
			}
		});
	}

	public TableViewer getViewer() {
		return viewer;
	}

	private StyledText getFormattedMatchExplanation(ContentMatch match) {
		return formattedMatchFactory.createFormattedMatch(match);
	}
}
