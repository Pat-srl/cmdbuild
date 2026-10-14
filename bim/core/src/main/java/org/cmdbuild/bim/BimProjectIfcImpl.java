package org.cmdbuild.bim;

import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.utils.ifc.utils.IfcUtils.isPlainIfc;
import static org.cmdbuild.utils.io.CmCompressionUtils.deflate;
import static org.cmdbuild.utils.io.CmCompressionUtils.inflate;
import static org.cmdbuild.utils.io.CmIoUtils.isDataCompressed;
import org.cmdbuild.utils.lang.Builder;

@CardMapping("_BimProjectIfc")
public class BimProjectIfcImpl implements BimProjectIfc {

    private final Long id;
    private final String poid;
    private final byte[] ifcFile;

    private BimProjectIfcImpl(BimProjectIfcImpl.BimProjectIfcImplBuilder builder) {
        this.id = builder.id;
        this.poid = builder.poid;
        this.ifcFile = isPlainIfc(builder.ifcFile) || !isDataCompressed(builder.ifcFile) ? deflate(builder.ifcFile) : builder.ifcFile;
    }

    @Override
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr("ProjectId")
    public String getProjectId() {
        return poid;
    }

    @Override
    @CardAttr("IfcFile")
    public byte[] getIfcFile() {
        return ifcFile;
    }

    @Override
    public byte[] getIfcDecompressedFile() {
        return isPlainIfc(ifcFile) || !isDataCompressed(ifcFile) ? ifcFile : inflate(ifcFile);
    }

    public static BimProjectIfcImpl.BimProjectIfcImplBuilder builder() {
        return new BimProjectIfcImpl.BimProjectIfcImplBuilder();
    }

    public static BimProjectIfcImplBuilder copyOf(BimProjectIfc source) {
        return new BimProjectIfcImplBuilder()
                .withId(source.getId())
                .withProjectId(source.getProjectId())
                .withIfcFile(source.getIfcFile());
    }

    public static class BimProjectIfcImplBuilder implements Builder<BimProjectIfcImpl, BimProjectIfcImplBuilder> {

        private Long id;
        private String poid;
        private byte[] ifcFile;

        public BimProjectIfcImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public BimProjectIfcImplBuilder withProjectId(String projectId) {
            this.poid = projectId;
            return this;
        }

        public BimProjectIfcImplBuilder withIfcFile(byte[] ifcFile) {
            this.ifcFile = ifcFile;
            return this;
        }

        @Override
        public BimProjectIfcImpl build() {
            return new BimProjectIfcImpl(this);
        }

    }

}
