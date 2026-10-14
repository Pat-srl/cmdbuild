/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.translation;

import static java.lang.String.format;
import org.cmdbuild.gis.GisAttribute;

/**
 *
 * @author afelice
 */
public class TranslationUtils_WithGis extends TranslationUtils {

    public static String gisAttributeDescriptionTranslationCode(GisAttribute attribute) {
        return format("gisattributeclass.%s.%s.description", attribute.getOwnerClassName(), attribute.getLayerName());
    }

}
