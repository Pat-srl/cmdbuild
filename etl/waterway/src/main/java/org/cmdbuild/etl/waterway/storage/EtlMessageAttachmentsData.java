/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.apache.commons.codec.digest.DigestUtils.sha256;
import org.apache.commons.io.FileUtils;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentStorage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType;
import org.cmdbuild.utils.crypto.MagicUtils;
import static org.cmdbuild.utils.io.CmCompressionUtils.deflate;
import static org.cmdbuild.utils.io.CmCompressionUtils.inflate;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EtlMessageAttachmentsData {

    private final static MagicUtils.MagicUtilsHelper MAGIC_HELPER = MagicUtils.helper(encodeHexString(sha256("CMDBUILD_ETL_MESSAGE_DATA_V1")).substring(0, 6), 7, 13, 19, 21, 24, 27);
    private final static long COMPRESSION_THRESHOLD_BYTES = 20 * FileUtils.ONE_MB;

    private final String name, data;
    private final Map<String, String> meta;
    private final WaterwayMessageAttachmentStorage storage;
    private final WaterwayMessageAttachmentType type;

    @JsonCreator
    public EtlMessageAttachmentsData(@JsonProperty("name") String name, @JsonProperty("data") String data, @JsonProperty("meta") Map<String, String> meta, @JsonProperty("storage") WaterwayMessageAttachmentStorage storage, @JsonProperty("type") WaterwayMessageAttachmentType type) {
        this.name = checkNotBlank(name);
        this.data = nullToEmpty(data);
        this.meta = map(meta).immutable();
        this.storage = checkNotNull(storage);
        this.type = checkNotNull(type);
    }

    public EtlMessageAttachmentsData(WaterwayMessageAttachment attachment) {
        this.name = attachment.getName();
        String data = switch (attachment.getType()) {
            case WMAT_JSON, WMAT_TEXT ->
                attachment.getText();
            case WMAT_BYTES ->
                Base64.encodeBase64String(attachment.getBytes());
            default ->
                throw runtime("unsupported attachment type for serialization = %s", attachment.getType());
        };
        this.data = addMagicAndCompress(data);
        this.meta = map(attachment.getMeta()).immutable();;
        this.storage = attachment.getStorage();
        this.type = attachment.getType();
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    @JsonIgnore
    public WaterwayMessageAttachmentStorage getStorage() {
        return storage;
    }

    @JsonIgnore
    public WaterwayMessageAttachmentType getType() {
        return type;
    }

    @JsonProperty("storage")
    public String getStorageAsString() {
        return serializeEnum(storage);
    }

    @JsonProperty("type")
    public String getTypeAsString() {
        return serializeEnum(type);
    }

    @JsonIgnore
    public WaterwayMessageAttachment toAttachment() {
        String data = stripMagicAndDecompress(this.data);
        return WaterwayMessageAttachmentImpl.builder()
                .withName(name)
                .withStorage(storage)
                .withType(type)
                .withMeta(meta)
                .accept(b -> {
                    switch (type) {
                        case WMAT_JSON, WMAT_TEXT ->
                            b.withObject(data);
                        case WMAT_BYTES ->
                            b.withObject(Base64.decodeBase64(data));
                        default ->
                            throw runtime("unsupported attachment type for deserialization = %s", type);
                    }
                }).build();
    }

    private static String addMagicAndCompress(String data) {
        if (isNotBlank(data) && data.length() > COMPRESSION_THRESHOLD_BYTES) {
            data = Base64.encodeBase64String(deflate(data.getBytes()));
            return MAGIC_HELPER.embedMagicIfPossible(data);
        }
        return data;
    }

    private static String stripMagicAndDecompress(String data) {
        if (MAGIC_HELPER.hasMagic(data)) {
            return new String(inflate(Base64.decodeBase64(MAGIC_HELPER.stripMagic(data))), StandardCharsets.UTF_8);
        }
        return data;
    }
}
