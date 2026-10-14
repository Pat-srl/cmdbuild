/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.template;

import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.job.MapperConfig;
import org.cmdbuild.template.ExpressionInputData;
import org.cmdbuild.utils.lang.Builder;

/**
 *
 * <b>Beware</b>: has
 * <ul>
 * <li>{@link #ftlTemplateData};
 * </ul>
 *
 * @author afelice
 */
public class EmailExpressionInputData extends ExpressionInputData {

    /**
     * May contain data and expressions to evaluate
     */
    private final Map<String, Object> ftlTemplateData;

    public EmailExpressionInputData(EmailExpressionInputDataBuilder builder) {
        super(builder.innerBuilder);
        this.ftlTemplateData = builder.ftlTemplateData;
    }

    /**
     * Enables use of {@link #expression} and {@link #expressions}.
     *
     * @return extended builder
     */
    public static EmailExpressionInputDataBuilder extendedBuilder() {
        return new EmailExpressionInputDataBuilder();
    }

    public static EmailExpressionInputDataBuilder copyOf(EmailExpressionInputData source) {
        EmailExpressionInputDataBuilder builder = new EmailExpressionInputDataBuilder()
                .withTemplate(source.getTemplate())
                .withReceivedEmail(source.getReceivedEmail())
                .withMapperConfig(source.getMapperConfig())
                .withOtherData(source.getOtherData())
                .withForcedLanguage(source.getForcedLanguage())
                .withFtlTemplateData(source.getFtlTemplateData());

        // validated fields
        if (source.getClientCard() != null) {
            builder.withClientCard(source.getClientCard());
        }
        if (source.getServerCard() != null) {
            builder.withServerCard(source.getServerCard());
        }

        return builder;

    }

    public Map<String, Object> getFtlTemplateData() {
        return ftlTemplateData;
    }

    public static class EmailExpressionInputDataBuilder implements Builder<EmailExpressionInputData, EmailExpressionInputDataBuilder> {

        private ExpressionInputDataBuilder innerBuilder = new ExpressionInputDataBuilder();

        /**
         * May contain data and expressions to evaluate
         */
        private Map<String, Object> ftlTemplateData;

        @Override
        public EmailExpressionInputData build() {
            return new EmailExpressionInputData(this);
        }

        public EmailExpressionInputDataBuilder withFtlTemplateData(Map<String, Object> ftlTemplateData) {
            this.ftlTemplateData = ftlTemplateData;
            return this;
        }

        public EmailExpressionInputDataBuilder withTemplate(EmailTemplate template) {
            innerBuilder = innerBuilder.withTemplate(template);
            return this;
        }

        public EmailExpressionInputDataBuilder withClientCard(Card clientCard) {
            innerBuilder = innerBuilder.withClientCard(clientCard);
            return this;
        }

        public EmailExpressionInputDataBuilder withServerCard(Card serverCard) {
            innerBuilder = innerBuilder.withServerCard(serverCard);
            return this;
        }

        public EmailExpressionInputDataBuilder withReceivedEmail(Email receivedEmail) {
            innerBuilder = innerBuilder.withReceivedEmail(receivedEmail);
            return this;
        }

        public EmailExpressionInputDataBuilder withMapperConfig(MapperConfig mapperConfig) {
            innerBuilder = innerBuilder.withMapperConfig(mapperConfig);
            return this;
        }

        public EmailExpressionInputDataBuilder withOtherData(Map<String, Object> otherData) {
            innerBuilder = innerBuilder.withOtherData(otherData);
            return this;
        }

        /**
         *
         * @param language decided elsewhere
         * @return
         */
        public EmailExpressionInputDataBuilder withForcedLanguage(String language) {
            innerBuilder = innerBuilder.withForcedLanguage(language);
            return this;
        }

    } // end EmailExpressionInputDataBuilder class

}
