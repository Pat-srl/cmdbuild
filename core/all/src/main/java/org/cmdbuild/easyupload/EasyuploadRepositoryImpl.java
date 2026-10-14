/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

import static com.google.common.base.Objects.equal;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.cache.CacheService;
import org.springframework.stereotype.Component;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import static org.cmdbuild.easyupload.EasyuploadItemInfo.EASYUPLOAD_ITEM_INFO_ATTRS;
import static org.cmdbuild.easyupload.EasyuploadUtils.normalizePath;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.dao.core.q3.WhereOperator.MATCHES_REGEXP;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.easyupload.EasyuploadItemInfo.EASYUPLOAD_PATH;

@Component
public class EasyuploadRepositoryImpl implements EasyuploadRepository {

    private final DaoService dao;
    private final CmCache<Optional<EasyuploadItem>> itemsByPath;
    private final CmCache<EasyuploadItem> itemsById;

    public EasyuploadRepositoryImpl(DaoService dao, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        itemsById = cacheService.newCache("upload_item_by_id");
        itemsByPath = cacheService.newCache("upload_item_by_path");
    }

    private void invalidateCache() {
        itemsById.invalidateAll();
        itemsByPath.invalidateAll();
    }

    @Override
    public EasyuploadItem create(EasyuploadItem item) {
        item = dao.create(EasyuploadItemImpl.toImpl(item));
        invalidateCache();
        return item;
    }

    @Override
    public EasyuploadItem update(EasyuploadItem item) {
        item = dao.update(EasyuploadItemImpl.toImpl(item));//TODO check path ??
        invalidateCache();
        return item;
    }

    @Override
    public void delete(long fileId) {
        dao.delete(EasyuploadItemImpl.class, fileId);
        invalidateCache();
    }

    @Override
    @Nullable
    public EasyuploadItem getByPathOrNull(String path) {
        checkNotBlank(path);
        return itemsByPath.get(path, () -> Optional.fromNullable(doGetByPathOrNull(path))).orNull();
    }

    @Override
    public EasyuploadItem getItemById(long fileId) {
        return itemsById.get(String.valueOf(fileId), () -> doGetItemById(fileId));
    }

    @Override
    public List<EasyuploadItem> getAll() {
        return dao.selectAll().from(EasyuploadItemImpl.class).orderBy(EASYUPLOAD_PATH, ASC).asList();
    }

    @Override
    public EasyuploadItem getByPath(String path) {
        return checkNotNull(getByPathOrNull(path), "upload not found for path = %s", path);
    }

    @Override
    public List<EasyuploadItemInfo> getAllInfo() {
        return (List) getAll();
    }

    @Override
    public List<EasyuploadItemInfo> getInfoByDir(String dir) {
        dir = normalizePath(dir);
        String regexp = equal(dir, "/") ? "^[^/]+$" : format("^%s/[^/]+$", dir);
        return dao.select(EASYUPLOAD_ITEM_INFO_ATTRS).from(EasyuploadItemInfoImpl.class)
                .where(EASYUPLOAD_PATH, MATCHES_REGEXP, regexp)
                .orderBy(EASYUPLOAD_PATH, ASC).asList();
    }

    @Override
    public List<EasyuploadItem> getAllByDir(String dir) {
        dir = normalizePath(dir);
        String regexp = equal(dir, "/") ? "^[^/]+$" : format("^%s/[^/]+$", dir);
        return dao.selectAll().from(EasyuploadItemImpl.class)
                .where(EASYUPLOAD_PATH, MATCHES_REGEXP, regexp)
                .orderBy(EASYUPLOAD_PATH, ASC).asList();
    }

    private EasyuploadItem doGetByPathOrNull(String path) {
        return dao.selectAll().from(EasyuploadItemImpl.class).where(EASYUPLOAD_PATH, EQ, normalizePath(path)).getOneOrNull();
    }

    private EasyuploadItem doGetItemById(long fileId) {
        return dao.selectAll().from(EasyuploadItemImpl.class).where(ATTR_ID, EQ, fileId).getOne();
    }

    @Override
    public List<String> getSubdirsForDir(String path) {
        path = normalizePath(path);
        String regexp = equal(path, "/") ? "^([^/]+)/.*" : format("^%s/([^/]+)/.*", path);
        return dao.getJdbcTemplate().queryForList(format("WITH q AS (SELECT DISTINCT regexp_replace(\"Path\",%s,'\\1') p FROM \"_Upload\" WHERE \"Status\" = 'A' AND \"Path\" ~ %s) SELECT p FROM q ORDER BY p", systemToSqlExpr(regexp), systemToSqlExpr(regexp)), String.class);//TODO check pattern quote
    }
}
