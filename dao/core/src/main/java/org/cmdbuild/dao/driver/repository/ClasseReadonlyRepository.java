/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import org.cmdbuild.dao.entrytype.Classe;

public interface ClasseReadonlyRepository {

    List<Classe> getAllClasses();

    @Nullable
    Classe getClasseOrNull(long oid);

    @Nullable
    Classe getClasseOrNull(String name);

    default Classe getClasse(Classe classe) {
        return getClasse(classe.getName());
    }

    default Classe getClasse(long oid) {
        return checkNotNull(getClasseOrNull(oid), "classe not found for oid = %s", oid);
    }

    default Classe getClasse(String name) {
        return checkNotNull(getClasseOrNull(name), "classe not found for name =< %s >", name);
    }

    default Classe getRootClass() {
        return getClasse(BASE_CLASS_NAME);
    }
}
