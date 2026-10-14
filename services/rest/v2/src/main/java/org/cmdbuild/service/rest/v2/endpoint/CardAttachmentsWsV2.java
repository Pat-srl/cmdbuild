package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.activation.DataHandler;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrEmpty;

@Path("classes/{classId}/cards/{cardId}/attachments/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardAttachmentsWsV2 {

    private final AttachmentWsHelper service;
    private final DmsService documentService;
    private final OperationUserSupplier userSupplier;

    public CardAttachmentsWsV2(AttachmentWsHelper attachmentWs, DmsService documentService, OperationUserSupplier userSupplier) {
        this.service = checkNotNull(attachmentWs);
        this.documentService = checkNotNull(documentService);
        this.userSupplier = checkNotNull(userSupplier);
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object create(
            @PathParam("classId") String classId,
            @PathParam("cardId") Long cardId,
            @Multipart(value = ATTACHMENT, required = false) WsAttachmentData attachment,
            @Multipart(FILE) DataHandler dataHandler) throws IOException {
        DocumentData docData = DocumentDataImpl.builder()
                .withAuthor(userSupplier.getUser().getLoginUser().getUsername())
                .withData(dataHandler.getInputStream())
                .accept((b) -> {
                    if (attachment != null) {
                        b.withCategory(attachment.getCategory()).withDescription(attachment.getDescription()).withFilename(firstNotBlankOrEmpty(attachment.getFileName(), dataHandler.getName()));
                    } else {
                        b.withDescription("").withFilename(dataHandler.getName());
                    }
                })
                .withMajorVersion(true)
                .build();
        documentService.checkRegularFileAttachment(docData, classId);
        documentService.checkRegularFileSize(docData, classId);
        DocumentInfoAndDetail document = documentService.create(classId, cardId, docData);
        return map("data", serializeAttachment(classId, document), "meta", map());
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("classId") String classId, @PathParam("cardId") Long cardId) {
        List<DocumentInfoAndDetail> list = documentService.getCardAttachments(classId, cardId);
        return map("data", list.stream().map(d -> serializeAttachment(classId, d)).collect(toList()), "meta", map("total", list.size()));
    }

    @GET
    @Path("{attachmentId}/")
    public Object readOne(@PathParam("classId") String classId, @PathParam("cardId") Long cardId, @PathParam("attachmentId") String attachmentId) {
        DocumentInfoAndDetail document = documentService.getCardAttachmentById(classId, cardId, attachmentId);
        return serializeAttachment(classId, document);
    }

    @GET
    @Path("{attachmentId}/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public Object download(@PathParam("classId") String classId, @PathParam("cardId") Long cardId, @PathParam("attachmentId") String attachmentId) {
        return service.download(classId, cardId, attachmentId);
    }

    @PUT
    @Path("{attachmentId}/")
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object update(@PathParam("classId") String classId, @PathParam("cardId") Long cardId, @PathParam("attachmentId") String attachmentId,
            @Multipart(value = ATTACHMENT, required = false) WsAttachmentData attachment, @Multipart(value = FILE, required = false) DataHandler dataHandler) {
        DocumentInfoAndDetail document = documentService.updateDocumentWithAttachmentId(classId, cardId, attachmentId, DocumentDataImpl.builder()
                .withAuthor(userSupplier.getUser().getLoginUser().getUsername())
                .accept((b) -> {
                    if (attachment != null) {
                        b.withCategory(attachment.getCategory()).withDescription(attachment.getDescription());
                    } else {
                        b.withDescription("");
                    }
                })
                .withData(dataHandler)
                .withFilename(attachment.getFileName())
                .withMajorVersion(true)
                .build());
        return serializeAttachment(classId, document);
    }

    @DELETE
    @Path("{attachmentId}/")
    public Object delete(@PathParam("classId") String classId, @PathParam("cardId") Long cardId, @PathParam("attachmentId") String attachmentId) {
        return service.delete(classId, cardId, attachmentId);
    }

    public static class WsAttachmentData {

        private final String category, description, fileName;
        private final boolean majorVersion;

        public WsAttachmentData(@JsonProperty("_category") String category,
                @JsonProperty("_description") String description,
                @JsonProperty("_attachment") String fileName,
                @JsonProperty("majorVersion") boolean majorVersion) {
            this.category = category;
            this.description = description;
            this.fileName = fileName;
            this.majorVersion = firstNotNull(majorVersion, true);
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }

        public String getFileName() {
            return fileName;
        }

        public boolean getMajorVersion() {
            return majorVersion;
        }
    }

    private Object serializeAttachment(String classId, DocumentInfoAndDetail input) {
        return map(
                "_name", input.getFileName(),
                "_author", input.getAuthor(),
                "_created", toIsoDateTime(input.getCreated()),
                "_category", input.getCategory(),
                "_id", input.getDocumentId(),
                "_description", input.getDescription(),
                "_version", input.getVersion(),
                "_modified", toIsoDateTime(input.getModified()));
    }

}
