/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.translation;

import org.cmdbuild.gis.GisAttribute;
import static org.cmdbuild.translation.TranslationUtils_WithGis.gisAttributeDescriptionTranslationCode;
/**
 * Extracted from {@link ObjectTranslationService} to avoid dependency on
 * {@link GisAttribute} and <codee>gis-api</code> module (which in turn depends
 * on
 * *
 * @author afelice
 */
public interface ObjectTranslationService_WithGis extends ObjectTranslationService {

    default String translateGisAttributeDescription(GisAttribute attribute) {
        return translateByCode(gisAttributeDescriptionTranslationCode(attribute), attribute.getDescription());
    }
}
