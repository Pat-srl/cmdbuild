package org.cmdbuild.classe.access;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.cmdbuild.dao.core.q3.BasicWhereMethods;
import org.cmdbuild.dao.core.q3.SuperclassQueryService;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface SuperclassUserQueryHelper {

    Map<String, UserCardAccess> getSubclassesUserCardAccess();

    Consumer<SuperclassQueryService.SuperclassQueryBuilderHelper> addSubclassesFiltersAndMarks(Function<Classe, Consumer<BasicWhereMethods>> where);

    default UserCardAccess getSubclassesUserCardAccess(String classId) {
        return checkNotNull(getSubclassesUserCardAccess().get(checkNotBlank(classId)), "subclass card access not found for classId =< {} >", classId);
    }

    default Consumer<SuperclassQueryService.SuperclassQueryBuilderHelper> addSubclassesFiltersAndMarks() {
        return addSubclassesFiltersAndMarks(c -> b -> {
        });
    }

}
