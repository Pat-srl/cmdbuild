/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import com.google.common.eventbus.EventBus;

/**
 *
 * @author davide
 */
public interface DatabaseStatusService {

	/**
	 * return true if database is ready (started and fully configured)
	 *
	 * @return
	 */
	boolean isReady();

	/**
	 * return event bus to listen to db status events
	 *
	 * @return
	 */
	EventBus getEventBus();

	/**
	 * triggered when database is configured AND patch manager is up-to-date
	 */
	enum DatabaseBecomeReadyEvent {
		INSTANCE
	}

}
