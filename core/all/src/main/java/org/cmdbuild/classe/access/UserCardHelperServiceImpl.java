/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import jakarta.annotation.Nullable;
import java.util.Map;
import org.cmdbuild.classe.access.UserCardHelperService;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NOTES;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_HTML_SAFE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.STRING;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.TEXT;
import org.cmdbuild.utils.html.HtmlSanitizerUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.cmdbuild.utils.lang.CmPredicatesUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.springframework.stereotype.Component;

@Component
public class UserCardHelperServiceImpl implements UserCardHelperService {

    private final CoreConfiguration coreConfiguration;

    public UserCardHelperServiceImpl(CoreConfiguration coreConfiguration) {
        this.coreConfiguration = checkNotNull(coreConfiguration);
    }

    @Override
    public Card sanitizeValues(Card card) {
        return CardImpl.copyOf(card).withAttributes(sanitizeValues(card.getType(), card.getAllValuesAsMap())).build();
    }

    @Override
    public Map<String, Object> sanitizeValues(EntryType type, Map<String, Object> values) {
        return map(values)
                .accept(m -> {
                    // sanitize html values that has enabled html safe
                    list(type.getAllAttributes()).filter(a -> a.isOfType(STRING, TEXT) && a.getMetadata().hasTextContentSecurity(TCS_HTML_SAFE))
                            .map(Attribute::getName)
                            .accept(l -> {
                                if (coreConfiguration.hasDefaultTextContentSecurity(TCS_HTML_SAFE)) {
                                    l.add(ATTR_NOTES);
                                }
                            })
                            .filter(values::containsKey)
                            .forEach(a -> m.put(a, sanitizeHtml(m.get(a))));

                    // trim values where trim option is enabled
                    list(type.getAllAttributes()).filter(CmPredicatesUtils.and(a -> a.isOfType(STRING, TEXT), Attribute::isTrimEnabled))
                            .map(Attribute::getName)
                            .filter(values::containsKey)
                            .forEach(a -> m.put(a, trimValue(m.get(a))));
                });
    }

    @Nullable
    private Object sanitizeHtml(@Nullable Object value) {
        if (isNotBlank(toStringOrNull(value))) {
            String string = toStringNotBlank(value);
            string = HtmlSanitizerUtils.sanitizeHtml(string);
            return string;
        } else {
            return value;
        }
    }

    @Nullable
    private Object trimValue(@Nullable Object value) {
        if (isNotBlank(toStringOrNull(value))) {
            String string = toStringNotBlank(value);
            string = string.trim();
            return string;
        } else {
            return value;
        }
    }
}
