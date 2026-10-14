/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Optional;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.DatabaseRecord;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.service.rest.common.utils.WsSerializationUtils.userDescription;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;

@Component
public class HistorySerializationHelper {

    private final UserRepository userRepository;
    private final ObjectTranslationService translationService;

    public HistorySerializationHelper(UserRepository userRepository, ObjectTranslationService translationService) {
        this.userRepository = checkNotNull(userRepository);
        this.translationService = checkNotNull(translationService);
    }

    public FluentMap<Object, Object> serializeBasicHistory(DatabaseRecord record) {
        return map(
                "_type", record.getType().getName(),
                "_id", record.getId(),
                "_endDate", toIsoDateTime(record.getEndDate()),
                "_beginDate", toIsoDateTime(record.getBeginDate()),
                "_user", record.getUser(),
                "__user_description", userDescription(userRepository, record.getUser()),
                "_status", record.get(ATTR_STATUS))
                .accept(m -> {
                    if (record.getType().isDomain()) {
                        CMRelation rel = (CMRelation) record;
                        m.put("_status", rel.getStatus().toString());
                        if (rel.get("_isReference") != null && rel.get("_isReference").equals(true)) {
                            m.put("_historyType", "reference");
                        } else {
                            m.put("_historyType", "relation");
                        }
                        if (rel.isDirect()) {
                            m.put("_description", translationService.translateDomainDirectDescription(record.getType().asDomain().getName(), record.getType().asDomain().getDirectDescription()));
                        } else {
                            m.put("_description", translationService.translateDomainInverseDescription(record.getType().asDomain().getName(), record.getType().asDomain().getInverseDescription()));
                        }
                    } else if (record.getType().isClasse()) {
                        m.put("_historyType", record.getUser().startsWith("system") ? "system" : "card");
                    }
                });
    }
}
