/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cql.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CqlUtils {

	public static @Nullable
	String getFrom(String cqlExpr) {
		//cannot use cql parsing if we haven't resolved easytemplate stuff before, so we will use simple regexp parsing here
//		AtomicReference<String> classId = new AtomicReference<>();
//		cqLService.compileAndAnalyze(cql, emptyMap(), new DummyCQLProcessingCallback() {
//			@Override
//			public void from(Classe classe) {
//				classId.set(classe.getName());
//			}
//
//		});
		Matcher matcher = Pattern.compile("from\\s+([^\\s]+)", Pattern.CASE_INSENSITIVE).matcher(checkNotBlank(cqlExpr));
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

}
