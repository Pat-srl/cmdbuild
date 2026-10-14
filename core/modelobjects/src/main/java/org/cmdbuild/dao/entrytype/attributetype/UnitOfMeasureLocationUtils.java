/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype.attributetype;

public class UnitOfMeasureLocationUtils {

    public static String serializeUnitOfMeasureLocation(UnitOfMeasureLocation value) {
        return value.toString().replaceFirst("UML_", "");
    }

}
