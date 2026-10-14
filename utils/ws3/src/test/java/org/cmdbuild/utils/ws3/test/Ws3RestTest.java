/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.ws3.inner.WsPart;
import org.cmdbuild.utils.ws3.inner.WsRequestHandlerImpl;
import static org.cmdbuild.utils.ws3.inner.WsRequestHandlerImpl.getPathExprs;
import org.cmdbuild.utils.ws3.inner.WsRestRequestImpl;
import static org.cmdbuild.utils.ws3.utils.WsUtils.buildWs3RestResourceUri;
import static org.cmdbuild.utils.ws3.utils.WsUtils.getBestRestResourceMatch;
import static org.cmdbuild.utils.ws3.utils.WsUtils.getResourceUriPathParts;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;

public class Ws3RestTest {

    @Test
    public void testWs3PartParse() throws Exception {
        assertEquals(list("{type:classes|processes}/{classId}/{typeName:cards|instances}/{cardId}/emails/{emailId}/attachments/", "calendar/events/{cardId}/emails/{emailId}/attachments/"),
                list(getPathExprs("{type:classes|processes}/{classId}/{typeName:cards|instances}/{cardId}/emails/{emailId}/attachments|calendar/events/{cardId}/emails/{emailId}/attachments", "")));
        assertEquals(list("{type:classes|processes}/{classId}/{typeName:cards|instances}/{cardId}/emails/{emailId}/attachments/{attachmentId}", "calendar/events/{cardId}/emails/{emailId}/attachments/{attachmentId}"),
                list(getPathExprs("{type:classes|processes}/{classId}/{typeName:cards|instances}/{cardId}/emails/{emailId}/attachments|calendar/events/{cardId}/emails/{emailId}/attachments", "{attachmentId}")));
    }

