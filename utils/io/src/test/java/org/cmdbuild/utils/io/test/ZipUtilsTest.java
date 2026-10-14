/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import static org.cmdbuild.utils.io.CmZipUtils.unzipDataAsMap;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ZipUtilsTest {

    private final static byte[] ZIP_DATA_1 = Base64.decodeBase64("UEsDBAoAAgAAAANYbE1ofTsVBQAAAAUAAAAJABwAZmlsZTEudHh0VVQJAAMlT+lbR0/pW3V4CwABBOgDAAAE6AMAAGNpYW8KUEsDBAoAAAAAAAhYbE0AAAAAAAAAAAAAAAAEABwAZGlyL1VUCQADL0/pW0dP6Vt1eAsAAQToAwAABOgDAABQSwMECgACAAAACFhsTX9oNGYGAAAABgAAAA0AHABkaXIvZmlsZTIudHh0VVQJAAMvT+lbL0/pW3V4CwABBOgDAAAE6AMAAGNpYW8yClBLAwQKAAAAAAAKWGxNAAAAAAAAAAAAAAAABQAcAGRpcjIvVVQJAAM0T+lbR0/pW3V4CwABBOgDAAAE6AMAAFBLAQIeAwoAAgAAAANYbE1ofTsVBQAAAAUAAAAJABgAAAAAAAEAAAC0gQAAAABmaWxlMS50eHRVVAUAAyVP6Vt1eAsAAQToAwAABOgDAABQSwECHgMKAAAAAAAIWGxNAAAAAAAAAAAAAAAABAAYAAAAAAAAABAA/UFIAAAAZGlyL1VUBQADL0/pW3V4CwABBOgDAAAE6AMAAFBLAQIeAwoAAgAAAAhYbE1/aDRmBgAAAAYAAAANABgAAAAAAAEAAAC0gYYAAABkaXIvZmlsZTIudHh0VVQFAAMvT+lbdXgLAAEE6AMAAAToAwAAUEsBAh4DCgAAAAAAClhsTQAAAAAAAAAAAAAAAAUAGAAAAAAAAAAQAP1B0wAAAGRpcjIvVVQFAAM0T+lbdXgLAAEE6AMAAAToAwAAUEsFBgAAAAAEAAQANwEAABIBAAAAAA==");

    @Test
    public void testUnzipData() {
        Map<String, byte[]> data = unzipDataAsMap(ZIP_DATA_1);
        assertEquals(2, data.size());
        assertEquals("ciao\n", new String(data.get("file1.txt")));
        assertEquals("ciao2\n", new String(data.get("file2.txt")));
    }

}
