/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

/**
 * @author ldare
 */
@Component
public class CharsetsWsCommand {

    public CharsetsWsCommand() {
    }

    public Set<Map.Entry<String, Charset>> doReadAvailableCharsets() {
        return Charset.availableCharsets().entrySet();
    }
}
