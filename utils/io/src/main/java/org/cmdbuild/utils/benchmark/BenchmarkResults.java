/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.utils.benchmark;

import java.util.List;

public interface BenchmarkResults {
    
    List<BenchmarkResult> getResults();
    
    double getAverageScore();

}
