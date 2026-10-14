/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.corecomponents;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.corecomponents.CoreComponentType.CCT_SCRIPT;

public interface CoreComponent {

    boolean isActive();

    String getCode();

    String getDescription();

    CoreComponentType getType();

    String getData();

    default boolean isScript() {
        return equal(CCT_SCRIPT, getType());
    }
}
