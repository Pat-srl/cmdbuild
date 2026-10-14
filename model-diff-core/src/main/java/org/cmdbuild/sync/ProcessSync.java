/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import jakarta.activation.DataHandler;

/**
 *
 * @author afelice
 */
public interface ProcessSync extends ProcessLoader {

    /**
     *
     * @param processClasseName
     * @param xpdlContent
     * @param bReplace
     * @return the <code>planId</code>
     */
    String addXpld(String processClasseName, DataHandler xpdlContent, boolean bReplace);

}
