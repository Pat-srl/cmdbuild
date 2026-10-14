/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.template;

import java.util.Collection;

public interface TemplateBindings {

    Collection<String> getClientBindings();

    Collection<String> getServerBindings();

}
