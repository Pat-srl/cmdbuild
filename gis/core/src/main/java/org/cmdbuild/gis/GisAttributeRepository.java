package org.cmdbuild.gis;

import java.util.List;
import jakarta.annotation.Nullable;

public interface GisAttributeRepository {

    GisAttribute create(GisAttribute layer);

    GisAttribute get(String classId, String name);

    GisAttribute update(GisAttribute changes);

    void delete(String classId, String name);

    List<GisAttribute> getAllLayers();

    List<GisAttribute> getLayersByOwnerClass(String classId);

    List<GisAttribute> getVisibleLayersForClass(String classId);

    List<GisAttribute> getLayersByOwnerClassAndLayerName(String classId, @Nullable Iterable<String> layerNames);

    GisAttribute getLayer(long attrId);

}
