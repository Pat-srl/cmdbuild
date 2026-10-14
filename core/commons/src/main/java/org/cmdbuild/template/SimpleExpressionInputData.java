/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.template;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.job.MapperConfig;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.utils.lang.Builder;

/**
 *
 * <b>Beware</b>: has {@link ExpressionInputData} plus
 * <ul>
 * <li>{@link #expression};
 * <li>{@link #expressions};
 * </ul>
 *
 * @author afelice
 */
public class SimpleExpressionInputData extends ExpressionInputData {

    private final String expression;
    /**
     * May contain data and expressions to evaluate
     */
    private final Map<String, ?> expressions;

    public SimpleExpressionInputData(SimpleExpressionInputDataBuilder builder) {
        super(builder.innerBuilder);
        this.expression = builder.expression;
        this.expressions = builder.expressions;
    }

    /**
     * Enables use of {@link #expression} and {@link #expressions}.
     *
     * @return extended builder
     */
    public static SimpleExpressionInputDataBuilder extendedBuilder() {
        return new SimpleExpressionInputDataBuilder();
    }

    public static SimpleExpressionInputDataBuilder copyOf(SimpleExpressionInputData source) {
        SimpleExpressionInputDataBuilder builder = new SimpleExpressionInputDataBuilder()
                .withClientCard(source.getClientCard())
                .withTemplate(source.getTemplate())
                .withReceivedEmail(source.getReceivedEmail())
                .withMapperConfig(source.getMapperConfig())
                .withOtherData(source.getOtherData())
                .withForcedLanguage(source.getForcedLanguage())
                .withExpression(source.getExpression());

        // validated fields
        if (source.getServerCard() != null) {
            builder.withServerCard(source.getServerCard());
        }
        if (source.getExpressions() != null) {
            builder.withExpressions(source.getExpressions());
        }

        return builder;
    }

    public String getExpression() {
        return expression;
    }

    /**
     *
     * @return expressions may contain both data and multiple expressions to
     * evaluate.
     */
    public Map<String, ?> getExpressions() {
        return expressions;
    }

    public static class SimpleExpressionInputDataBuilder implements Builder<SimpleExpressionInputData, SimpleExpressionInputDataBuilder> {

        private ExpressionInputDataBuilder innerBuilder = new ExpressionInputDataBuilder();

        private String expression;

        /**
         * May contain data and expressions to evaluate
         */
        private Map<String, ?> expressions;

        @Override
        public SimpleExpressionInputData build() {
            return new SimpleExpressionInputData(this);
        }

        public SimpleExpressionInputDataBuilder withExpression(String expression) {
            this.expression = expression;
            return this;
        }

        /**
         *
         * @param expressions may contain both data and multiple expressions to
         * evaluate.
         * @return
         */
        public SimpleExpressionInputDataBuilder withExpressions(Map<String, ?> expressions) {
            checkNotNull(expressions);
            this.expressions = expressions;
            return this;
        }

        public SimpleExpressionInputDataBuilder withTemplate(EmailTemplate template) {
            innerBuilder = innerBuilder.withTemplate(template);
            return this;
        }

        public SimpleExpressionInputDataBuilder withClientCard(Card clientCard) {
            innerBuilder = innerBuilder.withClientCard(clientCard);
            return this;
        }

        public SimpleExpressionInputDataBuilder withServerCard(Card serverCard) {
            innerBuilder = innerBuilder.withServerCard(serverCard);
            return this;
        }

        public SimpleExpressionInputDataBuilder withReceivedEmail(Email receivedEmail) {
            innerBuilder = innerBuilder.withReceivedEmail(receivedEmail);
            return this;
        }

        public SimpleExpressionInputDataBuilder withMapperConfig(MapperConfig mapperConfig) {
            innerBuilder = innerBuilder.withMapperConfig(mapperConfig);
            return this;
        }

        public SimpleExpressionInputDataBuilder withOtherData(Map<String, Object> otherData) {
            innerBuilder = innerBuilder.withOtherData(otherData);
            return this;
        }

        public SimpleExpressionInputDataBuilder withForcedLanguage(String language) {
            innerBuilder = innerBuilder.withForcedLanguage(language);
            return this;
        }

    } // end SimpleExpressionInputDataBuilder class

}
