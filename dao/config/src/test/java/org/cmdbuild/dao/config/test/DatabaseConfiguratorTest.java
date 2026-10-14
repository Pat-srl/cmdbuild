package org.cmdbuild.dao.config.test;

import static com.google.common.base.Objects.equal;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.function.Function.identity;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.cmdbuild.utils.postgres.PostgresUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfiguratorTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testUrlParsing() throws Exception {
        DatabaseCreator databaseConfigurator = new DatabaseCreator(DatabaseCreatorConfigImpl.builder().withDatabaseUrl("jdbc:postgresql://localhost:5432/cmdbuild_test").build());
        assertEquals("localhost", databaseConfigurator.getConfig().getHost());
        assertEquals(5432, databaseConfigurator.getConfig().getPort());
        assertEquals("cmdbuild_test", databaseConfigurator.getConfig().getDatabaseName());
    }

    @Test
    @Ignore //TODO
    public void testReadTableFromDump() throws Exception {
        List<Map<String, String>> records = PostgresUtils.newHelper("localhost", 5436, "postgres", "postgres").withDatabase("cmdbuild")
                .buildHelper().readTableFromDump(new File("/tmp/file4.dump"), "_SystemConfig");
        Set<String> extensions = set(toListOfStrings(list(records).filter(r -> equal(r.get("Status"), "A")).collect(toMap(r -> r.get("Code"), identity())).mapValues(r -> r.get("Value")).get("org.cmdbuild.database.ext")));
        logger.info("ext =< {} >", extensions);
    }
}
