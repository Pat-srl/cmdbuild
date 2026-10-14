/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data.deserializer;

import java.io.IOException;
import org.cmdbuild.modeldiff.diff.data.GeneratedDiffData;

/**
 *
 * @author afelice
 */
public class CardDiffDataDeserializerImpl_OnFileSystem extends Deserializer_OnFileSystem<GeneratedDiffData> {
    
    public CardDiffDataDeserializerImpl_OnFileSystem() {
        super(GeneratedDiffData.class, "diff (card data)");
    }

    @Override
    protected GeneratedDiffData initData() {
        return new GeneratedDiffData();
    }

    @Override
    protected void cumulateData(GeneratedDiffData result, GeneratedDiffData partialData) throws IOException {
        // Raises IOException
        result.add(partialData);
    }
}
