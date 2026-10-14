/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners.annotatedmethods;

import java.util.List;

public interface AnnotatedMethodJobSupplier {

    List<AnnotatedMethodJob> getAnnotatedMethodJobs();
    
    AnnotatedMethodJob getAnnotatedMethodJob(String key);
}
