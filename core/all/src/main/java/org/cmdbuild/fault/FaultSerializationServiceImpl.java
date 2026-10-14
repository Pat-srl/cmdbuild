/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnumUpper;
import static org.cmdbuild.utils.lang.CmExceptionUtils.isParamError;
import static org.cmdbuild.utils.lang.CmExceptionUtils.serializeError;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FaultSerializationServiceImpl implements FaultSerializationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectTranslationService translationService;

    public FaultSerializationServiceImpl(ObjectTranslationService translationService) {
        this.translationService = checkNotNull(translationService);
    }

    @Override
    public List<Map<String, Object>> errorToJsonMessages(FaultEvent event) {
        return FaultUtils.errorToMessages(event).stream().map(this::buildMessageForResponse).collect(toImmutableList());
    }

    private Map<String, Object> buildMessageForResponse(FaultMessage e) {
        String code = e.getCode();
        String message = e.getMessage();
        return mapOf(String.class, Object.class).with(
                "level", serializeEnumUpper(e.getLevel()),
                "show_user", e.showUser()
        ).accept(m -> {
            if (e.hasCode()) {
                m.with(
                        "message", formatMessage(message),
                        "_message_translation", formatMessage(message, code),
                        "code", code
                );
            } else if (e.showUser()) {
                m.with(
                        "message", message,
                        "_message_translation", message
                );
            } else {
                m.with(
                        "message", "generic error",
                        "_message_translation", "generic error"
                );
            }
        });
    }

    private String formatMessage(String message) {
        return formatMessage(message, null);
    }

    private String formatMessage(String message, @Nullable String code) {
        try {
            Pair<String, List<String>> error = Pair.of(message, emptyList());
            if (isParamError(message)) {
                error = serializeError(message);
                message = error.getKey();
            }
            if (isNotBlank(code)) {
                String defaultValue = message;
                message = safe(() -> translationService.translateByCode(code, defaultValue), defaultValue);
            }
            if (error.getValue().isEmpty()) {
                return message;
            }
            return escapeHtml4(format(message, error.getValue().toArray()));
        } catch (Exception ex) {
            logger.error("error on translating message", ex);
        }
        return message;
    }
}
