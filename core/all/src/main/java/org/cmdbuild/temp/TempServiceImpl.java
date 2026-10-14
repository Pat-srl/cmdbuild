/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.temp.TempServiceUtils.tempRecordId;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.BigByteArrayInputStream;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.tempFilesTtlCleanup;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_ALL_NODES;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TempServiceImpl implements TempService {

    private final static int MAX_TEMP_PART_SIZE = 10 * 1024 * 1024; //10 MiB

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;

    public TempServiceImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @ScheduledJob(value = "0 */10 * * * ?", clusterMode = RUN_ON_SINGLE_NODE, persistRun = false) //run every 10 minutes
    public void removeExpiredRecords() {
        dao.getJdbcTemplate().update("DELETE FROM \"_Temp\" WHERE now() > \"BeginDate\" + format('%s seconds', \"TimeToLive\")::interval");
    }

    @ScheduledJob(value = "0 */10 * * * ?", clusterMode = RUN_ON_ALL_NODES, persistRun = false) //run every 10 minutes
    public void removeTempFiles() {
        tempFilesTtlCleanup();
    }

    @Override
    public TempServiceHelper helper() {
        return new TempServiceHelperImpl();
    }

    @Override
    public DataHandler getTempData(String tempId) {
        BigByteArray data = getTempDataBigBytes(tempId);
        TempInfo info = getTempInfo(tempId);
        return newDataHandler(data, info.getContentType(), info.getFileName());
    }

    @Override
    public BigByteArray getTempDataBigBytes(String tempId) {
        logger.debug("get temp record =< {} >", tempId);
        TempData card = dao.getByCode(TempData.class, tempId);
        BigByteArray data = new BigByteArray();
        if (card.isComposite()) {
            CompositionInfo compositionInfo = card.getCompositionInfo();
            for (int i = 0; i < compositionInfo.getParts().size(); i++) {
                logger.debug("load temp part {} / {} ( {} {}% )", i + 1, byteCountToDisplaySize(data.length()), compositionInfo.getParts().size(), i * 100 / compositionInfo.getParts().size());
                TempData part = dao.getById(TempData.class, compositionInfo.getParts().get(i));
                data.append(part.getData());
            }
            checkArgument(Objects.equals(compositionInfo.getSize(), data.length()), "invalid temp data size: expected = %s, observed = %s", compositionInfo.getSize(), data.length());
            checkArgument(Objects.equals(compositionInfo.getHash(), hash(data)), "invalid temp data hash");
        } else {
            data.append(card.getData());
        }
        logger.debug("loaded temp record =< {} > ( {} )", tempId, byteCountToDisplaySize(data.length()));
        return data;
    }

    @Override
    public TempInfo getTempInfo(String tempId) {
        return dao.getByCode(TempData.class, tempId).getInfo();
    }

    @Override
    public void deleteTempData(String tempId) {
        dao.getJdbcTemplate().execute(format("DELETE FROM \"_Temp\" WHERE \"Code\" = %s", systemToSqlExpr(checkNotBlank(tempId))));
    }

    private class TempServiceHelperImpl implements TempServiceHelper {

        private final TempInfoImpl.TempFileInfoImplBuilder info = TempInfoImpl.builder();
        private BigByteArray data;

        @Override
        public TempServiceHelper withData(DataSource data) {
            info.withContentType(getContentType(data)).withFileName(data.getName());
            return this.withData(toBigByteArray(data));
        }

        @Override
        public TempServiceHelper withData(BigByteArray data) {
            this.data = data;
            return this;
        }

        @Override
        public TempServiceHelper withSource(TempInfoSource source) {
            info.withSource(source);
            return this;
        }

        @Override
        public TempServiceHelper withTimeToLive(Long timeToLiveInSeconds) {
            info.withTimeToLive(timeToLiveInSeconds);
            return this;
        }

        @Override
        public String put() {
            return doPutTempData(data, info.build());
        }

    }

    private String doPutTempData(BigByteArray data, TempInfo info) {
        logger.debug("put temp data ( {} {} )", byteCountToDisplaySize(data.length()), info.getContentType());
        info = TempInfoImpl.copyOf(info).withSize(data.length()).build();
        String tempId = tempRecordId();
        if (data.length() < MAX_TEMP_PART_SIZE) {
            dao.createOnly(TempDataImpl.builder().withTempId(tempId).withData(data.toByteArray()).withInfo(info).build());
        } else {
            logger.debug("data is big, split and load multiple parts");
            try {
                InputStream in = new BigByteArrayInputStream(data);
                byte[] buffer = new byte[MAX_TEMP_PART_SIZE];
                int len;
                List<Long> parts = list();
                while ((len = in.read(buffer)) > 0) {
                    byte[] part;
                    if (len < buffer.length) {
                        part = Arrays.copyOf(buffer, len);
                    } else {
                        part = buffer;
                    }
                    logger.debug("load temp part {} ( {}% )", parts.size() + 1, 100l * parts.size() * MAX_TEMP_PART_SIZE / data.length());
                    parts.add(dao.createOnly(TempDataImpl.builder().withTempId(tempRecordId()).withData(part).withTimeToLive(info.getTimeToLive()).build()));
                }
                logger.debug("aggregate temp parts");
                dao.create(TempDataImpl.builder().withTempId(tempId).withCompositionInfo(new CompositionInfoImpl(parts, hash(data), data.length())).withInfo(info).build()).getId();
            } catch (IOException ex) {
                throw runtime(ex);
            }
        }
        logger.debug("stored temp record =< {} > ( {} )", tempId, byteCountToDisplaySize(data.length()));
        return tempId;
    }

}
