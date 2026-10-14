/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.beans;

import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.email.EmailSignature;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_EmailSignature")
public class EmailSignatureImpl implements EmailSignature {

    private final Long id;
    private final String code, description, contentHtml;
    private final boolean active;

    private EmailSignatureImpl(EmailSignatureImplBuilder builder) {
        this.id = builder.id;
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.contentHtml = checkNotBlank(builder.contentHtml);
        this.active = firstNotNull(builder.active, true);
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @CardAttr(ATTR_CODE)
    @Override
    public String getCode() {
        return code;
    }

    @CardAttr(ATTR_DESCRIPTION)
    @Override
    public String getDescription() {
        return description;
    }

    @CardAttr
    @Override
    public String getContentHtml() {
        return contentHtml;
    }

    @CardAttr
    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "EmailSignature{" + "id=" + id + ", code=" + code + '}';
    }

    public static EmailSignatureImplBuilder builder() {
        return new EmailSignatureImplBuilder();
    }

    public static EmailSignatureImplBuilder copyOf(EmailSignature source) {
        return new EmailSignatureImplBuilder()
                .withId(source.getId())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withContentHtml(source.getContentHtml())
                .withActive(source.isActive());
    }

    public static class EmailSignatureImplBuilder implements Builder<EmailSignatureImpl, EmailSignatureImplBuilder> {

        private Long id;
        private String code;
        private String description;
        private String contentHtml;
        private Boolean active;

        public EmailSignatureImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EmailSignatureImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public EmailSignatureImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public EmailSignatureImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EmailSignatureImplBuilder withContentHtml(String contentHtml) {
            this.contentHtml = contentHtml;
            return this;
        }

        @Override
        public EmailSignatureImpl build() {
            return new EmailSignatureImpl(this);
        }

    }
}
