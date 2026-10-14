/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * Represents a dataset, for data only , for:
 * <ol>
 * <li>list of {@link Classe};
 * <li>related list of {@link Attribute};
 * <li>filters to be applied to obtain relevant {@link Card}.
 * </ol>
 *
 * @author afelice
 */
public class DataDataset implements Cloneable {

    private String id;

    private String name;

    private String description;

    @JsonProperty("_description_translation")
    public String descriptionTranslation;

    @JsonProperty("_description_plural_translation")
    public String descriptionPluralTranslation;

    public boolean active;

    public List<ClasseDataset> classes = list();

    public List<ProcessDataset> processes = list();

    public List<ViewDataset> views = list();

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = checkNotBlank(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = checkNotBlank(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = checkNotBlank(description);
        if (isBlank(descriptionTranslation)) {
            this.descriptionTranslation = this.description;
        }
        if (isBlank(descriptionPluralTranslation)) {
            this.descriptionPluralTranslation = this.description;
        }
    }

    @JsonIgnore
    public void reduceToWritableOnly() {
        classes = list(classes).filter(ClasseDataset::writable);
        processes = list(processes).filter(ProcessDataset::writable);
        views = list(views).filter(ViewDataset::writable);
    }

    @Override
    @JsonIgnore
    public DataDataset clone() {
        try {
            return (DataDataset) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw runtime(ex);
        }
    }
}
