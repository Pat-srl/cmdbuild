/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.utils;

import org.cmdbuild.config.api.ConfigComponent;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;

public class ConfigBeanUtils {

	public static String getNamespace(Object bean) {
		return trimAndCheckNotBlank(bean.getClass().getAnnotation(ConfigComponent.class).value());
	}
}
