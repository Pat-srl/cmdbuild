package org.cmdbuild.gis;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.isEmpty;
import com.google.common.collect.Lists;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Multimap;
import static com.google.common.collect.Multimaps.index;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import net.postgis.jdbc.PGbox2d;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.postgres.q3.AliasBuilder;
import org.cmdbuild.dao.postgres.q3.beans.PreparedQueryExt;
import org.cmdbuild.dao.postgres.utils.RelationDirectionQueryHelper;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.sqlTableToClassName;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import static org.cmdbuild.gis.utils.GisUtils.buildGisTableNameForQuery;
import static org.cmdbuild.gis.utils.GisUtils.postgisSqlToCmGeometryOrNull;
import org.cmdbuild.navtree.NavTreeNode;
import static org.cmdbuild.utils.io.CmIoUtils.isJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("geoFeatureStore")
public class GisValueRepositoryImpl implements GisValueRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final JdbcTemplate jdbcTemplate;
    private final GisSchemaService databaseService;
    private final UserClassService userClassService;
    private final GisAttributeRepository attributeRepository;

    public GisValueRepositoryImpl(DaoService dao, GisSchemaService databaseService, GisAttributeRepository attributeRepository, UserClassService userClassService) {
        this.dao = checkNotNull(dao);
        this.jdbcTemplate = dao.getJdbcTemplate();
        this.databaseService = checkNotNull(databaseService);
        this.attributeRepository = checkNotNull(attributeRepository);
        this.userClassService = checkNotNull(userClassService);
    }

    @Override
    public void checkGisSchemaAndCreateIfMissing() {
        databaseService.checkGisSchemaAndCreateIfMissing();
    }

    @Override
    public boolean isGisSchemaOk() {
        return databaseService.isGisSchemaOk();
    }

    @Override
    public void createGisTable(GisAttribute gisAttribute) {
        jdbcTemplate.queryForObject("SELECT _cm3_gis_table_create(?::regclass, ?, ?)", Object.class, quoteSqlIdentifier(gisAttribute.getOwnerClassName()), gisAttribute.getLayerName(), getSqlGeoType(gisAttribute));
    }

    @Override
    public void deleteGisTable(String targetClassName, String geoAttributeName) {
        String geoAttributeTableName = buildGisTableNameForQuery(targetClassName, geoAttributeName);
        logger.debug("Delete GIS table = {}", geoAttributeTableName);
        jdbcTemplate.queryForObject("SELECT _cm3_class_delete(?::regclass)", Object.class, geoAttributeTableName);
    }

    @Override
    public void setGisValue(GisAttribute gisAttribute, String value, long ownerCardId) {
        jdbcTemplate.queryForObject("SELECT _cm3_gis_value_set(?::regclass,?,?,?)", Object.class, quoteSqlIdentifier(gisAttribute.getOwnerClassName()), gisAttribute.getLayerName(), ownerCardId, value);
    }

    @Override
    public void deleteGisValue(GisAttribute gisAttribute, long ownerCardId) {
        jdbcTemplate.queryForObject("SELECT _cm3_gis_value_delete(?::regclass,?,?)", Object.class, quoteSqlIdentifier(gisAttribute.getOwnerClassName()), gisAttribute.getLayerName(), ownerCardId);
    }

    @Nullable
    @Override
    public GisValue getGisValueOrNull(GisAttribute gisAttribute, long ownerCardId) {
        String strValue = jdbcTemplate.queryForObject("SELECT _cm3_gis_value_get(?::regclass,?,?)", String.class, quoteSqlIdentifier(gisAttribute.getOwnerClassName()), gisAttribute.getLayerName(), ownerCardId);
        return postgisSqlToCmGeometryOrNull(gisAttribute, ownerCardId, strValue, null);
    }

    @Override
    public List<GisValue> getGisValues(Iterable<Long> attributeIdList, String bboxArea) {
        return jdbcTemplate.query(buildGeoValueBaseQuery(attributeIdList, null, bboxArea), (rs, i) -> gisValueFromResultSet(rs));
    }

    @Override
    public GisValuesAndNavTree getGeoValuesAndNavTree(Iterable<Long> layers, String bbox, NavTreeNode navTreeDomains) {
        List<GisValue> gisValues = list();
        List<GisNavTreeNode> navTreeNodes = list();
        DomainTreeNodeAux domainTreeNodes = new DomainTreeNodeAux(navTreeDomains);
        for (long attrId : layers) {
            logger.debug("get geo values and nav tree for attr = {}", attrId);
            Pair<List<GisValue>, List<GisNavTreeNode>> geoValuesAndNavTree = new GeoValuesAndNavTreeAux(attrId, domainTreeNodes).getGeoValuesAndNavTree(bbox);
            gisValues.addAll(geoValuesAndNavTree.getLeft());
            navTreeNodes.addAll(geoValuesAndNavTree.getRight());
        }

        return new GisValuesAndNavTreeImpl(gisValues, navTreeNodes);
    }

    @Override
    @Nullable
    public Area getAreaForValues(Collection<Long> attrs, CmdbFilter filter, String forOwner) {
        checkArgument(!isNullOrEmpty(attrs), "attrs is null");
        String area = jdbcTemplate.queryForObject(format("SELECT st_extent(geometry) FROM (%s) g",
                attrs.stream().map(attributeRepository::getLayer)
                        .map(a -> {
                            PreparedQueryExt preparedQuery = (PreparedQueryExt) dao.select(ATTR_ID).from(userClassService.getUserClass(a.getOwnerClassName())).where(filter).build(); //TODO privileges filter ??
                            return format("SELECT \"Geometry\"::geometry geometry FROM %s WHERE \"Master\" IN (SELECT %s FROM (%s) _idlist )", getTableName(a), preparedQuery.getSelectForAttr(ATTR_ID).getAlias(), preparedQuery.getQuery());
                        }).collect(joining(" UNION "))), String.class);
        if (isBlank(area)) {
            return null;
        } else {
            try {
                PGbox2d postgisBox = new PGbox2d(area);
                return new AreaImpl(postgisBox.getLLB().x, postgisBox.getLLB().y, postgisBox.getURT().x, postgisBox.getURT().y);
            } catch (SQLException ex) {
                throw new GisException(ex);
            }
        }
    }

    @Override
    public List<Pair<Long, String>> getOwnerIdGeometryForValues(Collection<Long> attrs) {
        checkArgument(!isNullOrEmpty(attrs), "attrs is null");
        List<Pair<Long, String>> onwerGeometryList = list();
        jdbcTemplate.query(attrs.stream().map(attributeRepository::getLayer).map(a -> format("SELECT \"Master\" ownerId, \"Geometry\"::geometry geometry FROM %s", getTableName(a))).collect(joining(" UNION ")), (rs, i) -> onwerGeometryList.add(Pair.of(rs.getLong(1), rs.getString(2))));
        return onwerGeometryList;
    }

    @Override
    public List<GisValue> readGeoFeatures(GisAttribute gisAttribute, @Nullable String bbox) {

        String tableName = getTableName(gisAttribute);
        Classe ownerClass = dao.getClasse(gisAttribute.getOwnerClassName());

        String sql = format("SELECT _gis_table.\"Master\" AS _owner, st_astext(_gis_table.\"Geometry\"::geometry) AS _geometry FROM %s AS _gis_table"
                + " JOIN \"%s\" AS _master_table " + "ON _gis_table.\"Master\" = _master_table.\"Id\" WHERE _master_table.\"Status\" = 'A'", tableName, ownerClass.getName());

        if (isNotBlank(bbox)) {
            sql += format(" AND (_gis_table.\"Geometry\" && st_makeenvelope(%s::varchar,3857))", getEnvelopeParamsFromBboxArea(bbox));
        }

        return jdbcTemplate.query(sql, (rs, i) -> {
            String geometryAsString = rs.getString("_geometry");
            Long ownerCardId = rs.getLong("_owner");
            return checkNotNull(postgisSqlToCmGeometryOrNull(gisAttribute.getLayerName(), ownerClass.getName(), ownerCardId, geometryAsString, null));
        });
    }

    private static String getSqlGeoType(GisAttribute gisAttribute) {
        checkArgument(gisAttribute.isPostgis(), "invalid gis attribute = %s : not a postgis type", gisAttribute);
        return serializeEnum(gisAttribute.getType()).toUpperCase();
    }

    private static GisValue gisValueFromResultSet(ResultSet rs) throws SQLException {
        String geometryAsString = rs.getString("_geometry"),
                ownerClass = rs.getString("_ownerclass"),
                attrName = rs.getString("_attrname"),
                ownerDescription = rs.getString("_ownercarddesc");
        Long ownerCard = rs.getLong("_ownercard");
        return postgisSqlToCmGeometryOrNull(attrName, ownerClass, ownerCard, checkNotBlank(geometryAsString), ownerDescription);
    }

    private String buildGeoValueBaseQuery(Iterable<Long> layers, List<String> select, String bbox) {
        checkArgument(!isEmpty(layers), "geo attribute (layer) list param cannot be null");
        checkNotBlank(bbox, "area param cannot be null");
        String otherSelect;
        if (isNullOrEmpty(select)) {
            otherSelect = "";
        } else {
            otherSelect = "," + Joiner.on(",").join(select);
        }
        return format("SELECT _cm3_utils_regclass_to_name(_geometry.ownerclass) _ownerclass,_geometry.attrname _attrname,_geometry.ownercard _ownercard, _class.\"Description\" _ownercarddesc,_geometry.geometry _geometry%s FROM _cm3_gis_find_values('{%s}'::bigint[],'%s') _geometry LEFT JOIN \"Class\" _class ON _geometry.ownercard = _class.\"Id\" AND \"Status\" = 'A'", otherSelect, Joiner.on(",").join(layers), getEnvelopeParamsFromBboxArea(bbox));
    }

    private class DomainTreeNodeAux {

        private final List<NavTreeNode> domainTreeNodes;
        private final Multimap<String, NavTreeNode> domainTreeNodesByTargetClassName;
        private final Map<String, NavTreeNode> domainTreeNodesById;

        public DomainTreeNodeAux(NavTreeNode navTreeDomains) {
            this.domainTreeNodes = navTreeDomains.getThisNodeAndAllDescendants();
            domainTreeNodesByTargetClassName = index(domainTreeNodes, NavTreeNode::getTargetClassName);
            domainTreeNodesById = uniqueIndex(domainTreeNodes, NavTreeNode::getId);
        }

        public Collection<NavTreeNode> getNodesByTargetClass(String classId) {
            Classe classe = dao.getClasse(classId);
            return getNodesByTargetClass(classe);
        }

        private Collection<NavTreeNode> getNodesByTargetClass(Classe classe) {
            Collection<NavTreeNode> nodes = domainTreeNodesByTargetClassName.get(classe.getName());
            if (nodes.isEmpty() && classe.hasParent()) {
                nodes = getNodesByTargetClass(classe.getParent());
            }
            return nodes;
        }

        public NavTreeNode getNodeById(String id) {
            return checkNotNull(domainTreeNodesById.get(id), "node not found for id = %s", id);
        }
    }

    private class GeoValuesAndNavTreeAux {

        private final long attrId;
        private final DomainTreeNodeAux domainTreeNodes;

        private final AliasBuilder aliasBuilder = new AliasBuilder();
        private final List<List<NavTreeNodeQueryAliases>> aliasesForCardIdAndDescription = list();
        private final List<String> select = list();
        private final List<String> queryJoins = list();

        public GeoValuesAndNavTreeAux(long layer, DomainTreeNodeAux domainTreeNodes) {
            this.attrId = layer;
            this.domainTreeNodes = checkNotNull(domainTreeNodes);
        }

        public Pair<List<GisValue>, List<GisNavTreeNode>> getGeoValuesAndNavTree(String bbox) {
            GisAttribute attr = attributeRepository.getLayer(attrId);
            List<List<NavTreeNode>> navTreeBranches = buildReverseNavTreeBranchesForAttr(attr);

            Classe ownerClass = userClassService.getUserClass(attr.getOwnerClassName());
            checkArgument(ownerClass.hasGisAttributeReadPermission(attr.getLayerName()), format("User not allowed to access the specified GeoAttribute < %s >", attr.getLayerName()));

            String ownerClassAlias = aliasBuilder.buildAlias(ownerClass.getName());
            queryJoins.add(format(" JOIN %s %s ON %s.\"IdClass\" = _geometry.ownerclass AND %s.\"Id\" = _geometry.ownercard AND %s.\"Status\" = 'A'",
                    entryTypeToSqlExpr(ownerClass), ownerClassAlias, ownerClassAlias, ownerClassAlias, ownerClassAlias));
            NavTreeNodeQueryAliases baseNodeAlias = selectCardAttrs(ownerClass, ownerClassAlias, "-");

            for (List<NavTreeNode> navTreeBranch : navTreeBranches) {
                logger.debug("prepare query for nav tree branch = {}", navTreeBranch);

                String sourceClassIdExpr = "_geometry.ownerclass";
                String sourceCardIdExpr = "_geometry.ownercard";

                List<NavTreeNodeQueryAliases> list = list(new NavTreeNodeQueryAliases(baseNodeAlias.cardIdAlias, baseNodeAlias.cardDescAlias, baseNodeAlias.targetClass, navTreeBranch.get(0).getId(), baseNodeAlias.cardIdClassAlias));

                for (NavTreeNode node : navTreeBranch) {
                    if (isNotBlank(node.getDomainName())) {
                        logger.debug("add join for node = {}", node);
                        String domainId = node.getDomainName();
                        RelationDirection direction = node.getDirection();
                        Domain domain = dao.getDomain(domainId).getThisDomainWithDirection(direction);
                        RelationDirectionQueryHelper helper = RelationDirectionQueryHelper.forDirection(direction);
                        Classe targetClass = domain.getTargetClass();
                        String domainAlias = aliasBuilder.buildAlias(domainId);
                        logger.debug("join domain = {} with direction = {} ( {} -> {} )", domain, direction, domain.getSourceClass(), targetClass);

                        queryJoins.add(format(" LEFT JOIN %s %s ON %s.%s = %s AND %s.%s = %s AND %s.\"Status\" = 'A' ",
                                entryTypeToSqlExpr(domain), domainAlias, domainAlias, helper.getSourceCardIdExpr(), sourceCardIdExpr, domainAlias, helper.getSourceClassIdExpr(), sourceClassIdExpr, domainAlias));

                        String targetClassAlias = aliasBuilder.buildAlias(targetClass.getName());

                        String classJoinQuery = format(" LEFT JOIN %s %s ON %s.\"IdClass\" = %s.%s AND %s.\"Id\" = %s.%s AND %s.\"Status\" = 'A'",
                                entryTypeToSqlExpr(targetClass), targetClassAlias, targetClassAlias, domainAlias, helper.getTargetClassIdExpr(), targetClassAlias, domainAlias, helper.getTargetCardIdExpr(), targetClassAlias);

                        if (node.hasFilter()) {
                            logger.debug("attach filter =< {} >", node.getTargetFilter());
                            CmdbFilter filter = isJson(node.getTargetFilter()) ? parseFilter(node.getTargetFilter()) : CmdbFilterImpl.builder().withCqlFilter(node.getTargetFilter()).build();
                            PreparedQueryExt query = (PreparedQueryExt) dao.select(ATTR_ID).from(domain.getSourceClass()).where(filter).build();
                            classJoinQuery += format(" AND %s = ANY ( SELECT %s FROM ( %s ) %s )", sourceCardIdExpr, query.getSelectForAttr(ATTR_ID).getAlias(), query.getQuery(), aliasBuilder.buildAlias("subfilterquery"));
                        }

                        queryJoins.add(classJoinQuery);

                        sourceClassIdExpr = format("%s.\"IdClass\"", targetClassAlias);
                        sourceCardIdExpr = format("%s.\"Id\"", targetClassAlias);

                        list.add(selectCardAttrs(targetClass, targetClassAlias, node.getParentId()));
                    } else {
                        logger.debug("found top level node = {}", node);
                        if (node.hasFilter()) {
                            //TODO improve this, duplicated code from above
                            logger.debug("attach filter =< {} >", node.getTargetFilter());
                            CmdbFilter filter = isJson(node.getTargetFilter()) ? parseFilter(node.getTargetFilter()) : CmdbFilterImpl.builder().withCqlFilter(node.getTargetFilter()).build();
                            PreparedQueryExt query = (PreparedQueryExt) dao.select(ATTR_ID).from(node.getTargetClassName()).where(filter).build();
                            String querySourceClass = Iterables.getLast(queryJoins);//queryJoins.get(queryJoins.size() - 1);
                            querySourceClass += format(" AND %s = ANY ( SELECT %s FROM ( %s ) %s )", sourceCardIdExpr, query.getSelectForAttr(ATTR_ID).getAlias(), query.getQuery(), aliasBuilder.buildAlias("subfilterquery"));
                            queryJoins.set(queryJoins.size() - 1, querySourceClass);
                        }
                        break;
                    }
                }
                aliasesForCardIdAndDescription.add(list);
            }

            String query = buildGeoValueBaseQuery(singleton(attrId), select, bbox) + " " + Joiner.on(" ").join(queryJoins);

            List<GisNavTreeNode> navTreeNodes = list();
            Set<String> addedNodeKeys = set();
            List<GisValue> gisValues = list();

            jdbcTemplate.query(query, (ResultSet rs) -> {
                GisValue gisValue = gisValueFromResultSet(rs);
                logger.trace("process gis value = {}", gisValue);
                gisValues.add(gisValue);
                for (List<NavTreeNodeQueryAliases> list : aliasesForCardIdAndDescription) {
                    GisNavTreeNode parent = null;
                    logger.trace("process results for nav tree branch = {}", list);
                    for (NavTreeNodeQueryAliases navTreeNodeQueryAliases : Lists.reverse(list)) {
                        long cardId = rs.getLong(navTreeNodeQueryAliases.cardIdAlias);
                        if (isNotNullAndGtZero(cardId)) {
                            GisNavTreeNode thisNode = GisNavTreeNodeImpl.builder()
                                    .withCardId(cardId)
                                    .withClassId(sqlTableToClassName(rs.getString(navTreeNodeQueryAliases.cardIdClassAlias)))
                                    .withDescription(rs.getString(navTreeNodeQueryAliases.cardDescAlias))
                                    .withParentClassId(applyOrNull(parent, GisNavTreeNode::getClassId))
                                    .withParentCardId(applyOrNull(parent, GisNavTreeNode::getCardId))
                                    .withNavTreeNodeId(navTreeNodeQueryAliases.navTreeNodeId)
                                    .build();
                            if (addedNodeKeys.add(key(thisNode.getClassId(), thisNode.getCardId(), navTreeNodeQueryAliases.navTreeNodeId))) {
                                logger.trace("add nav tree node = {}", thisNode);
                                navTreeNodes.add(thisNode);
                            } else {
                                logger.trace("skip nav tree node = {} (already added)", thisNode);
                            }
                            parent = thisNode;
                        } else {
                            break;
                        }
                    }
                }
            });
            return Pair.of(gisValues, navTreeNodes);
        }

        private NavTreeNodeQueryAliases selectCardAttrs(Classe targetClass, String targetClassAlias, String navTreeNodeId) {
            String cardIdAlias = aliasBuilder.buildAlias(targetClass.getName() + "cardid");
            String cardDescAlias = aliasBuilder.buildAlias(targetClass.getName() + "carddesc");
            String cardIdClassAlias = aliasBuilder.buildAlias(targetClass.getName() + "cardidclass");
            select.add(format("%s.\"Id\" %s", targetClassAlias, cardIdAlias));
            select.add(format("%s.\"Description\" %s", targetClassAlias, cardDescAlias));
            select.add(format("%s.\"IdClass\" %s", targetClassAlias, cardIdClassAlias));
            return new NavTreeNodeQueryAliases(cardIdAlias, cardDescAlias, targetClass, navTreeNodeId, cardIdClassAlias);
        }

        private List<List<NavTreeNode>> buildReverseNavTreeBranchesForAttr(GisAttribute attr) {
            Classe classe = dao.getClasse(attr.getOwnerClassName());
            logger.debug("build reverse nav tree branch for class = {}", classe);
            Collection<NavTreeNode> nodes = domainTreeNodes.getNodesByTargetClass(classe.getName());
            if (nodes.isEmpty()) {
                logger.debug("nav tree domain not found for class = {}", classe);
                return emptyList();
            } else {
                return nodes.stream().map((node) -> {
                    logger.debug("found leaf node = {}, building reverse nav tree branch", node);
                    List<NavTreeNode> list = list(node);
                    while (node.hasParent()) {
                        node = domainTreeNodes.getNodeById(node.getParentId());
                        logger.debug("load parent node = {}", node);
                        list.add(node);
                    }
                    return list;
                }).collect(toList());
            }
        }
    }

    private String getTableName(GisAttribute gisAttribute) {
        checkArgument(gisAttribute.isPostgis(), "invalid attribute type: not a gis/postgis attr = %s", gisAttribute);
        Classe classe = dao.getClasse(gisAttribute.getOwnerClassName());
        return buildGisTableNameForQuery(classe.getName(), gisAttribute.getLayerName());
    }

    private String getEnvelopeParamsFromBboxArea(String bbox) {
        String[] coordinates = bbox.split(",");
        Preconditions.checkArgument(coordinates.length == 4);
        return Joiner.on(",").join(coordinates);
    }

    private static class NavTreeNodeQueryAliases {

        private final String cardIdAlias, cardDescAlias, navTreeNodeId, cardIdClassAlias;
        private final Classe targetClass;

        public NavTreeNodeQueryAliases(String cardIdAlias, String cardDescAlias, Classe targetClass, String navTreeNodeId, String cardIdClassAlias) {
            this.cardIdAlias = checkNotBlank(cardIdAlias);
            this.cardDescAlias = checkNotBlank(cardDescAlias);
            this.targetClass = checkNotNull(targetClass);
            this.navTreeNodeId = checkNotBlank(navTreeNodeId);
            this.cardIdClassAlias = checkNotBlank(cardIdClassAlias);
        }

        @Override
        public String toString() {
            return "NavTreeNodeQueryAliases{" + "cardIdAlias=" + cardIdAlias + ", cardDescAlias=" + cardDescAlias + ", navTreeNodeId=" + navTreeNodeId + ", targetClass=" + targetClass + '}';
        }

    }
}
