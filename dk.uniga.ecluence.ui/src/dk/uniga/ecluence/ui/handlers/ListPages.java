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
 
package dk.uniga.ecluence.ui.handlers;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import javax.inject.Inject;

import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

import de.itboehmer.confluence.rest.core.domain.content.ContentBean;
import dk.uniga.ecluence.core.ConfluenceFacade;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.ui.Activator;

public class ListPages {
	
	@Inject
	private UISynchronize synchronize;

	@Execute
	public void execute() {
		try {
			showDialog(getPageList());
		} catch (QueryException e) {
			e.printStackTrace();
		}
	}

	private void showDialog(ObservableList<ContentBean> pages) throws QueryException {
		ListDialog dialog = new ListDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		dialog.setTitle("Cached pages");
		dialog.setMessage("Confluence pages currently in cache.");
		dialog.setAddCancelButton(true);
		dialog.setContentProvider(new ObservableListContentProvider());
		dialog.setLabelProvider(createLabelProvider());
		dialog.setInput(pages);
		synchronize.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (dialog.open() == Window.OK) {
					IEventBroker eventBroker = Activator.getDefault().getEventBroker();
					eventBroker.post(EventConstants.USER_SELECTED_PAGE, dialog.getResult()[0]);
				}
			}
		});
	}

	private ILabelProvider createLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				ContentBean page = (ContentBean) element;
				return toLabel(page);
			}

		};
	}

	private String toLabel(ContentBean page) {
		return String.format("%s (%s)", page.getTitle(), page.getId());
	}
	
	private ObservableList<ContentBean> getPageList() throws QueryException {
		ConfluenceFacade facade = dk.uniga.ecluence.core.Activator.getDefault().getConfluenceFacade();
		WritableList<ContentBean> pageList = new WritableList<>();
		pageList.addAll(sorted(facade.getPages()));
		return pageList;
	}

	private Collection<? extends ContentBean> sorted(Collection<ContentBean> pages) {
		TreeSet<ContentBean> sorted = new TreeSet<ContentBean>(new Comparator<ContentBean>() {
			@Override
			public int compare(ContentBean o1, ContentBean o2) {
				return toLabel(o1).compareTo(toLabel(o2));
			}
		});
		sorted.addAll(pages);
		return sorted;
	}
		
}
