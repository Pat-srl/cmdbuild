/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.fluent;

import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface NewAttachment {

    NewAttachment withName(String name);

    NewAttachment withDescription(String description);

    NewAttachment withCategory(String category);

    NewAttachment withDocument(Object document);

    NewAttachment withMeta(Map<String, Object> meta);

    void upload();

    default NewAttachment withMeta(Object... keyValues) {
        return withMeta((Map) map(keyValues));
    }
}
