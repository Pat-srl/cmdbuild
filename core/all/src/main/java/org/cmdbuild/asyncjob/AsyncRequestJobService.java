/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.asyncjob;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AsyncRequestJobService {

    static final String ASYNC_JOB_REQUEST_PARAM = "async", ASYNC_JOB_REQUEST_HEADER = "CMDBuild-AsyncJob";

    AsyncRequestJob createAsyncRequest(String requestPath, HttpServletRequest request, HttpServletResponse response);

    AsyncRequestJob getJobForCurrentUserById(long jobId);

    void deleteJob(long jobId);

}
