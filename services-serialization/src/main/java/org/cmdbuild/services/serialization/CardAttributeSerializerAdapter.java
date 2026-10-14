/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization;

import java.util.Map;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.utils.lang.CmMapUtils;

/**
 * Handle (make explicit) recursion when serializing Card attributes.
 *
 * @author afelice
 */
public interface CardAttributeSerializerAdapter {

    CmMapUtils.FluentMap<String, Object> serialize(Attribute attribute, Map<String, Object> cardData);
}
