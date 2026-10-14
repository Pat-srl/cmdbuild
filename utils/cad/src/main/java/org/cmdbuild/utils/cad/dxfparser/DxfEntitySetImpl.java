package org.cmdbuild.utils.cad.dxfparser;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableList;
import jakarta.annotation.Nullable;
import java.util.List;
import static org.cmdbuild.utils.cad.CadGeometryUtils.getBBoxOfMultipleEntities;
import org.cmdbuild.utils.cad.dxfparser.model.DxfEntity;
import org.cmdbuild.utils.cad.dxfparser.model.DxfEntitySet;
import org.cmdbuild.utils.cad.dxfparser.model.DxfExtendedData;
import org.cmdbuild.utils.cad.model.CadPolyline;

public class DxfEntitySetImpl implements DxfEntitySet {

    private final String layer;
    private final List<DxfEntity> dxfEntities;
    private final DxfExtendedDataImpl xdata;

    public DxfEntitySetImpl(@Nullable String layer, List<DxfEntity> dxfEntities, DxfExtendedDataImpl xdata) {
        this.layer = layer;
        this.dxfEntities = ImmutableList.copyOf(dxfEntities);
        this.xdata = new DxfExtendedDataImpl(xdata);
        checkArgument(!dxfEntities.isEmpty(), "cannot build an entity set with empty entity list");
    }

    @Nullable
    @Override
    public String getLayer() {
        return layer;
    }

    @Override
    public String getType() {
        return "ENTITYSET";
    }

    @Override
    public DxfExtendedData getXdata() {
        return xdata;
    }

    @Override
    public CadPolyline getPerimeter() {
        return getBBoxOfMultipleEntities(dxfEntities);
    }

    @Override
    public boolean isClosedPerimeter() {
        return false;
    }

    @Override
    public List<DxfEntity> getDxfEntities() {
        return dxfEntities;
    }

}
