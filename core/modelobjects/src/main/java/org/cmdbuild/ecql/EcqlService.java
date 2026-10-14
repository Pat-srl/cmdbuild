/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql;

public interface EcqlService {

    String prepareCqlExpression(String ecqlId, String jsContext);

}
