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
 
package dk.uniga.ecluence.ui.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import dk.uniga.ecluence.core.Activator;
import dk.uniga.ecluence.core.QueryException;
import dk.uniga.ecluence.core.cache.ContentStoreException;

/**
 * Action for refreshing.
 */
public class RefreshAction {
	
	@Inject
	private IEventBroker broker;
	
	@Execute
	public void execute() {
		new Job("Searching for matching Confluence content") {
			@Override
			protected IStatus run(IProgressMonitor arg0) {
				try {
					int updates = Activator.getDefault().getConfluenceFacade().refresh();
					broker.post(EventConstants.REFRESH_DONE, updates);
				} catch (QueryException e) {
					dk.uniga.ecluence.ui.Activator.handleError("Error connecting to Confluence API", e, true);
				} catch (ContentStoreException e) {
					dk.uniga.ecluence.ui.Activator.handleError("Error reading content from local store", e, true);
				}
				return Status.OK_STATUS;
			}
		}.schedule();
	}
	
}
