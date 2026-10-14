/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

public interface EmailSignatureService {

    List<EmailSignature> getAll();

    EmailSignature getOneByCode(String code);

    EmailSignature getOne(long id);

    EmailSignature create(EmailSignature emailSignature);

    EmailSignature update(EmailSignature emailSignature);

    void delete(long id);
    
    String getSignatureHtmlForCurrentUser(long id);

    default EmailSignature getOne(String idOrCode) {
        return NumberUtils.isCreatable(idOrCode) ? getOne(toLong(idOrCode)) : getOneByCode(idOrCode);
    }
}
