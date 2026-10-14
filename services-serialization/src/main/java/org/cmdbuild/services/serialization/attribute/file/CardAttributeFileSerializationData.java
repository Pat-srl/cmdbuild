/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization.attribute.file;

import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.services.serialization.SerializationPrefixMode;
import org.cmdbuild.services.serialization.widget.WidgetSerializationData;

/**
 * Data fetched by {@link CardAttributeFile_Fetcher} and serialized by
 * {@link CardAttributeFile_BasicSerializer} (and related).
 *
 * <b>Warning</b>: contained <code>document.getMetedata()</code> may be null if
 * <code>wsQueryOptions.isDetailed()</code> not set when invoking serialization
 * through a WS.
 *
 * @author afelice
 */
public class CardAttributeFileSerializationData {

    /**
     * Don't prefix serialization with <code>_&lt;attributeName&gt;_</code>
     */
    public static final String ANONYMOUS_SERIALIZATION_MODE = "<ANONYMOUS_SERIALIZATION_MODE>";

    public Map<String, Object> cardData;

    public final String attributeName;
    public final String classId;
    public DocumentInfoAndDetail document;
    public String documentAuthorDescription;
    public boolean isDmsServiceOk;
    public boolean canDmsCategoryWritePermission;
    public List<WidgetSerializationData> widgets = emptyList();

    @Nullable
    public Classe userClass;

    @Nullable
    public org.cmdbuild.lookup.LookupValue category;
    @Nullable
    public String categoryDescriptionTranslation;
    public boolean categoryCanUpdate;
    public boolean categoryCanDelete;

    private SerializationPrefixMode prefixMode = SerializationPrefixMode.SPM_PACKED; // As in CardWsSerializationHelperv3

    @Nullable
    Card cardWithPermissions;

    public CardAttributeFileSerializationData(String attributeName, String classId) {
        this.attributeName = attributeName;
        this.classId = classId;

        if (ANONYMOUS_SERIALIZATION_MODE.equals(attributeName)) {
            prefixMode = SerializationPrefixMode.SPM_ANONYMOUS_SERIALIZATION;
        }
    }

    public void setPrefixMode(SerializationPrefixMode prefixMode) {
        this.prefixMode = prefixMode;
    }

    public SerializationPrefixMode getPrefixMode() {
        return prefixMode;
    }
}
