/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.springframework.stereotype.Component;

import java.util.Map;

import static org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils.encryptValueIfNotEncrypted;

/**
 * @author ldare
 */
@Component
public class UtilsWsCommand {

    private static final String CRYPTO_VALUE = "value";

    public UtilsWsCommand() {
    }

    public String doEncryptValue(Map<String, String> payload) {
        return encryptValueIfNotEncrypted(payload.get(CRYPTO_VALUE));
    }
}
