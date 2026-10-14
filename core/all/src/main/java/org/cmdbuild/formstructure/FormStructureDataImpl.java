/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formstructure;

import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_Form")
public class FormStructureDataImpl implements FormStructureData {

    private final Long id;
    private final String code, data;

    private FormStructureDataImpl(FormStructureDataImplBuilder builder) {
        this.id = builder.id;
        this.code = checkNotBlank(builder.code);
        this.data = checkNotBlank(builder.data);
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getCode() {
        return code;
    }

    @Override
    @CardAttr
    public String getData() {
        return data;
    }

    public static FormStructureDataImplBuilder builder() {
        return new FormStructureDataImplBuilder();
    }

    public static FormStructureDataImplBuilder copyOf(FormStructureData source) {
        return new FormStructureDataImplBuilder()
                .withId(source.getId())
                .withCode(source.getCode())
                .withData(source.getData());
    }

    public static class FormStructureDataImplBuilder implements Builder<FormStructureDataImpl, FormStructureDataImplBuilder> {

        private Long id;
        private String code;
        private String data;

        public FormStructureDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public FormStructureDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public FormStructureDataImplBuilder withData(String data) {
            this.data = data;
            return this;
        }

        @Override
        public FormStructureDataImpl build() {
            return new FormStructureDataImpl(this);
        }

    }
}
