/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.stub;

import java.util.List;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 * @author afelice
 */
public class KnownModelRoot {

    private final String name;
    private String description = "<NO_DESCR>";

    private List<FirstLevelComponentModel> firstLevelComponents = list();

    public KnownModelRoot(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void addComponent(FirstLevelComponentModel component) {
        firstLevelComponents.add(component);
    }

    @Override
    public String toString() {
        return "KnownModelRoot{name =< %s >= ([%d] model components)}".formatted(name, firstLevelComponents.size());
    }


}
