/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import com.google.common.base.Objects;
import jakarta.annotation.Nullable;

public interface ClusterMessageReceivedEvent {

	ClusterMessage getClusterMessage();

	default String getMessageType() {
		return getClusterMessage().getMessageType();
	}

	default boolean isOfType(String messageType) {
		return Objects.equal(messageType, getMessageType());
	}

	@Nullable
	default <T> T getData(String key) {
		return (T) getClusterMessage().getMessageData().get(key);
	}

}
