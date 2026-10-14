/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.ai;

/**
 *
 * @author ataboga
 */
public interface AiConfiguration {

    final String AI_CONFIG_NAMESPACE = "org.cmdbuild.ai",
            AI_CONFIG_PROVIDER = AI_CONFIG_NAMESPACE + ".service.type",
            AI_CONFIG_CHAT_NAME = AI_CONFIG_NAMESPACE + ".chat.name",
            AI_CONFIG_CHAT_AVATAR = AI_CONFIG_NAMESPACE + ".chat.avatar";

    boolean isEnabled();

    String getService();

    String getName();

    String getAvatar();
}
