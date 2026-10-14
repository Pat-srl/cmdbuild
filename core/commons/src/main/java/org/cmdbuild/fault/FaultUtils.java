/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.DOTALL;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnumUpper;
import static org.cmdbuild.utils.lang.CmExceptionUtils.exceptionToMessage;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;

public class FaultUtils {

    public static String buildFaultEventMessage(@Nullable String message, @Nullable Throwable exception) {
        checkArgument(isNotBlank(message) || exception != null, "must provide message or exception");
        if (message == null) {
            return exceptionToMessage(exception);
        } else {
            String exMessage = exceptionToMessage(exception);
            if (isBlank(exMessage)) {
                return message;
            } else {
                return format("%s: %s", message, exMessage);
            }
        }
    }

    public static String exceptionToUserMessage(Throwable ex) {
        return getUserMessage(FaultEventImpl.error(ex));
    }

    public static List<FaultMessage> errorToMessages(FaultEvent event) {
        List<FaultMessage> list = list();
        String message = event.getMessage();
        String errorCode;
        {
            Matcher matcher = Pattern.compile("(CME[\\[\\(\\s:]+([^;:\\s\\]\\)]+)[;:\\s\\]\\)]*)", DOTALL).matcher(message);
            if (matcher.find()) {
                errorCode = checkNotBlank(matcher.group(2));
                message = matcher.replaceFirst("");
            } else {
                errorCode = null;
            }
        }
        Matcher matcher = Pattern.compile("(CM_CUSTOM_EXCEPTION|CM|CMO)(\\s*(:|\\[|\\()?([a-zA-Z0-9.]+)(\\]|\\))?)?\\s*:\\s*([^\n\r]+?)\\s*(, caused by: .*|([\n\r].*|(:\\s*|;\\s*nested exception is )?[a-zA-Z0-9_.-]+[.][a-zA-Z0-9_]+(Exception|Error):.*)?)$", DOTALL).matcher(message);
        boolean addTechMessage = true;
        if (matcher.find()) {
            errorCode = firstNotBlankOrNull(errorCode, matcher.group(4));
            String userMessage = matcher.group(6);
            list.add(new FaultMessageImpl(event.getLevel(), userMessage, errorCode, true));
            if (matcher.group(1).equals("CMO")) {
                addTechMessage = false;
            }
        }
        if (addTechMessage) {
            list.add(new FaultMessageImpl(event.getLevel(), event.getMessage(), errorCode, false));
        }
        return list;
    }

    public static String errorsToMessage(List<FaultEvent> faults) {
        return list(faults).filter(FaultEvent::isError).map(FaultEvent::getMessage).collect(joining("; "));
    }

    public static String getUserMessage(FaultEvent event) {
        return errorToMessages(event).stream().filter(FaultMessage::showUser).findFirst().map(FaultMessage::getMessage).orElse("generic error");
    }

    public static FluentMap<String, Object> buildMessageForResponse(FaultLevel level, boolean showUser, String message) {
        return map("level", serializeEnumUpper(level), "show_user", showUser, "message", message);
    }

    public static List buildMessageListForResponse(FaultLevel level, boolean showUser, String message) {
        return list(buildMessageForResponse(level, showUser, message));
    }

    @Deprecated() // use fault service, for message translation support
    public static List<Map<String, Object>> errorToJsonMessages(FaultEvent event) {
        return FaultUtils.errorToMessages(event).stream().map(e -> buildMessageForResponse(e.getLevel(), e.showUser(), e.getMessage()).accept(m -> {
            if (e.hasCode()) {
                m.with("code", e.getCode());
            }
        })).collect(toImmutableList());
    }
}
