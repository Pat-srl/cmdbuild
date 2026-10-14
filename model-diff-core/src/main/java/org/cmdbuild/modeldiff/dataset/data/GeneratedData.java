/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import java.io.IOException;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.utils.lang.CmNullableUtils;

/**
 * Represents generated JSON, contains the list of serialization for
 * {@link Classe}.
 *
 * @author afelice
 */
public class GeneratedData {

    public String name;

    public GeneratedData_Container data = new GeneratedData_Container();

    /**
     * Used if adding incrementally {@link Classe} deserialized data.
     *
     * <p>
     * Used in {@link CardDataDeserializerImpl_OnFileSystem}.
     *
     * @param curClasseData
     * @throws IOException if found <i>dataset</i> name is mismatching.
     */
    public void add(GeneratedData curClasseData) throws IOException {
        checkDataset(curClasseData);

        data.add(curClasseData.data);
    }

    protected void checkDataset(GeneratedData curClasseData) throws IOException {
        if (CmNullableUtils.isBlank(name)) {
            this.name = curClasseData.name;
        } else {
            if (!this.name.equals(curClasseData.name)) {
                throw new IOException("mismatching related dataset, found =< %s > but expected =< %s >".formatted(curClasseData.name, this.name));
            }
        }
    }
}
