/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.spring;

/**
 *
 */
public interface BeanNamesAndQualifiers {

    public static final String RAW_DATA_SOURCE = "rawDataSource";

    /**
     * require only db access
     *
     * lower level, direct db access, no tenant security, cache support, or any
     * access control (note: tenant-enabled tables may not work correctly)
     */
    public static final String SYSTEM_LEVEL_ONE = "system_level_one";

    /**
     * require db access, cache (and clustering config), and tenant
     * configuration
     *
     * tenant system enabled (but with admin access). This is the default system
     * access level
     */
    public static final String SYSTEM_LEVEL_TWO = "system_level_two";

    public static final String INNER = "inner";
}
