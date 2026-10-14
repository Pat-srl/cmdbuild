/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.localization;

import java.util.Locale;

public class LanguageUtils {

	public static Locale toLocale(String language) {
		String[] splitLang = language.split("_");
		if (splitLang.length > 1) {
			return new Locale(splitLang[0], splitLang[1]);
		} else {
			return new Locale(splitLang[0]);
		}
	}

}
