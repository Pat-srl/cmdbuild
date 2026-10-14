/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.template;

import java.util.Map;
import org.cmdbuild.email.Email;
import org.cmdbuild.template.ExpressionInputData;
import org.cmdbuild.template.SimpleExpressionInputData;
import org.cmdbuild.template.TemplateBindings;

/**
 *
 * @author afelice
 */
public interface EmailTemplateProcessorService {

    /**
     * Processor to parse an Email/Email template.
     *
     * @param email if <code>null</code>, the returned email is a synthesized
     * email with status {@link EmailStatus#ES_DRAFT}
     * @param expressionInputData
     * @return
     */
    Email processEmail(Email email, ExpressionInputData expressionInputData);

    String processExpression(SimpleExpressionInputData simpleExprInputData);

    Map<String, Object> processMultipleExpressions(SimpleExpressionInputData simpleExprInputData);

    TemplateBindings fetchTemplateBindings(EmailTemplate template);

}
