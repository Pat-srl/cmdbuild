package org.cmdbuild.api.fluent;

import java.net.URL;
import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.type.ReferenceType;

public interface NewMail {

    NewMail withProvider(String notificationProvider);

    NewMail withAccount(String accountCode);

    NewMail withSignature(String signatureCode);

    NewMail withFrom(String from);

    NewMail withTo(String to);

    NewMail withTo(String... tos);

    NewMail withTo(Iterable<String> tos);

    NewMail withCc(String cc);

    NewMail withCc(String... ccs);

    NewMail withCc(Iterable<String> ccs);

    NewMail withBcc(String bcc);

    NewMail withBcc(String... bccs);

    NewMail withBcc(Iterable<String> bccs);

    NewMail withSubject(String subject);

    NewMail withContent(String content);

    NewMail withContentType(String contentType);

    NewMail withAttachment(URL url);

    NewMail withAttachment(URL url, String name);

    NewMail withAttachment(String url);

    NewMail withAttachment(String url, String name);

    NewMail withAttachment(DataHandler dataHandler);

    NewMail withAttachment(DataHandler dataHandler, String name);

    NewMail withAsynchronousSend(boolean asynchronous);

    NewMail withMeta(String key, String value);

    NewMail fromTemplate(String template, Map<String, ?> data);

    @Nullable
    Long send();

    long create(); //TODO: test this

    NewMail withCard(@Nullable String className, @Nullable Long cardId);

    default NewMail withCard(@Nullable Long cardId) {
        return this.withCard(null, cardId);
    }

    default NewMail withCard(@Nullable CardDescriptor card) {
        return this.withCard(card == null ? null : card.getClassName(), card == null ? null : card.getId());
    }

    default NewMail withCard(@Nullable ReferenceType card) {
        return this.withCard(card == null ? null : card.getClassName(), card == null ? null : card.getId());
    }

    default NewMail fromTemplate(String template) {
        return fromTemplate(template, emptyMap());
    }

    default NewMail fromTemplate(String template, Object... meta) {
        return fromTemplate(template, (Map) map(meta));
    }
}
