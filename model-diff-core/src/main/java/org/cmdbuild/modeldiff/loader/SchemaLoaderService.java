/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.loader;

import org.cmdbuild.modeldiff.diff.schema.GeneratedDiffSchema;
import org.cmdbuild.modeldiff.schema.SchemaConfiguration;

/**
 *
 * @author ataboga
 */
public interface SchemaLoaderService {

    boolean isSchemaAvailable();

    public SchemaConfiguration getSchemaModel(String schemaCode);

    public GeneratedDiffSchema executeDiffFromSchema(SchemaConfiguration schema);

    public void executeMergeFromDiff(GeneratedDiffSchema diff);
}
