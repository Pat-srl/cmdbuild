package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import static java.lang.Long.MAX_VALUE;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.data.filter.SorterElement;
import org.cmdbuild.data.filter.SorterElementDirection;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.workflow.model.Process;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ACTIVE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.WorkflowService;

@Path("processes/")
@Produces(APPLICATION_JSON)
public class ProcessWsV2 {

    private final WorkflowService workflowService;
    private final ObjectTranslationService translationService;
    private final LookupService lookupService;

    private final static Map<SorterElementDirection, String> mappingDirection = ImmutableMap.of(ASC, "ascending", DESC, "descending");

    public ProcessWsV2(WorkflowService workflowService, ObjectTranslationService translationService, LookupService lookupService) {
        this.workflowService = checkNotNull(workflowService);
        this.translationService = checkNotNull(translationService);
        this.lookupService = checkNotNull(lookupService);
    }

    @GET
    @Path(EMPTY)
    public Object readAll(@QueryParam(ACTIVE) boolean activeOnly, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        Collection<Process> all = workflowService.getAllProcessClasses();
        Map metaRef = map();
        all.forEach((p) -> {
            p.getAllAttributes().stream().filter((attribute) -> (p instanceof IdAndDescription)).map((attribute) -> (IdAndDescription) attribute).forEachOrdered((idAndDesc) -> {
                metaRef.put(idAndDesc.getId(), map("description", idAndDesc.getDescription()));
            });
        });
        return map("data", all.stream().map(this::processGeneralDataMapConsumer).collect(toList()), "meta", map("total", all.size(), "references", metaRef));
    }

    @GET
    @Path("{processId}/")
    public Object readOne(@PathParam("processId") String processId) {
        Process classe = workflowService.getProcess(processId);
        Map metaRef = map();
        classe.getAllAttributes().stream().filter((attribute) -> (attribute instanceof IdAndDescription)).map((attribute) -> (IdAndDescription) attribute).forEachOrdered((idAndDesc) -> {
            metaRef.put(idAndDesc.getId(), map("description", idAndDesc.getDescription()));
        });
        return map("data", processSpecificDataMapConsumer(classe), "meta", map("total", null, "references", metaRef));
    }

    @GET
    @Path("{processId}/generate_id")
    public Object generateId(@PathParam("processId") String processId) {
        return map("success", true, "data", -nextLong(1, MAX_VALUE), "meta", map());
    }

    private CmMapUtils.FluentMap<String, Object> processSpecificDataMapConsumer(Process p) {
        return map(
                "statuses", lookupService.getAllLookup("FlowStatus").elements().stream().map(l -> l.getId()).collect(toList()),
                "defaultStatus", lookupService.getAllLookup("FlowStatus").elements().stream().map(l -> l.getId()).collect(toList()).get(0), //TODO do better
                "defaultOrder", serializeDefaultOrder(p.getDefaultOrder().getElements()),
                "description_attribute_name", "Description",
                "name", p.getName(),
                "description", translationService.translateClassDescription(p),
                "parent", p.getParent(),
                "prototype", p.isSuperclass(),
                "_id", p.getName()
        );
    }

    private List<FluentMap<String, String>> serializeDefaultOrder(List<SorterElement> listEl) {
        List<FluentMap<String, String>> defOrderList = list();
        listEl.forEach(e -> defOrderList.add(map(
                "attribute", e.getProperty(),
                "direction", mappingDirection.get(e.getDirection()))));
        return defOrderList;
    }

    private CmMapUtils.FluentMap<String, Object> processGeneralDataMapConsumer(Process p) {
        return map(
                "name", p.getName(),
                "description", translationService.translateClassDescription(p),
                "parent", p.getParent(),
                "prototype", p.isSuperclass(),
                "_id", p.getName()
        );
    }

}
