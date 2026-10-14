/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import java.util.List;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 * @author afelice
 */
public class GeneratedData_Container {
    public List<GeneratedData_Classe> classes;
    public List<GeneratedData_Process> processes;
    public List<GeneratedData_View> views;

    /**
     * Used if adding incrementally {@link Classe}/{@link Process}/{@link View}
     * deserialized data.
     *
     * <p>
     * Used in {@link CardDataDeserializerImpl_OnFileSystem}.
     *
     * @param curClasseData
     */
    void add(GeneratedData_Container otherContainerData) {
        classes = addComponentData(classes, otherContainerData.classes);
        processes = addComponentData(processes, otherContainerData.processes);
        views = addComponentData(views, otherContainerData.views);
    }

    protected <T extends GeneratedData_Item> List<T> addComponentData(List<T> thisComponentData, List<T> otherComponentData) {
        List<T> result = thisComponentData;
        if (result == null) {
            result = list();
        }

        if (otherComponentData != null) {
            result.addAll(otherComponentData);
        }

        return result;
    }


}
