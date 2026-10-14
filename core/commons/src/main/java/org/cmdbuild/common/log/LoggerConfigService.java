/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.log;

import java.io.File;
import java.util.List;
import jakarta.activation.DataHandler;

public interface LoggerConfigService {

    final String LOGGER_LEVEL_DEFAULT = "DEFAULT";

    List<File> getActiveLogFiles();

    List<File> getAllLogFiles();

    List<LoggerConfig> getAllLoggerConfig();

    List<LoggerConfig> getAllLoggerConfigIncludeUnconfigured();

    void setLoggerConfig(LoggerConfig loggerConfig);

    void removeLoggerConfig(String category);

    String getConfigFileContent();

    DataHandler downloadLogFile(String fileName);

    DataHandler downloadActiveLogFiles();

    DataHandler downloadAllLogFiles();

}
