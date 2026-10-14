package org.cmdbuild.gis;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.onlyElement;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GisAttributeRepositoryImpl implements GisAttributeRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final Holder<List<GisAttribute>> allAttributes;

    public GisAttributeRepositoryImpl(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        allAttributes = cacheService.newHolder("all_gis_attributes", CacheConfig.SYSTEM_OBJECTS);
    }

    private void invalidateCache() {
        allAttributes.invalidate();
    }

    @Override
    public GisAttribute get(String classId, String name) {
        checkNotNull(classId);
        checkNotBlank(name);
        try {
            return getAllLayers().stream().filter((a) -> equal(a.getOwnerClassName(), classId) && equal(a.getLayerName(), name)).collect(onlyElement());
        } catch (Exception ex) {
            throw runtime(ex, "gis attr not found for classe = %s and layer name = %s", classId, name);
        }
    }

    @Override
    public GisAttribute getLayer(long attrId) {
        try {
            return getAllLayers().stream().filter((l) -> l.getId() == attrId).collect(onlyElement());
        } catch (Exception ex) {
            throw runtime(ex, "gis attr not found for id = %s", attrId);
        }
    }

    @Override
    public List<GisAttribute> getLayersByOwnerClass(String classId) {
        checkNotNull(classId);
        return getAllLayers().stream().filter((a) -> equal(a.getOwnerClassName(), classId)).collect(toList());
    }

    @Override
    public List<GisAttribute> getVisibleLayersForClass(String classId) {
        checkNotNull(classId);
        return getAllLayers().stream().filter((a) -> a.isVisible(classId)).collect(toList());
    }

    @Override
    public List<GisAttribute> getLayersByOwnerClassAndLayerName(String classId, @Nullable Iterable<String> layerNames) {
        checkNotNull(classId);
        Stream<GisAttribute> stream = getAllLayers().stream().filter((a) -> equal(a.getOwnerClassName(), classId));
        if (layerNames != null) {
            Set<String> set = set(checkNotEmpty(layerNames));
            stream = stream.filter((a) -> set.contains(a.getLayerName()));
        }
        return stream.collect(toList());
    }

    @Override
    public GisAttribute create(GisAttribute attr) {
        logger.info("create layer = {}", attr);
        if (isNullOrLtEqZero(attr.getIndex())) {
            attr = GisAttributeImpl.copyOf(attr).withIndex(getAllLayers().stream().mapToInt(GisAttribute::getIndex).max().orElse(-1) + 1).build();
        }
        attr = dao.create(attr);
        invalidateCache();
        return attr;
    }

    @Override
    public GisAttribute update(GisAttribute attr) {
        logger.info("update layer = {}", attr);
        attr = dao.update(attr);
        invalidateCache();
        return attr;
    }

    @Override
    public void delete(String classId, String name) {
        logger.info("deleteLayer layer = {} {}", classId, name);
        GisAttribute attr = get(classId, name);
        dao.delete(attr);
        invalidateCache();
    }

    @Override
    public List<GisAttribute> getAllLayers() {
        return allAttributes.get(this::doGetAllLayers);
    }

    private List<GisAttribute> doGetAllLayers() {
        return dao.selectAll().from(GisAttributeImpl.class).orderBy("Index").asList();
    }

}
