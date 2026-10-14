/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.console;

import org.jline.reader.Candidate;

/**
 *
 * @author ataboga
 */
public class CmCandidate extends Candidate {

    public CmCandidate(String value) {
        super(value, value, null, null, null, null, false);
    }
}
