/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.utils;

import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;

public class XpdlUtils {

    public static DataSource xpdlToDatasource(String xpdl, String filename) {
        return xpdlToDatasource(xpdl.getBytes(StandardCharsets.UTF_8), filename);
    }

    public static DataSource xpdlToDatasource(byte[] xpdl, String filename) {
        ByteArrayDataSource dataSource = new ByteArrayDataSource(xpdl, "application/x-xpdl");
        dataSource.setName(format("%s.xpdl", filename));
        return dataSource;
    }
}
