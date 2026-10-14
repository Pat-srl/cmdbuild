/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api;

import java.util.Map;
import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.email.template.EmailTemplate;

public interface UtilsApi {

    String applyTemplate(String template, Card card);

    String applyTemplate(String template, Map<String, ?> data);

    EmailTemplate getTemplateOrNull(String templateCode);

}
