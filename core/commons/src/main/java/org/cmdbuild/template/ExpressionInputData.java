/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.template;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Optional;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.job.MapperConfig;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;

/**
 * This were attributes and operations (in constructor) in
 * <code>EmailTemplateProcessor</code>
 *
 * @author afelice
 */
public class ExpressionInputData {

    private final EmailTemplate template;
    private final Card clientCard;
    private final Map<String, Object> clientData; // calculated
    private final Card serverCard;
    private final Map<String, Object> serverData; // calculated
    private final Email receivedEmail;
    private final MapperConfig mapperConfig;
    private final Map<String, Object> otherData;

    /**
     * (in case) decided elsewhere
     */
    private final String forcedLanguage;

    public ExpressionInputData(ExpressionInputDataBuilder builder) {
        this.template = builder.template;
        this.clientCard = builder.clientCard;
        this.clientData = builder.clientData;
        this.serverCard = builder.serverCard;
        this.serverData = builder.serverData;
        this.receivedEmail = builder.receivedEmail;
        this.mapperConfig = builder.mapperConfig;
        this.otherData = builder.otherData;

        this.forcedLanguage = builder.forcedLanguage;
    }

    public Object getTemplateContextData() {
        return mapToLoggableStringLazy(Optional.ofNullable(getTemplate()).map(EmailTemplate::getMeta).orElse(emptyMap()));
    }

    public static ExpressionInputDataBuilder builder() {
        return new ExpressionInputDataBuilder();
    }

    public static ExpressionInputDataBuilder copyOf(ExpressionInputData source) {
        ExpressionInputDataBuilder builder = new ExpressionInputDataBuilder()
                .withClientCard(source.getClientCard())
                .withTemplate(source.getTemplate())
                .withReceivedEmail(source.getReceivedEmail())
                .withMapperConfig(source.getMapperConfig())
                .withForcedLanguage(source.getForcedLanguage())
                .withOtherData(source.getOtherData());

        // validated fields
        if (source.getServerCard() != null) {
            builder.withServerCard(source.getServerCard());
        }

        return builder;
    }

    public EmailTemplate getTemplate() {
        return template;
    }

    public Card getClientCard() {
        return clientCard;
    }

    /**
     * @return the loggable data for {@link #clientCard}
     */
    public Map<String, Object> getClientData() {
        return clientData;
    }

    public Card getServerCard() {
        return serverCard;
    }

    /**
     * @return the loggable data for {@link #serverCard}
     */
    public Map<String, Object> getServerData() {
        return serverData;
    }

    public Email getReceivedEmail() {
        return receivedEmail;
    }

    public MapperConfig getMapperConfig() {
        return mapperConfig;
    }

    public Map<String, Object> getOtherData() {
        return otherData;
    }

    /**
     *
     * @return null if no forced (decided elsewhere) language
     */
    public String getForcedLanguage() {
        return forcedLanguage;
    }

    public static class ExpressionInputDataBuilder implements Builder<ExpressionInputData, ExpressionInputDataBuilder> {

        protected EmailTemplate template;
        protected Card clientCard;
        protected Map<String, Object> clientData; // calculated
        protected Card serverCard;
        protected Map<String, Object> serverData; // calculated
        protected Email receivedEmail;
        protected MapperConfig mapperConfig;
        protected Map<String, Object> otherData = emptyMap();

        private String forcedLanguage;

        @Override
        public ExpressionInputData build() {
            return new ExpressionInputData(this);
        }

        public ExpressionInputDataBuilder withTemplate(EmailTemplate template) {
            this.template = template;
            return this;
        }

        public ExpressionInputDataBuilder withClientCard(Card clientCard) {
            this.clientCard = clientCard;
            this.clientData = fetchCardData(clientCard);
            return this;
        }

        public ExpressionInputDataBuilder withServerCard(Card serverCard) {
            checkNotNull(serverCard);
            this.serverCard = serverCard;
            this.serverData = fetchCardData(serverCard);
            return this;
        }

        public ExpressionInputDataBuilder withReceivedEmail(Email receivedEmail) {
            this.receivedEmail = receivedEmail;
            return this;
        }

        public ExpressionInputDataBuilder withMapperConfig(MapperConfig mapperConfig) {
            this.mapperConfig = mapperConfig;
            return this;
        }

        public ExpressionInputDataBuilder withOtherData(Map<String, Object> otherData) {
            if (otherData != null) {
                this.otherData = otherData;
            }
            return this;
        }

        /**
         *
         * @param language decided elsewhere
         * @return
         */
        public ExpressionInputDataBuilder withForcedLanguage(String language) {
            this.forcedLanguage = language;
            return this;
        }

        private static Map<String, Object> fetchCardData(Card card) {
            return Optional.ofNullable(card).map(Card::getAllValuesAsMap).orElse(emptyMap());
        }

    } // end builder

}
