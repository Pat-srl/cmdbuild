/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import java.util.Map;
import static java.util.function.Function.identity;
import java.util.stream.Collectors;
import org.cmdbuild.dao.entrytype.Classe;

/**
 *
 * @author afelice
 */
public class DataDatasetBuilder {
    static public Map<String, AttributeDataset> buildAllAttributes_ReadOnly(Classe classe) {
        return classe.getAllAttributesAsMap().keySet().stream()
                    .collect(Collectors.toMap(identity(),
                            AttributeDataset::buildReadOnly));        
    }
    
    static public Map<String, AttributeDataset> buildAllAttributes(Classe classe) {
        return classe.getAllAttributesAsMap().keySet().stream()
                    .collect(Collectors.toMap(identity(),
                            AttributeDataset::build));        
    }     
}
