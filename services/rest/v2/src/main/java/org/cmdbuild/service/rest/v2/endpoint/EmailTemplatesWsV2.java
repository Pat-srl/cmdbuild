package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.template.TemplateBindings;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("email_templates/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class EmailTemplatesWsV2 {

    private final EmailTemplateService service;

    public EmailTemplatesWsV2(EmailTemplateService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset) {
        List<EmailTemplate> list = service.getAll();
        return map("data", list.stream().map(this::serializeDetailedTemplate).collect(toList()), "meta", map("total", list.size()));
    }

    @GET
    @Path("{id}/")
    public Object readOne(@PathParam("id") String id) {
        EmailTemplate element;
        if (NumberUtils.isCreatable(id)) {
            element = service.getById(toLong(id));
        } else {
            element = service.getByName(id);
        }
        return map("data", serializeTemplate(element), "meta", map());
    }

    private CmMapUtils.FluentMap<String, Object> serializeTemplate(EmailTemplate t) {
        return (serializeDetailedTemplate(t)).accept((m) -> {
            TemplateBindings bindings = service.fetchTemplateBindings(t);
            m.put("_bindings", map(
                    "client", bindings.getClientBindings(),
                    "server", bindings.getServerBindings()
            ));
        });
    }

    private CmMapUtils.FluentMap<String, Object> serializeDetailedTemplate(EmailTemplate t) {
        return serializeBasicTemplate(t).with(
                "from", t.getFrom(),
                "to", t.getTo(),
                "cc", t.getCc(),
                "bcc", t.getBcc(),
                "subject", t.getSubject(),
                "body", t.getContent(),
                "contentType", t.getContentType(),
                "account", t.getAccount(),
                "keepSynchronization", t.getKeepSynchronization(),
                "promptSynchronization", t.getPromptSynchronization(),
                "delay", t.getDelay(),
                "data", t.getMeta()
        );
    }

    private CmMapUtils.FluentMap<String, Object> serializeBasicTemplate(EmailTemplate t) {
        return map(
                "_id", t.getId(),
                "name", t.getCode(),
                "description", t.getDescription()
        );
    }

}
