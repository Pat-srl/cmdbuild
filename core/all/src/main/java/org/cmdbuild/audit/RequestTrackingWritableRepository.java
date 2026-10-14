/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

public interface RequestTrackingWritableRepository extends RequestTrackingRepository {

	void cleanupRequestTableForMaxAge(int maxRecordAgeToKeepSeconds);

	void cleanupRequestTableForMaxRecords(int maxRecordsToKeep);

	void dropAll();

	void create(RequestData data);

	void update(RequestData data);

}
