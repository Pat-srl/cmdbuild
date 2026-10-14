/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.ai.core.api;

/**
 *
 * @author ataboga
 */
public record AiRequest(String query, String userId, String context) {
}
