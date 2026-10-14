/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.benchmark;

import jakarta.annotation.Nullable;

public interface BenchmarkResult {

    String getCategory();

    long getResult();

    double getScore();
    
    @Nullable
    Throwable getError();

    default boolean hasError() {
        return getError()!=null;
    }

}
