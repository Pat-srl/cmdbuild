/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli;

import java.util.List;

public interface CliCommandRunner {

    String getName();

    List<String> getNames();

    String getDescription();

    void exec(String[] args) throws Exception;
}
