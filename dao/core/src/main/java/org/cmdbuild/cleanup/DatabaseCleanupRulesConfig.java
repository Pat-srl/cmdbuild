/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cleanup;

import java.util.List;

public interface DatabaseCleanupRulesConfig {

    List<RecordCleanupRule> getCleanupRules();

}
