/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.python;

import org.springframework.stereotype.Component;

@Component
public class PythonScriptServiceImpl implements PythonScriptService {

    public PythonScriptServiceImpl() {
    }

    @Override
    public PythonScriptExecutor getScriptExecutor(String scriptContent) {
        return new PythonScriptExecutorImpl(scriptContent);
    }

}
