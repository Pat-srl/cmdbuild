package org.cmdbuild.common.utils;

import jakarta.activation.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class TempDataSourceTest {

    private final Context context;
    private final DataSource dataSource;

    public TempDataSourceTest(final Context context) throws IOException {
        checkNotNull(context);
        this.context = context;
        this.dataSource = (DataSource) TempDataSource.newInstance() //
                .withName(context.getName()) //
                .build();
    }

    @Parameters
    public static Collection<Object[]> data() {
        final Collection<Object[]> parameters = new ArrayList<>();

        parameters.add(new Object[]{new Context("test.txt", "text/plain")});
        parameters.add(new Object[]{new Context("test.text", "text/plain")});

        parameters.add(new Object[]{new Context("test.htm", "text/html")});
        parameters.add(new Object[]{new Context("test.html", "text/html")});

        parameters.add(new Object[]{new Context("test.gif", "image/gif")});

        parameters.add(new Object[]{new Context("test.ief", "image/ief")});

        parameters.add(new Object[]{new Context("test.jpeg", "image/jpeg")});
        parameters.add(new Object[]{new Context("test.jpg", "image/jpeg")});
        parameters.add(new Object[]{new Context("test.jpe", "image/jpeg")});

        parameters.add(new Object[]{new Context("test.tif", "image/tiff")});
        parameters.add(new Object[]{new Context("test.tiff", "image/tiff")});

        parameters.add(new Object[]{new Context("test.png", "image/png")});

        parameters.add(new Object[]{new Context("test.xwd", "image/x-xwindowdump")});

        parameters.add(new Object[]{new Context("test.ai", "application/postscript")});
        parameters.add(new Object[]{new Context("test.eps", "application/postscript")});
        parameters.add(new Object[]{new Context("test.ps", "application/postscript")});

        parameters.add(new Object[]{new Context("test.rtf", "application/rtf")});

        parameters.add(new Object[]{new Context("test.tex", "application/x-tex")});

        parameters.add(new Object[]{new Context("test.texinfo", "application/x-texinfo")});
        parameters.add(new Object[]{new Context("test.texi", "application/x-texinfo")});

        parameters.add(new Object[]{new Context("test.t", "application/x-troff")});
        parameters.add(new Object[]{new Context("test.tr", "application/x-troff")});
        parameters.add(new Object[]{new Context("test.roff", "application/x-troff")});

        parameters.add(new Object[]{new Context("test.au", "audio/basic")});

        parameters.add(new Object[]{new Context("test.midi", "audio/midi")});
        parameters.add(new Object[]{new Context("test.mid", "audio/midi")});

        parameters.add(new Object[]{new Context("test.aifc", "audio/x-aifc")});

        parameters.add(new Object[]{new Context("test.aif", "audio/x-aiff")});
        parameters.add(new Object[]{new Context("test.aiff", "audio/x-aiff")});

        // parameters.add(new Object[] { new Context("test.mpeg",
        // "audio/x-mpeg") });
        // parameters.add(new Object[] { new Context("test.mpg", "audio/x-mpeg")
        // });
        parameters.add(new Object[]{new Context("test.wav", "audio/x-wav")});

        parameters.add(new Object[]{new Context("test.mpeg", "video/mpeg")});
        parameters.add(new Object[]{new Context("test.mpg", "video/mpeg")});
        parameters.add(new Object[]{new Context("test.mpe", "video/mpeg")});

        parameters.add(new Object[]{new Context("test.qt", "video/quicktime")});
        parameters.add(new Object[]{new Context("test.mov", "video/quicktime")});

        parameters.add(new Object[]{new Context("test.avi", "video/x-msvideo")});

        parameters.add(new Object[]{new Context("test.pdf", "application/pdf")});

        return parameters;
    }

    @Test
    public void testGetName() {
        assertEquals(context.getName(), dataSource.getName());
    }

    @Test
    public void testGetContentType() {
        assertEquals(context.getContentType(), dataSource.getContentType());
    }

    private static class Context {

        private final String name;
        private final String contentType;

        public Context(final String name, final String contentType) {
            this.name = name;
            this.contentType = contentType;
        }

        public String getName() {
            return name;
        }

        public String getContentType() {
            return contentType;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).append(name).append(contentType).toString();
        }

    }

}
