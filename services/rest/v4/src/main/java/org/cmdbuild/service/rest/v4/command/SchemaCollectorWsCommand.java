/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.api.SchemaCollectorApi;
import org.cmdbuild.api.SystemApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author ldare
 */
@Component
public class SchemaCollectorWsCommand {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SchemaCollectorApi schemaCollectorApi;

    public SchemaCollectorWsCommand(SystemApi systemApi) {
        this.schemaCollectorApi = systemApi.schemaCollector(); // null if schemaCollector module not available
    }

    public String doTest(String msg) {
        logger.info("test({})", msg);

        String returnedMsg = "success:" + schemaCollectorApi.test(msg);

        logger.info("test({}) - returnedMsg =< {} >", msg, returnedMsg);
        return returnedMsg;
    }

    public String doCollectSchema(String curSystemMnemonicName, String curSystemId) {
        //        logger.info("collectSchema(\"{}\",\"{}\")", curSystemMnemonicName, curSystemId);

        // Throws UnsupportedOperationException if SchemaCollector is unavailable
        String returnedTmpFile = schemaCollectorApi.collectSchema(curSystemMnemonicName, curSystemId);

//        logger.info("collectSchema(\"{}\",\"{}\") - returnedTmpFile =< {} >", curSystemMnemonicName, curSystemId, returnedTmpFile);
        return returnedTmpFile;
    }

    public String doCompareSchema(String otherSchemaSerialization, String curSystemMnemonicName) {
        // Throws UnsupportedOperationException if SchemaCollector is unavailable
        return schemaCollectorApi.compareSchema(otherSchemaSerialization, curSystemMnemonicName);
    }

    public String doCompareSchemaBetween(String newSchemaSerialization, String aSchemaSerialization) {
        // Throws UnsupportedOperationException if SchemaCollector is unavailable
        return schemaCollectorApi.compareSchemaBetween(newSchemaSerialization, aSchemaSerialization);
    }

    public String doApplySchemaDiff(String diffSchemaSerialization) {
        // Throws UnsupportedOperationException if SchemaCollector is unavailable
        return schemaCollectorApi.applySchemaDiff(diffSchemaSerialization);
    }
}
