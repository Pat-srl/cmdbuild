/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres;

import java.util.List;

public interface PostgresHelperConfig {

    String getHost();

    String getUsername();

    String getPassword();

    String getDatabase();

    int getPort();

    String getPostgresBinariesVersion();

    boolean getVerbose();

    boolean getCreateSchema();

    List<String> getSchemas();

    boolean handleExitStatus();

    boolean getXzCompression();

    boolean getLightMode();

    boolean isSingleCore();

}
