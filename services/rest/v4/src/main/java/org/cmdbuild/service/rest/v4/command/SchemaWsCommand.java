/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import jakarta.activation.DataHandler;
import org.cmdbuild.modeldiff.diff.schema.GeneratedDiffSchema;
import org.cmdbuild.modeldiff.loader.SchemaLoaderService;
import org.cmdbuild.modeldiff.schema.SchemaConfiguration;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class SchemaWsCommand {

    private final SchemaLoaderService schemaLoaderService;

    public SchemaWsCommand(SchemaLoaderService schemaLoaderService) {
        this.schemaLoaderService = checkNotNull(schemaLoaderService);
    }

    public SchemaConfiguration doLoad(String name) {
        return schemaLoaderService.getSchemaModel(name);
    }

    public GeneratedDiffSchema doDiff(DataHandler dataHandler) throws IOException {
        SchemaConfiguration schema = fromJson(dataHandler.getInputStream(), SchemaConfiguration.class);
        return schemaLoaderService.executeDiffFromSchema(schema);
    }

    public void doMerge(GeneratedDiffSchema diff) {
        schemaLoaderService.executeMergeFromDiff(diff);
    }
}
