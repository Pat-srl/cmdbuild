/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.activation.DataSource;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;

public interface EtlTemplateInlineProcessorService extends EtlTemplateProcessorService {

    DataSource exportDataInline(List<? extends DatabaseRecord> data, EtlTemplate template);

    <T extends DatabaseRecord> List<T> importDataInline(Object data, EntryType type, EtlTemplate template);

    default DataSource exportDataInline(List<Map<String, Object>> data, Classe model, EtlTemplate template) {
        List<Card> cards = data.stream().map(r -> CardImpl.buildCard(model, r)).collect(toList());
        return exportDataInline(cards, template);
    }

}
