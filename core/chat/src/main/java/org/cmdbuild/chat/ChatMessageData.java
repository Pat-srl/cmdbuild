/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.chat;

import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface ChatMessageData {

    String getTarget();

    @Nullable
    String getSubject();

    @Nullable
    String getContent();

    @Nullable
    String getThread();

    Map<String, String> getMeta();

    @Nullable
    default String getMeta(String key) {
        return getMeta().get(checkNotBlank(key));
    }
}
