/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_Translation")
public class TranslationImpl implements Translation {

    private final String code, lang, value;
    private final Long id;

    private TranslationImpl(SimpleTranslationDataBuilder builder) {
        this.id = builder.id;
        this.code = checkNotBlank(builder.code);
        this.lang = checkNotBlank(builder.lang);
        this.value = checkNotNull(builder.value);
    }

    @CardAttr(ATTR_ID)
    @Nullable
    public Long getId() {
        return id;
    }

    public boolean hasId() {
        return isNotNullAndGtZero(id);
    }

    @CardAttr(ATTR_CODE)
    @Override
    public String getCode() {
        return code;
    }

    @CardAttr("Lang")
    @Override
    public String getLang() {
        return lang;
    }

    @CardAttr("Value")
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "SimpleTranslationData{" + "code=" + code + ", lang=" + lang + ", value=" + value + '}';
    }

    public static SimpleTranslationDataBuilder builder() {
        return new SimpleTranslationDataBuilder();
    }

    public static SimpleTranslationDataBuilder copyOf(Translation source) {
        return new SimpleTranslationDataBuilder()
                .withCode(source.getCode())
                .withLang(source.getLang())
                .withValue(source.getValue());
    }

    public static SimpleTranslationDataBuilder copyOf(TranslationImpl source) {
        return copyOf((Translation) source)
                .withId(source.getId());
    }

    public static class SimpleTranslationDataBuilder implements Builder<TranslationImpl, SimpleTranslationDataBuilder> {

        private Long id;
        private String lang;
        private String value;
        private String code;

        public SimpleTranslationDataBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SimpleTranslationDataBuilder withLang(String lang) {
            this.lang = lang;
            return this;
        }

        public SimpleTranslationDataBuilder withValue(String content) {
            this.value = content;
            return this;
        }

        public SimpleTranslationDataBuilder withCode(String key) {
            this.code = key;
            return this;
        }

        @Override
        public TranslationImpl build() {
            return new TranslationImpl(this);
        }

    }
}
