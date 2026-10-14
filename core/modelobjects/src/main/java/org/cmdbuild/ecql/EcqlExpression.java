/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql;

import java.util.Map;

public interface EcqlExpression {

	String getEcql();

	Map<String, Object> getContext();
}
