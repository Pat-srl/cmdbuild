/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data.deserializer;

import java.io.IOException;
import org.cmdbuild.modeldiff.dataset.data.GeneratedData;

/**
 *
 * @author afelice
 */
public class CardDataDeserializerImpl_OnFileSystem extends Deserializer_OnFileSystem<GeneratedData> {
    
    public CardDataDeserializerImpl_OnFileSystem() {
        super(GeneratedData.class, "model (card data)");
    }

    @Override
    protected GeneratedData initData() {
        return new GeneratedData();
    }

    @Override
    protected void cumulateData(GeneratedData result, GeneratedData partialData) throws IOException {
        // Raises IOException
        result.add(partialData);
    }
}
