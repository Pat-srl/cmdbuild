/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.beans;

import org.cmdbuild.email.template.EmailTemplateInlineConfig;
import org.cmdbuild.utils.json.JsonBean;

@JsonBean(EmailTemplateInlineDataImpl.class)
public interface EmailTemplateInlineData extends EmailTemplateInlineConfig {

}
