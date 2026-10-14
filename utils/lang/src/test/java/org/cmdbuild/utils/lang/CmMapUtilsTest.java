package org.cmdbuild.utils.lang;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import java.util.Map;
import java.util.function.Function;
import static java.util.function.Function.identity;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMultimap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmMapUtils.toMultimap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmMapUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testMultimapCollector1() throws Exception {
        FluentMultimap<String, String> multimap = asList("a", "b", "c").stream().collect(toMultimap(identity(), identity()));
        assertNotNull(multimap);
        assertEquals(3, multimap.size());
        assertEquals(3, multimap.asMap().size());
        assertEquals(newArrayList("a", "b", "c"), newArrayList(multimap.keySet()));
        assertEquals(newArrayList("a", "b", "c"), newArrayList(multimap.keys()));
        assertEquals(newArrayList("a", "b", "c"), newArrayList(multimap.values()));
    }

    @Test
    public void testMultimapCollector2() throws Exception {
        FluentMultimap<String, String> multimap = asList("a", "b", "c").stream().collect(toMultimap((v) -> "1", identity()));
        assertNotNull(multimap);
        assertEquals(3, multimap.size());
        assertEquals(1, multimap.asMap().size());
        assertEquals(newArrayList("1"), newArrayList(multimap.keySet()));
        assertEquals(newArrayList("1", "1", "1"), newArrayList(multimap.keys()));
        assertEquals(newArrayList("a", "b", "c"), newArrayList(multimap.values()));
        assertEquals(newArrayList("a", "b", "c"), newArrayList(multimap.get("1")));
    }

    @Test
    public void testMapCollector1() throws Exception {
        Map<String, String> map = asList("a", "b", "c").stream().collect(toMap(identity(), (v) -> "1"));
        assertEquals(3, map.size());
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertTrue(map.containsKey("c"));
        assertEquals("1", map.get("a"));
    }

    @Test
    public void testMapCollector2() throws Exception {
        Map<String, String> map = asList("a", "b", "c").stream().collect(toMap(identity(), (v) -> null));
        assertEquals(3, map.size());
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertTrue(map.containsKey("c"));
        assertEquals(null, map.get("a"));
    }

    @Test(expected = RuntimeException.class)
    public void testMapCollector3() throws Exception {
        Map<String, String> map = asList("a", "b", "c", "b").stream().collect(toMap(identity(), (v) -> "1"));
        fail();
    }

    @Test
    public void testMapKeys1() throws Exception {
        FluentMap<String, String> map = map("a", "uno", "b", "due", "c", "tre");
        map.mapKeys((Function) map("a", "A", "b", "B")::get);
        assertEquals(map("A", "uno", "B", "due", "c", "tre"), map);
    }

    @Test
    public void testMapKeys2() throws Exception {
        FluentMap<String, String> map = map("a", "uno", "b", "due", "c", "tre");
        map.mapKeys((Function) map("a", "b")::get);
        assertEquals(map("b", "uno", "c", "tre"), map);
    }

    @Test
    public void testMapKeys3() throws Exception {
        FluentMap<String, String> map = map("a", "uno", "b", "due", "c", "tre");
        map.mapKeys((Function) map("b", "a")::get);
        assertEquals(map("a", "due", "c", "tre"), map);
    }

    @Test
    public void testMapKeys4() throws Exception {
        FluentMap<String, String> map = map("_tenant", "1", "IdTenant", "2");
        map.mapKeys((Function) map("_tenant", "IdTenant")::get);
        assertEquals(map("IdTenant", "1"), map);
    }

    @Test
    public void testMapKeys5() throws Exception {
        FluentMap<String, String> map = map("IdTenant", "2", "_tenant", "1");
        map.mapKeys((Function) map("_tenant", "IdTenant")::get);
        assertEquals(map("IdTenant", "1"), map);
    }

    @Test
    public void testMapValues() throws Exception {
        FluentMap<String, String> map = map("a", "uno", "b", "due", "c", "tre");
        map.mapValues((k, v) -> v + "_");
        assertEquals(map("a", "uno_", "b", "due_", "c", "tre_"), map);
    }

}
