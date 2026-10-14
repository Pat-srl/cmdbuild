/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static java.util.Collections.emptyMap;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMaps;
import static org.cmdbuild.utils.lang.CmInlineUtils.recursiveUnflattenMaps;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenListOfMaps;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMap;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CmInlineUtilsTest {

    @Test
    public void testUnflattenMap() {
        assertEquals(map("a", "one", "b", "two"), unflattenMap(map("key_a", "one", "key_b", "two", "other", "wathever"), "key"));
        assertEquals(map("a", "one", "b", null), unflattenMap(map("key_a", "one", "key_b", null, "other", "wathever"), "key"));
        assertEquals(map("c", "asd"), unflattenMap(map("key_a", "one", "key_b", null, "other", "wathever", "keyey_c", "asd"), "keyey"));
        assertEquals(emptyMap(), unflattenMap(map("key_a", "one", "key_b", null, "other", "wathever"), "other"));
        assertEquals(emptyMap(), unflattenMap(map("key_a", "one", "key_b", null, "other", "wathever"), "else"));
    }

    @Test
    public void testUnflattenList() {
        assertEquals(list(map("c", "one"), map("c", "two")), unflattenListOfMaps(map("key_0_c", "one", "key_1_c", "two", "other", "wathever", "keyey_c", "asd"), "key"));
        assertEquals(list(map("c", "one")), unflattenListOfMaps(map("key_0_c", "one", "key_2_c", "two", "other", "wathever", "keyey_c", "asd"), "key"));
        assertEquals(list(map("c", null)), unflattenListOfMaps(map("key_0_c", null, "key_2_c", "two", "other", "wathever", "keyey_c", "asd"), "key"));
        assertEquals(list(map(), map("c", "two")), unflattenListOfMaps(map("key_0", null, "key_1_c", "two", "other", "wathever", "keyey_c", "asd"), "key"));
        assertEquals(list(map()), unflattenListOfMaps(map("key_0", null, "key1_c", "two", "other", "wathever", "keyey_c", "asd"), "key"));
        assertEquals(list(), unflattenListOfMaps(map("key__0_c", "one", "key_2_c", "two", "other", "wathever", "keyey_c", "asd"), "key"));
    }

    @Test
    public void testUnflattenMaps() {
        assertEquals(map("key", map("a", "one", "b", "two"), "other", "wathever"), unflattenMaps(map("key___a", "one", "key___b", "two", "other", "wathever")));
        assertEquals(map("key___a", "one", "key___b", "two", "other", "wathever"), flattenMaps(map("key", map("a", "one", "b", "two"), "other", "wathever")));

        assertEquals(map("a", "one", "b", "two"), unflattenMap(map("key___a", "one", "key___b", "two", "other", "wathever"), "key"));

        Map<String, Object> map = map("email_template", "myTemplate", "email_account", "myAccount", "email_template_context", map("a", 1, "b", "{dbtmpl:something}"));
        assertEquals(map, unflattenMaps(flattenMaps(map)));
    }

    @Test
    public void testMultiUnflattenListMaps() {
        Map<String, Object> map = map(
                "key", "value",
                "list", list(map("a", list("b")))
        );
        assertEquals("{key=value, list___0___a___0=b}", flattenMaps(map).toString());
    }

    @Test
    public void testRecursiveUnflattenMaps() {
        Map<String, String> map = map(
                "nomecomponente___chiave1", "valore1",
                "nomecomponente___chiave2", "valore2",
                "nomecomponente___componente1___chiave3", "valore3",
                "nomecomponente___componente1___chiave4", "valore4",
                "nomecomponente___componente1___componente2___chiave5", "valore5",
                "nomecomponente___componente1___componente2___chiave6", "valore6"
        );
        assertEquals(map(
                "nomecomponente", map(
                        "chiave1", "valore1",
                        "chiave2", "valore2",
                        "componente1", map(
                                "chiave3", "valore3",
                                "chiave4", "valore4",
                                "componente2", map(
                                        "chiave5", "valore5",
                                        "chiave6", "valore6"
                                )
                        )
                )
        ), recursiveUnflattenMaps(map));
    }
}
