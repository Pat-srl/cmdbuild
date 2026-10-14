/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

/**
 *
 * @author afelice
 */
public class Md5Helper {
    
    private static final Md5Helper helper = new Md5Helper(); 
    private static MessageDigest md5;
    
    private Md5Helper() {}
    
    /**
     * Raises <code>RuntimeException</code> if missing MD5.
     * 
     * @return 
     */
    public static synchronized Md5Helper getInstance() {
        if (md5 == null) {
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException ex) {
                throw runtime("error initializing byte en/decoders for contextMenuItem JS components", ex);
            }
        }
        
        return helper;
    }
    
    public byte[] md5(byte[] inputBytes) {
        return md5.digest(inputBytes);
    }

    public String toMd5Str(byte[] inputBytes) {
        return java.util.HexFormat.of().formatHex(md5(inputBytes));
    }
}
