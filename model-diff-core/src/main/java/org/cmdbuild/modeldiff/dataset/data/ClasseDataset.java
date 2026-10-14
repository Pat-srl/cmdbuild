/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import java.util.List;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * Represents a dataset, for data only, for a {@link Classe} and filters to be
 * applied to obtain relevant {@link Card}.
 *
 * @author afelice
 */
public class ClasseDataset {

    private String name;

    /**
     * <code>read</code>/<code>write</code>
     */
    protected String mode;

    private List<AttributeDataset> attributes;

    private String filter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = checkNotBlank(name);
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        checkNotBlank(mode);
        checkArgument("read".equals(mode) || "write".equals(mode), "unsupported mode %s", mode);
        this.mode = mode;
    }

    public boolean writable() {
        return "write".equals(mode);
    }

    public List<AttributeDataset> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeDataset> attributes) {
        if (attributes == null) {
            this.attributes = emptyList();
        } else {
            this.attributes = attributes;
        }
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        // Check is a attribute filter
        if (!isBlank(filter)) {
            parseFilter(filter);
        }

        this.filter = filter;
    }

}
