/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.beans.CardIdAndClassName;

public interface BimProjectExt extends BimProject {

    @Nullable
    CardIdAndClassName getOwnerOrNull();

    default CardIdAndClassName getOwner() {
        return checkNotNull(getOwnerOrNull(), "this project = %s does not have a card mapping (bim object record)", this);
    }

    default boolean hasOwner() {
        return getOwnerOrNull() != null;
    }

    @Nullable
    default Long getOwnerIdOrNull() {
        return hasOwner() ? getOwner().getId() : null;
    }

}
