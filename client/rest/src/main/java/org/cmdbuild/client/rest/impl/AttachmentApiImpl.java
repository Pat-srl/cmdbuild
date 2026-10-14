/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Streams.stream;
import com.google.gson.JsonElement;
import java.io.InputStream;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.cmdbuild.client.rest.api.AttachmentApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.model.Attachment;
import org.cmdbuild.client.rest.model.SimpleAttachment;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import static org.cmdbuild.utils.url.CmUrlUtils.urlToByteArray;

public class AttachmentApiImpl extends AbstractServiceClientImpl implements AttachmentApi {

    public AttachmentApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public List<Attachment> getCardAttachments(String classId, String cardId) {
        logger.debug("get card attachments for classId = {} cardId = {}", classId, cardId);
        JsonElement response = get("classes/" + trimAndCheckNotBlank(classId) + "/cards/" + trimAndCheckNotBlank(cardId) + "/attachments").asJson();
        return stream(response.getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject).map((attachment) -> {
            return new SimpleAttachment(toString(attachment.get("name")), toString(attachment.get("version")), toString(attachment.get("_id")));
        }).collect(toList());
    }

    @Override
    public List<Attachment> getEmailAttachments(String classId, String cardId, String emailId) {
        logger.debug("get email attachments for classId = {} cardId = {} emailId = {}", classId, cardId, emailId);
        JsonElement response = get("classes/" + trimAndCheckNotBlank(classId) + "/cards/" + trimAndCheckNotBlank(cardId) + "/emails/" + trimAndCheckNotBlank(emailId) + "/attachments").asJson();
        return stream(response.getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject).map((attachment) -> {
            return new SimpleAttachment(toString(attachment.get("name")), toString(attachment.get("version")), toString(attachment.get("_id")));
        }).collect(toList());
    }

    @Override
    public List<Attachment> getAttachmentHistory(String classId, String cardId, String attachmentId) {
        logger.debug("get card attachments history for classId = {} cardId = {} attachmentId = {}", classId, cardId, attachmentId);
        JsonElement response = get("classes/" + trimAndCheckNotBlank(classId) + "/cards/" + trimAndCheckNotBlank(cardId) + "/attachments/" + trimAndCheckNotBlank(attachmentId) + "/history").asJson();
        return stream(response.getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject).map((attachment) -> {
            return new SimpleAttachment(toString(attachment.get("name")), toString(attachment.get("version")), toString(attachment.get("_id")));
        }).collect(toList());
    }

    @Override
    public AttachmentServiceWithAttachment createCardAttachment(String classId, String cardId, String fileName, InputStream data) {
        HttpEntity multipart = MultipartEntityBuilder.create()
                .addBinaryBody("file", listenUpload("attachment upload", data), ContentType.APPLICATION_OCTET_STREAM, fileName)
                .build();
        JsonElement response = post("classes/" + trimAndCheckNotBlank(classId) + "/cards/" + trimAndCheckNotBlank(cardId) + "/attachments", multipart).asJson();
        Attachment attachment = new SimpleAttachment(fileName, "TODO", toString(response.getAsJsonObject().get("data")));//TODO version
        return new AttachmentServiceWithAttachmentImpl(attachment);
    }

    @Override
    public AttachmentServiceWithAttachment updateCardAttachment(String classId, String cardId, String attachmentId, String fileName, InputStream data) {
        HttpEntity multipart = MultipartEntityBuilder.create()
                .addBinaryBody("file", listenUpload("attachment upload", data), ContentType.APPLICATION_OCTET_STREAM, fileName)
                .build();
        JsonElement response = put("classes/" + trimAndCheckNotBlank(classId) + "/cards/" + trimAndCheckNotBlank(cardId) + "/attachments/" + trimAndCheckNotBlank(attachmentId), multipart).asJson();
        Attachment attachment = new SimpleAttachment(fileName, "TODO", attachmentId);//TODO version
        return new AttachmentServiceWithAttachmentImpl(attachment);
    }

    @Override
    public AttachmentApi deleteCardAttachment(String classId, String cardId, String attachmentId) {
        logger.debug("delete attachment for classId = {} cardId = {} attachmentId = {}", classId, cardId, attachmentId);
        delete("classes/" + trimAndCheckNotBlank(classId) + "/cards/" + trimAndCheckNotBlank(cardId) + "/attachments/" + trimAndCheckNotBlank(attachmentId));
        return this;
    }

    @Override
    public AttachmentServiceWithData download(String classId, String cardId, String attachmentId) {
        byte[] data = getBytes("classes/" + trimAndCheckNotBlank(classId) + "/cards/" + trimAndCheckNotBlank(cardId) + "/attachments/" + trimAndCheckNotBlank(attachmentId) + "/file.out");
        return new AttachmentServiceWithDataImpl(new AttachmentData() {
            @Override
            public byte[] toByteArray() {
                return data;
            }
        });
    }

    @Override
    public BigByteArray exportAllDocumentsToZipFile() {
        return getBigBytes("system/dms/export");
    }

    @Override
    public AttachmentServiceWithPreview preview(String classId, String cardId, String attachmentId) {
        JsonElement jsonElement = get("classes/" + trimAndCheckNotBlank(classId) + "/cards/" + trimAndCheckNotBlank(cardId) + "/attachments/" + trimAndCheckNotBlank(attachmentId) + "/preview").asJson();
        boolean hasPreview = toBoolean(jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonPrimitive("hasPreview"));
        byte[] data;
        if (hasPreview) {
            data = urlToByteArray(toString(jsonElement.getAsJsonObject().getAsJsonObject("data").getAsJsonPrimitive("dataUrl")));
        } else {
            data = null;
        }
        return new AttachmentServiceWithPreviewImpl(new AttachmentPreview() {
            @Override
            public byte[] toByteArray() {
                return checkNotNull(data);
            }

            @Override
            public boolean hasPreview() {
                return hasPreview;
            }
        });
    }

    private class AttachmentServiceWithAttachmentImpl implements AttachmentServiceWithAttachment {

        private final Attachment attachment;

        public AttachmentServiceWithAttachmentImpl(Attachment attachment) {
            this.attachment = checkNotNull(attachment);
        }

        @Override
        public AttachmentApi then() {
            return AttachmentApiImpl.this;
        }

        @Override
        public Attachment getAttachment() {
            return attachment;
        }

    }

    private class AttachmentServiceWithDataImpl implements AttachmentServiceWithData {

        private final AttachmentData data;

        public AttachmentServiceWithDataImpl(AttachmentData data) {
            this.data = checkNotNull(data);
        }

        @Override
        public AttachmentApi then() {
            return AttachmentApiImpl.this;
        }

        @Override
        public AttachmentData getData() {
            return data;
        }

    }

    private class AttachmentServiceWithPreviewImpl implements AttachmentServiceWithPreview {

        private final AttachmentPreview data;

        public AttachmentServiceWithPreviewImpl(AttachmentPreview data) {
            this.data = checkNotNull(data);
        }

        @Override
        public AttachmentApi then() {
            return AttachmentApiImpl.this;
        }

        @Override
        public AttachmentPreview getPreview() {
            return data;
        }

    }

}
