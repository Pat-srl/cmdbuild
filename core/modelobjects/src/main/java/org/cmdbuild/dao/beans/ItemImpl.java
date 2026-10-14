/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

public class ItemImpl implements Item {

    private final Long id;
    private final String typeName;
    private final Map<String, Object> data;

    public ItemImpl(Map<String, Object> data) {
        this.data = map(data).immutable();
        this.id = toLongOrNull(data.get("Id"));
        this.typeName = toStringNotBlank(data.get("IdClass"));
    }

    @Override
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    public static ItemImplBuilder builder() {
        return new ItemImplBuilder();
    }

    public static ItemImplBuilder copyOf(Item source) {
        return new ItemImplBuilder().withData(source.getData());
    }

    public static class ItemImplBuilder implements Builder<ItemImpl, ItemImplBuilder> {

        private final Map<String, Object> data = map();

        public ItemImplBuilder withId(Long id) {
            data.put("Id", id);
            return this;
        }

        public ItemImplBuilder withTypeName(String typeName) {
            data.put("IdClass", typeName);
            return this;
        }

        public ItemImplBuilder withData(Map<String, Object> data) {
            this.data.putAll(data);
            return this;
        }

        public ItemImplBuilder addData(String key, Object value) {
            this.data.put(key, value);
            return this;
        }

        @Override
        public ItemImpl build() {
            return new ItemImpl(data);
        }

    }
}
