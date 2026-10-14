/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.template;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.job.MapperConfig;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.utils.lang.Builder;
import org.slf4j.Logger;

/**
 *
 * <b>Beware</b>: has {@link ExpressionInputData} plus
 * <ul>
 * <li>{@link #ftlTemplateData};
 *
 * <p>
 * Initialized with all stuff:
 * <ul>
 * <li>clientCard data;
 * <li>serverCard data;
 * <li>serialization of Email data;
 * <li>other data;
 * <li>language;
 * <li>{@link UtilsCmApi};
 * <li>{@link Logger}.
 * </ul>
 * </ul>
 *
 * @author afelice
 */
public class TemplateExpressionInputData extends ExpressionInputData {

    /**
     * Initialized with all stuff:
     * <ul>
     * <li>clientCard data;
     * <li>serverCard data;
     * <li>serialization of Email data;
     * <li>other data;
     * <li>language;
     * <li>{@link UtilsCmApi};
     * <li>{@link Logger}.
     * </ul>
     */
    private Map<String, Object> ftlTemplateData;

    public TemplateExpressionInputData(TemplateExpressionInputDataBuilder builder) {
        super(builder.innerBuilder);
        this.ftlTemplateData = builder.ftlTemplateData;
    }

    /**
     * Enables use of {@link #ftlTemplatedata}.
     *
     * @return extended builder
     */
    public static TemplateExpressionInputDataBuilder extendedBuilder() {
        return new TemplateExpressionInputDataBuilder();
    }

    public static TemplateExpressionInputDataBuilder buildFrom(ExpressionInputData source) {
        TemplateExpressionInputDataBuilder builder = new TemplateExpressionInputDataBuilder()
                .withClientCard(source.getClientCard())
                .withTemplate(source.getTemplate())
                .withReceivedEmail(source.getReceivedEmail())
                .withMapperConfig(source.getMapperConfig())
                .withOtherData(source.getOtherData())
                .withForcedLanguage(source.getForcedLanguage());

        // validated fields
        if (source.getServerCard() != null) {
            builder.withServerCard(source.getServerCard());
        }

        return builder;
    }

    /**
     *
     * @return map with all stuff:
     * <ul>
     * <li>clientCard data;
     * <li>serverCard data;
     * <li>serialization of Email data;
     * <li>other data;
     * <li>language;
     * <li>{@link UtilsCmApi};
     * <li>{@link Logger}.
     * </ul>
     */
    public Map<String, Object> getFtlTemplateData() {
        return ftlTemplateData;
    }

    public static class TemplateExpressionInputDataBuilder implements Builder<TemplateExpressionInputData, TemplateExpressionInputDataBuilder> {

        private ExpressionInputDataBuilder innerBuilder = new ExpressionInputDataBuilder();

        private Map<String, Object> ftlTemplateData = emptyMap();

        @Override
        public TemplateExpressionInputData build() {
            return new TemplateExpressionInputData(this);
        }

        /**
         *
         * @param ftlTemplateData contains data to bind to placeholders in
         * template.
         * @return
         */
        public TemplateExpressionInputDataBuilder withFtlTemplateData(Map<String, Object> ftlTemplateData) {
            checkNotNull(ftlTemplateData);
            this.ftlTemplateData = ftlTemplateData;
            return this;
        }

        public TemplateExpressionInputDataBuilder withTemplate(EmailTemplate template) {
            innerBuilder = innerBuilder.withTemplate(template);
            return this;
        }

        public TemplateExpressionInputDataBuilder withClientCard(Card clientCard) {
            innerBuilder = innerBuilder.withClientCard(clientCard);
            return this;
        }

        public TemplateExpressionInputDataBuilder withServerCard(Card serverCard) {
            innerBuilder = innerBuilder.withServerCard(serverCard);
            return this;
        }

        public TemplateExpressionInputDataBuilder withReceivedEmail(Email receivedEmail) {
            innerBuilder = innerBuilder.withReceivedEmail(receivedEmail);
            return this;
        }

        public TemplateExpressionInputDataBuilder withMapperConfig(MapperConfig mapperConfig) {
            innerBuilder = innerBuilder.withMapperConfig(mapperConfig);
            return this;
        }

        public TemplateExpressionInputDataBuilder withOtherData(Map<String, Object> otherData) {
            innerBuilder = innerBuilder.withOtherData(otherData);
            return this;
        }

        public TemplateExpressionInputDataBuilder withForcedLanguage(String language) {
            innerBuilder = innerBuilder.withForcedLanguage(language);
            return this;
        }

    } // end TemplateExpressionInputDataBuilder class

}
