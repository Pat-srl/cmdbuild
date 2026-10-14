/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import com.google.common.base.Joiner;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.transform;
import jakarta.annotation.Nullable;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EmailAddressUtils {

    public static InternetAddress parseEmailAddress(String emailAddress) {
        try {
            return new InternetAddress(checkNotBlank(emailAddress));
        } catch (AddressException ex) {
            throw new EmailException(ex, "error parsing email address from string =< %s >", emailAddress);
        }
    }

    public static List<InternetAddress> parseEmailAddressList(@Nullable String emailAddressList) {
        try {
            return list(InternetAddress.parse(nullToEmpty(emailAddressList), false));
        } catch (AddressException ex) {
            throw new EmailException(ex, "error parsing email address list from string =< %s >", emailAddressList);
        }
    }

    public static List<String> parseEmailAddressListAsStrings(@Nullable String emailAddressList) {
        return list(transform(parseEmailAddressList(emailAddressList), InternetAddress::toString));
    }

    public static String addressListToString(@Nullable List<String> list) {
        return Joiner.on(", ").join(CmCollectionUtils.nullToEmpty(list));//TODO check this
    }

}
