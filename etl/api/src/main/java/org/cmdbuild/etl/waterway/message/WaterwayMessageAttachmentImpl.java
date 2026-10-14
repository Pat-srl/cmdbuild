/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.message;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Map;
import static org.cmdbuild.etl.job.PayloadUtils.isValidPayload;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentStorage.WMAS_EMBEDDED;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_BYTES;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_JSON;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_OBJECT;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_TEXT;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

public class WaterwayMessageAttachmentImpl implements WaterwayMessageAttachment {

    private final String name;
    private final Map<String, String> meta;
    private final WaterwayMessageAttachmentStorage storage;
    private final WaterwayMessageAttachmentType type;
    private final Object object;

    private WaterwayMessageAttachmentImpl(WaterwayMessageAttachmentImplBuilder builder) {
        this.name = checkNotBlank(builder.name, "missing attachment name");
        this.storage = firstNotNull(builder.storage, WMAS_EMBEDDED);
        this.type = checkNotNull(builder.type);
        this.object = checkNotNull(builder.object);
        checkArgument(isValidPayload(object), "invalid message attachment payload =< {} > with type = {}", abbreviate(object), getClassOfNullable(object).getName());
        WaterwayMessageAttachmentType actualType = switch (storage) {
            case WMAS_EMBEDDED ->
                type;
            case WMAS_REFERENCE ->
                WMAT_TEXT;//TODO check this
        };
        switch (actualType) {
            case WMAT_BYTES ->
                checkArgument(object instanceof byte[], "invalid object type = %s for attachment type = %s storage = %s", getClassOfNullable(object).getName(), type, storage);
            case WMAT_JSON, WMAT_TEXT ->
                checkArgument(object instanceof String, "invalid object type = %s for attachment type = %s storage = %s", getClassOfNullable(object).getName(), type, storage);
            case WMAT_MESSAGE ->
                checkArgument(object instanceof WaterwayMessage, "invalid object type = %s for attachment type = %s storage = %s", getClassOfNullable(object).getName(), type, storage);
        }
        this.meta = map(builder.meta).accept(m -> {
            if (isBlank(m.get(WMA_CONTENT_TYPE)) && equal(type, WMAT_JSON)) {
                m.put(WMA_CONTENT_TYPE, "application/json");
            }
            if (isBlank(m.get(WMA_BYTE_SIZE)) && equal(storage, WMAS_EMBEDDED)) {
                switch (type) {
                    case WMAT_BYTES -> {
                        m.put(WMA_BYTE_SIZE, toStringNotBlank(getBytes().length));
                    }
                    case WMAT_JSON, WMAT_TEXT -> {
                        m.put(WMA_BYTE_SIZE, toStringNotBlank(getText().getBytes(UTF_8).length));
                    }
                }
            }
        }).immutable();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getMeta() {
        return meta;
    }

    @Override
    public WaterwayMessageAttachmentStorage getStorage() {
        return storage;
    }

    @Override
    public WaterwayMessageAttachmentType getType() {
        return type;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "WaterwayMessageAttachment{" + "name=" + name + ", storage=" + storage + ", type=" + type + '}';
    }

    public static WaterwayMessageAttachmentImplBuilder builder() {
        return new WaterwayMessageAttachmentImplBuilder();
    }

    public static WaterwayMessageAttachmentImplBuilder copyOf(WaterwayMessageAttachment source) {
        return new WaterwayMessageAttachmentImplBuilder()
                .withName(source.getName())
                .withMeta(source.getMeta())
                .withStorage(source.getStorage())
                .withType(source.getType())
                .withObject(source.getObject());
    }

    public static class WaterwayMessageAttachmentImplBuilder implements Builder<WaterwayMessageAttachmentImpl, WaterwayMessageAttachmentImplBuilder> {

        private String name;
        private final Map<String, String> meta = map();
        private WaterwayMessageAttachmentStorage storage;
        private WaterwayMessageAttachmentType type;
        private Object object;

        public WaterwayMessageAttachmentImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public WaterwayMessageAttachmentImplBuilder withMeta(Map<String, String> meta) {
            this.meta.putAll(meta);
            return this;
        }

        public WaterwayMessageAttachmentImplBuilder withMeta(String key, String value) {
            this.meta.put(key, value);
            return this;
        }

        public WaterwayMessageAttachmentImplBuilder clearMeta() {
            this.meta.clear();
            return this;
        }

        public WaterwayMessageAttachmentImplBuilder withStorage(WaterwayMessageAttachmentStorage storage) {
            this.storage = storage;
            return this;
        }

        public WaterwayMessageAttachmentImplBuilder withType(WaterwayMessageAttachmentType type) {
            this.type = type;
            return this;
        }

        public WaterwayMessageAttachmentImplBuilder withObject(Object payload) {
            this.object = payload;
            return this;
        }

        public WaterwayMessageAttachmentImplBuilder fromObject(Object payload) {
            withStorage(WMAS_EMBEDDED);
            if (payload instanceof String) {
                withObject(payload).withType(WMAT_TEXT);
            } else if (payload instanceof byte[]) {
                withObject(payload).withType(WMAT_BYTES);
            } else if (payload instanceof DataSource dataSource) {
                withObject(toByteArray(dataSource)).withType(WMAT_BYTES).withMeta(map(WMA_FILE_NAME, dataSource.getName(), WMA_CONTENT_TYPE, dataSource.getContentType()));
            } else {
                withObject(payload).withType(WMAT_OBJECT);
            }
            return this;
        }

        @Override
        public WaterwayMessageAttachmentImpl build() {
            return new WaterwayMessageAttachmentImpl(this);
        }

    }
}
