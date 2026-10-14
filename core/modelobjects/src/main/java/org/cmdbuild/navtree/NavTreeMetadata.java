/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.navtree;

import jakarta.annotation.Nullable;
import java.util.Map;

/**
 *
 * @author afelice
 */
public interface NavTreeMetadata {

    static final String INITIAL_EXPANSION_DEPTH = "cm_initialExpansionDepth";

    @Nullable
    Integer getInitialExpansionDepth();

    Map<String, String> asMap();

}
