package org.cmdbuild.utils.ipaddress;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.IncompatibleAddressException;
import jakarta.annotation.Nullable;
import java.util.Objects;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public class CmIpAddressUtils {

    @Nullable
    public static boolean isIpAddress(@Nullable Object value) {
        if (isNotBlank(value)) {
            return new IPAddressString(value.toString()).isIPAddress();
        } else {
            return false;
        }
    }

    public static String toIpAddress(@Nullable Object value) {
        try {
            IPAddressString ipAddress = new IPAddressString(value.toString());
            if ((ipAddress.isIPv4() && Objects.equals(ipAddress.getNetworkPrefixLength(), 32)) || (ipAddress.isIPv6() && Objects.equals(ipAddress.getNetworkPrefixLength(), 128))) {
                return ipAddress.toHostAddress().toString();
            }
            return ipAddress.toString();
        } catch (IncompatibleAddressException | AddressStringException ex) {
            throw runtime(ex, "invalid ip address =< %s >", value);
        }
    }
}