    @Test
    public void testWs3RequestHandlerImpl() throws Exception {
        List<Object> messages = list();
        WsRequestHandlerImpl handler = new WsRequestHandlerImpl(new WsResourceRepositoryTestImpl(new MyServiceBeanExt(), new MyOtherServiceBean()), () -> messages);

        String res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:get:myse/my/path"));
        assertEquals("{\"success\":true}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:get:something"));
        assertEquals("{\"success\":true}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:get:myse/my/other/path"));
        assertEquals("{\"success\":false}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:post:myse/my/path", map("myParam", "myValue"), "{\"attr\":\"myAttr\"}"));
        assertEquals("{\"success\":true,\"data\":{\"attr\":\"myAttr :)\",\"param\":\"myValue\"}}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:put:myse/my/path", emptyMap(), "[{\"attr\":\"myAttr2\"}]"));
        assertEquals("{\"success\":true,\"data\":{\"attr\":\"myAttr2 :)\"}}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:put:myse/my/path/1234"));
        assertEquals("{\"success\":true,\"data\":{\"param\":\"1234\"}}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:post:myse/my/multipart/path", emptyMap(), list(
                new WsPart("file1", newDataSource("my content", "text/plain", "file1.txt")),
                new WsPart("file2", newDataSource("{\"attr\":\"myAttr\"}", "application/json", "file2.txt"))
        ), null));
        assertEquals("{\"success\":true,\"data\":{\"param\":\"my content myAttr\"}}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl(new MyContextBean("my context"), "ws3rest:get:myse/my/other/path2", emptyMap(), emptyMap(), emptyMap(), null));
        assertEquals("{\"success\":true,\"data\":{\"param\":\"my context\"}}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:post:myse/my/multipart/222", emptyMap(), list(
                new WsPart("file1", newDataSource("my content1", "text/plain", "file1.txt")),
                new WsPart("file2", newDataSource("my content2", "text/plain", "file2.txt"))
        ), null));
        assertEquals("{\"success\":true,\"data\":{\"param\":\"my content1 my content2\"}}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:post:myse/my/multipart3/333", emptyMap(), list(
                new WsPart("file1", newDataSource("my content1a", "text/plain", "file1.txt")),
                new WsPart("file2", newDataSource("my content2b", "text/plain", "file2.txt"))
        ), null));
        assertEquals("{\"success\":true,\"data\":{\"param\":\"my content1a my content2b\"}}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:post:myse/my/multipart4", emptyMap(), list(
                new WsPart("file1", newDataSource("my content1c", "text/plain", "file1.txt")),
                new WsPart("file2", newDataSource("my content2d", "text/plain", "file2.txt")),
                new WsPart("data", newDataSource("{\"attr\":\"hello\"}", "application/javascript"))
        ), null));
        assertEquals("{\"success\":true,\"data\":{\"param\":\"my content1c my content2d ATTR = hello\"}}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl(null, "ws3rest:get:myse/my/hhh", emptyMap(), emptyMap(), map("myh", "4321"), null));
        assertEquals("{\"success\":true,\"data\":{\"attr\":\"4321\"}}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:get:myse/my/arr", map("ids", list("12", "34", "56"))));
        assertEquals("{\"success\":true,\"id\":102}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:get:myse/my/arr", map("ids", "12,34,56")));
        assertEquals("{\"success\":true,\"id\":102}", res);

        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:get:myse/my/param/map", map("myParam", "my value")));
        assertEquals("{\"success\":true,\"data\":{\"attr\":\"my value\"}}", res);

        messages.add("hello");
        res = handler.handleRequestToString(new WsRestRequestImpl("ws3rest:get:myse/my/path"));
        assertEquals("{\"success\":true,\"messages\":[\"hello\"]}", res);
    }

    @Test
    public void testGetBestRestResourceMatch() {
        Map<String, String> resources = map(
                "ws3rest:post:cards", "mino",
                "ws3rest:get:cards", "zero",
                "ws3rest:get:cards/{cardId}", "one",
                "ws3rest:get:cards/{cardId}/email", "two",
                "ws3rest:get:cards/{cardId}/email/{emailId}", "three",
                "ws3rest:get:cards/_ANY/email", "four",
                "ws3rest:get:cards/_ANY/email/{emailId}", "five",
                "ws3rest:delete:{a:something}", "seven",
                "ws3rest:get:{a:something}", "six",
                "ws3rest:delete:cards", "acht",
                "ws3rest:get:a/{b:.*}/c/d", "nine",
                "ws3rest:get:a/{z:.*}/c", "zen",
                "ws3rest:get:zet/{z:.+}", "elf"
        );

        assertEquals("one", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "/cards/123"), resources).getResource());
        assertEquals("123", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "/cards/123"), resources).getParams().get("cardId"));
        assertEquals("three", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "/cards/123/email/456"), resources).getResource());
        assertEquals("123", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "/cards/123/email/456"), resources).getParams().get("cardId"));
        assertEquals("456", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "/cards/123/email/456"), resources).getParams().get("emailId"));
        assertEquals("zero", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "/cards/"), resources).getResource());
        assertEquals("mino", getBestRestResourceMatch(buildWs3RestResourceUri("POST", "/cards/"), resources).getResource());
        assertEquals("five", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "/cards/_ANY/email/345"), resources).getResource());
        assertEquals("four", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "/cards/_ANY/email/"), resources).getResource());
        assertEquals("four", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "/cards/_ANY/email"), resources).getResource());
        assertEquals("four", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "cards/_ANY/email"), resources).getResource());
        assertEquals("six", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "something"), resources).getResource());
        assertEquals("seven", getBestRestResourceMatch(buildWs3RestResourceUri("DELETE", "something"), resources).getResource());
        assertEquals("acht", getBestRestResourceMatch(buildWs3RestResourceUri("DELETE", "/cards/"), resources).getResource());
        assertEquals("zen", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "a/b/c"), resources).getResource());
