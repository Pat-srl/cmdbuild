package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("classes/")
@Produces(APPLICATION_JSON)
public class ClassesWsV2 {

    private final UserClassService classService;
    private final ObjectTranslationService translationService;

    public ClassesWsV2(UserClassService classService, ObjectTranslationService translationService) {
        this.classService = checkNotNull(classService);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path(EMPTY)
    public Object readMany() {
        List<Classe> all = classService.getAllUserClasses();
        return map("data", all.stream().map(this::serializeResponse).collect(toList()), "meta", map("total", all.size()));
    }

    @GET
    @Path("{classId}/")
    public Object readOne(@PathParam("classId") String classId) {
        Classe classe = classService.getUserClass(classId);
        return map("data", serializeResponse(classe), "meta", map());
    }

    private CmMapUtils.FluentMap<String, Object> serializeResponse(Classe input) {
        return CmMapUtils.<String, Object, Object>map(
                "defaultOrder", input.getDefaultOrder().getElements(),
                "description_attribute_name", "Description",
                "name", input.getName(),
                "description", translationService.translateClassDescription(input),
                "prototype", input.isSuperclass(),
                "parent", input.getParentOrNull(),
                "_id", input.getName(),
                "defaultFilter", input.getMetadata().getDefaultFilterOrNull()
        );
    }
}
