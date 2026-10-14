/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.http;

/**
 *
 * @author ldare
 */
public interface ExtServiceConfiguration {

    String getUrl();

    String getUsername();

    String getPassword();

    Long getTimeout();
}
