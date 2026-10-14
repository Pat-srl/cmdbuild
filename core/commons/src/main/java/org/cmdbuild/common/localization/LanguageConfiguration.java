/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.localization;

import java.util.Locale;
import static org.cmdbuild.common.localization.LanguageUtils.toLocale;

public interface LanguageConfiguration {

	default Locale getDefaultLocale() {
		return toLocale(getDefaultLanguage());
	}

	String getDefaultLanguage();
}
