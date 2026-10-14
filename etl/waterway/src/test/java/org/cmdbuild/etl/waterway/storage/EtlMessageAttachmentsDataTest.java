/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.etl.waterway.storage;

import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentStorage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author ataboga
 */
public class EtlMessageAttachmentsDataTest {

    @Test
    public void testCompressData() {
        int mega100 = 100 * 1024 * 1024;

        String largeString = generateLargeString(mega100);

        assertEquals(mega100, largeString.length());

        WaterwayMessageAttachment wyMessageAttachment = WaterwayMessageAttachmentImpl.builder()
                .withName("Name")
                .withStorage(WaterwayMessageAttachmentStorage.WMAS_REFERENCE)
                .withObject(largeString)
                .withType(WaterwayMessageAttachmentType.WMAT_TEXT)
                .build();

        EtlMessageAttachmentsData wyAttachmentData = new EtlMessageAttachmentsData(wyMessageAttachment);

        assertEquals(339194, wyAttachmentData.getData().length());

        WaterwayMessageAttachment fromData = wyAttachmentData.toAttachment();
        assertEquals(mega100, fromData.getText().length());
    }

    @Test
    public void testNoCompressData() {
        int mega19 = 19 * 1024 * 1024;
        String largeString = generateLargeString(mega19);

        assertEquals(mega19, largeString.length());

        WaterwayMessageAttachment wyMessageAttachment = WaterwayMessageAttachmentImpl.builder()
                .withName("Name")
                .withStorage(WaterwayMessageAttachmentStorage.WMAS_REFERENCE)
                .withObject(largeString)
                .withType(WaterwayMessageAttachmentType.WMAT_TEXT)
                .build();

        EtlMessageAttachmentsData wyAttachmentData = new EtlMessageAttachmentsData(wyMessageAttachment);

        assertEquals(mega19, wyAttachmentData.getData().length());

        WaterwayMessageAttachment fromData = wyAttachmentData.toAttachment();
        assertEquals(mega19, fromData.getText().length());
    }

    private String generateLargeString(int size) {
        String text = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(size);
        while (sb.length() < size) {
            sb.append(text);
        }
        return sb.substring(0, size);
    }

}
