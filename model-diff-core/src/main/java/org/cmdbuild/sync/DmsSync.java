/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import jakarta.activation.DataSource;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.modeldiff.dms.DocumentHandle;

/**
 *
 * @author afelice
 */
public interface DmsSync {

    boolean isEnabled();

//    Classe readDmsModel(String className);
    Classe readDmsModel(Classe relatedClasse);

    String readDefaultDmsCategory();

    String getDmsCategory(Classe relatedClasse);

    LookupType readDmsCategory(long lookupTypeId);

    List<LookupType> readDmsCategories(String filter);

    default List<LookupType> readDmsCategories() {
        return readDmsCategories("");
    }

    Pair<String, List<LookupValue>> readDmsCategoryValues(Classe classe, String fallbackDmsCategory);

    List<LookupValue> readDmsCategoryValues(String dmsCategoryType);

    Map<String, String> fetchDmsCategoryDescrTranslations(String lookupTypeName, List<LookupValue> valuesList);

    DocumentInfoAndDetail attachmentUpdate(Classe dmsModelClasse, Card existingMetadataCard, DocumentHandle documentHandle);

    DocumentInfoAndDetail attachmentUpload(Classe relatedClasse, Card relatedCard, DocumentHandle documentHandle);

    DataSource download(Classe relatedClasse, Card relatedCard, String filename);

    void delete(Classe classe, Card relatedCard, String filename);

}
