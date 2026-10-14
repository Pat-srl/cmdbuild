/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.core;

import com.google.common.eventbus.EventBus;
import org.apache.http.client.HttpClient;
import org.cmdbuild.client.rest.RestClient;

public interface RestWsClient extends RestClient {

	HttpClient getHttpClient();

	String getServerUrl();

	String getActionId();
	
	EventBus getEventBus();
}
