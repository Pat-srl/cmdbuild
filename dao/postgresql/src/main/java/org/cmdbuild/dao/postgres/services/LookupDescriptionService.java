/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

public interface LookupDescriptionService {

	String getDescription(long lookupId);

	void invalidateCache();

}
