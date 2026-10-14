/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author afelice
 */
public class EcqlFilterSerializationHelper {

    /**
     *
     * @param key
     * @param filter the string, for example
     * <code>from Floor where Id in (/(select \"Id\" from \"Floor\" where \"Building\" = 0{client:Building.Id})/)</code>
     * @param ecqlKey
     * @param ecqlId the EcqlId, for example
     * <code>1kpo50n3y230ezume2pywttnt8b</code>
     * @param ecqlBindingInfo
     * @return
     */
    public static CmMapUtils.FluentMap<String, Object> buildEcqlFilterStuff(String key, String filter, String ecqlKey, String ecqlId, EcqlBindingInfo ecqlBindingInfo) {
        FluentMap<String, Object> map = map(key, filter);
        addEcqlFilter(map, ecqlKey, ecqlId, ecqlBindingInfo);
        return map;
    }

    public static void addEcqlFilter(FluentMap<String, Object> map, String ecqlKey, String ecqlId, EcqlBindingInfo ecqlBindingInfo) {
        map.put(ecqlKey, map(
                "id", ecqlId,
                "bindings", map("server", ecqlBindingInfo.getServerBindings(), "client", ecqlBindingInfo.getClientBindings())
        ));
    }
}
