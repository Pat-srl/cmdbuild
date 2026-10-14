/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.io.InputStream;
import java.util.List;
import org.cmdbuild.client.rest.model.Attachment;
import org.cmdbuild.utils.io.BigByteArray;

public interface AttachmentApi {

    List<Attachment> getCardAttachments(String classId, String cardId);

    List<Attachment> getEmailAttachments(String classId, String cardId, String emailId);

    List<Attachment> getAttachmentHistory(String classId, String cardId, String fileName);

    AttachmentServiceWithAttachment createCardAttachment(String classId, String cardId, String fileName, InputStream data);

    AttachmentServiceWithAttachment updateCardAttachment(String classId, String cardId, String attachmentId, String fileName, InputStream data);

    AttachmentApi deleteCardAttachment(String classId, String cardId, String attachmentId);

    AttachmentServiceWithData download(String classId, String cardId, String attachmentId);

    AttachmentServiceWithPreview preview(String classId, String cardId, String attachmentId);

    BigByteArray exportAllDocumentsToZipFile();

    interface AttachmentServiceWithAttachment {

        AttachmentApi then();

        Attachment getAttachment();
    }

    interface AttachmentServiceWithData {

        AttachmentApi then();

        AttachmentData getData();
    }

    interface AttachmentServiceWithPreview {

        AttachmentApi then();

        AttachmentPreview getPreview();

        default boolean hasPreview() {
            return getPreview().hasPreview();
        }
    }

    interface AttachmentData {

        byte[] toByteArray();
    }

    interface AttachmentPreview extends AttachmentData {

        boolean hasPreview();
    }
}
