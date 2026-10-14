/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

public class JobRunStatusImpl {

    public static String serializeJobRunStatus(JobRunStatus jobRunStatus) {
        return jobRunStatus.name().replaceFirst("[^_]+_", "").toLowerCase();
    }
}