//        assertEquals("elf", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "zet/b/c"), resources).getResource()); //TODO
//        assertEquals("b/c", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "zet/b/c"), resources).getParams().get("z"));//TODO
        assertNull(getBestRestResourceMatch(buildWs3RestResourceUri("GET", "cards/_ANY/qwe"), resources));
    }

    @Test
    @Ignore("not supported yet") //TODO
    public void testPathMatch1() {
        assertEquals("b/c", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "zet/b/c"), map("ws3rest:get:zet/{z:.+}", "elf")).getParams().get("z"));
    }

    @Test
    public void testGetBestResourceMatch2() {
        Map<String, String> resources = map(
                "ws3rest:get:uploads/{fileId}/{file}", "test");
        assertEquals(4, getResourceUriPathParts("ws3rest:get:uploads/{fileId}/{file}").size());
        assertEquals("test", getBestRestResourceMatch(buildWs3RestResourceUri("GET", "/uploads/21672/asd.png"), resources).getResource());
    }

    public static class MyServiceBeanExt extends MyServiceBean {

    }

    @Path("{a:something}/")
    public static class MyOtherServiceBean {

        @GET
        @Path("")
        public Object mySampleMethod() {
            return map("success", true);
        }
    }

    @Path("myse")
    public static class MyServiceBean {

        @GET
        @Path("my/path")
        public Object mySampleMethod() {
            return map("success", true);
        }

        @GET
        @Path("my/arr")
        public Object myArrMethod(@QueryParam("ids") Set<Long> set) {
            return map("success", true, "id", set.stream().mapToLong(x -> x).sum());
        }

        @GET
        @Path("my/other/path")
        public Object myOtherSampleMethod() {
            return map("success", false);
        }

        @POST
        @Path("my/path")
        public Object myCreateMethod(@QueryParam("myParam") String param, MyJsonBean bean) {
            return map("success", true, "data", map("attr", bean.attr + " :)", "param", param));
        }

        @PUT
        @Path("my/path/{something}")
        public Object myCreateMethod2(@PathParam("something") String param) {
            return map("success", true, "data", map("param", param));
        }

        @POST
        @Path("my/multipart/path")
        public Object myMultipartMethod(@FormParam("file1") DataHandler file1, @FormParam("file2") MyJsonBean bean1) {
            return map("success", true, "data", map("param", readToString(file1) + " " + bean1.attr));
        }

        @POST
        @Path("my/multipart/222")
        public Object myMultipartMethod2(List<Attachment> attachments) {
            return map("success", true, "data", map("param", readToString(attachments.get(0).getDataHandler()) + " " + readToString(attachments.get(1).getDataHandler())));
        }

        @POST
        @Path("my/multipart3/333")
        public Object myMultipartMethod3(@Multipart List<DataHandler> data) {
            return map("success", true, "data", map("param", readToString(data.get(0)) + " " + readToString(data.get(1))));
        }

        @POST
        @Path("my/multipart4")
        public Object myMultipartMethod4(@Multipart("-data") List<DataHandler> data, @Multipart(value = "data") MyJsonBean bean1) {
            return map("success", true, "data", map("param", readToString(data.get(0)) + " " + readToString(data.get(1)) + " ATTR = " + bean1.attr));
        }

        @GET
        @Path("my/other/path2")
        public Object mySampleMethodWithContext(@Context MyContextBean context) {
            return map("success", true, "data", map("param", context.attr));
        }

        @PUT
        @Path("my/path")
        public Object myListMethod(List<MyJsonBean> beans) {
            return map("success", true, "data", map("attr", getOnlyElement(beans).attr + " :)"));
        }

        @GET
        @Path("my/hhh")
        public Object myHeaderMethod(@HeaderParam("myh") String head) {
            return map("success", true, "data", map("attr", head));
        }

        @GET
        @Path("my/param/map")
        public Object myParamMapMethod(ParamMapper mapper) {
            return map("success", true, "data", map("attr", mapper.myParam));
        }

        @GET
        @Path("my/{param:.+}")
        public Object myParamRegexpMethod(@PathParam("param") String param) {
            return map("success", true, "data", map("param", param));
        }

    }

    public static class ParamMapper {

        private final String myParam;

        public ParamMapper(@QueryParam("myParam") String myParam) {
            this.myParam = myParam;
        }

    }

    public static class MyContextBean {

        private final String attr;

        public MyContextBean(String attr) {
            this.attr = attr;
        }

    }

    public static class MyJsonBean {

        private final String attr;

        public MyJsonBean(@JsonProperty("attr") String attr) {
            this.attr = attr;
        }

    }
}
