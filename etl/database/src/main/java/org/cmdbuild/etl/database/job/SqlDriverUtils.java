/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.database.job;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.lang.invoke.MethodHandles;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlDriverUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static {
        try {
            try (ScanResult scanResult = new ClassGraph().scan()) {
                scanResult.getSubclasses(Driver.class).forEach(rethrowConsumer(c -> Class.forName(c.getName())));
            }
        } catch (Exception ex) {
            LOGGER.warn("error loading sql driver classes", ex);
        }
    }

    public static List<Class<? extends Driver>> getInstalledJdbcDrivers() {
        return DriverManager.drivers().map(Driver::getClass)
                .filter(c -> !c.getSimpleName().matches("DriverWrapper|DriverWrapperLW|ContainerDatabaseDriver|DriverWrapperAutoprobe|XmlaOlap4jDriver|PoolingDriver|P6SpyDriver"))
                .collect(toList());
    }

}
