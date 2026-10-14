/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ifc.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.stream;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import static java.util.function.Function.identity;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.EnumUtils;
import org.bimserver.emf.IdEObject;
import org.bimserver.emf.PackageMetaData;
import org.bimserver.emf.Schema;
import org.bimserver.ifc.BasicIfcModel;
import org.bimserver.ifc.step.deserializer.DetectIfcVersion;
import org.bimserver.models.ifc2x3tc1.Ifc2x3tc1Package;
import org.bimserver.models.ifc4.Ifc4Package;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.cmdbuild.utils.ifc.IfcEntry;
import org.cmdbuild.utils.ifc.IfcFeature;
import org.cmdbuild.utils.ifc.IfcModel;
import org.cmdbuild.utils.ifc.inner.IfcLoader;
import org.cmdbuild.utils.ifc.inner.IfcModelImpl;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.isZip;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import org.cmdbuild.utils.io.StreamProgressListener;
import static org.cmdbuild.utils.lang.CmMapUtils.lazyMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfcUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static IfcModel loadIfc(InputStream in) {
        return loadIfc(toBigByteArray(in));
    }

    public static IfcModel loadIfc(byte[] data) {
        return loadIfc(new BigByteArray(data));
    }

    public static IfcModel loadIfc(BigByteArray data) {
        return new IfcLoader(newDataSource(data)).loadIfc();
    }

    public static IfcModel loadIfc(DataSource data, StreamProgressListener progressListener) {
        return new IfcLoader(data).withProgressListener(progressListener).loadIfc();
    }

    public static IfcModel loadIfc(DataSource data) {
        return new IfcLoader(data).loadIfc();
    }

    public static IfcModel emptyModel() {
        return emptyModel(Schema.IFC2X3TC1);
    }

    public static IfcModel emptyModel(Schema schema) {
        return new IfcModelImpl(new BasicIfcModel(buildPackageMetadata(schema), null, 0));
    }

    public static PackageMetaData buildPackageMetadata(Schema schema) {
        return switch (schema) {
            case IFC2X3TC1 ->
                new PackageMetaData(Ifc2x3tc1Package.eINSTANCE, schema, CmIoUtils.cmTmpDir().toPath());
            case IFC4 ->
                new PackageMetaData(Ifc4Package.eINSTANCE, schema, CmIoUtils.cmTmpDir().toPath());
            default ->
                throw new UnsupportedOperationException("unsupported schema = " + schema);
        };
    }

    public static Map<String, IfcFeature> getFeatures(EClass eclass) {
        return eclass.getEAllStructuralFeatures().stream().map(IfcFeatureImpl::new).collect(toMap(IfcFeature::getName, identity()));
    }

    public static JXPathContext buildJXPathContext(IfcModel model) {
        return JXPathContext.newContext(new MyDynaBean(() -> model.getClasses().stream().collect(toMap(identity(), c -> model.getEntries(c).stream().map(IfcUtils::toDynaBean).collect(toImmutableList()))), model));
    }

    public static JXPathContext buildJXPathContext(IfcEntry entry) {
        return JXPathContext.newContext(toDynaBean(entry));
    }

    public static Object toDynaBean(IfcEntry entry) {
        return new MyDynaBean(() -> map(entry.asMap()).mapValues(IfcUtils::toDynaBeanValue), entry);
    }

    public static Schema detectSchema(DataSource data) {
        try (InputStream in = data.getInputStream()) {
            return detectSchema(in.readAllBytes(), isZip(data));
        } catch (Exception ex) {
            throw new IfcException(ex, "error detecting ifc schema from header");
        }
    }

    @Nullable
    public static Object toDynaBeanValue(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof IfcEntry ifcEntry) {
            return toDynaBean(ifcEntry);
        } else if (value instanceof Iterable iterable) {
            return stream(iterable).map(IfcUtils::toDynaBeanValue).collect(toList());
        } else {
            return value;
        }
    }

    @Nullable
    public static Object fromDynaBeanValue(@Nullable Object value) {
        if (value != null && value instanceof MyDynaBean myDynaBean) {
            return myDynaBean.getInner();
        } else if (value instanceof Iterable iterable) {
            return stream(iterable).map(IfcUtils::fromDynaBeanValue).collect(toList());
        } else {
            return value;
        }
    }

    public static boolean isPlainIfc(byte[] data) {
        return new String(data, 0, 15, StandardCharsets.UTF_8).startsWith("ISO-10303-21");
    }

    private static Schema detectSchema(byte[] in, boolean isZipped) throws IOException, DeserializeException {
        String ifcSchemaVersion = new DetectIfcVersion().detectVersion(in, isZipped);
        LOGGER.debug("found ifc header schema version =< {} > from {}", ifcSchemaVersion, isZipped ? "zipped data" : "raw data");

        return checkNotNull(map(EnumUtils.getEnumList(Schema.class), e -> e.getHeaderName().toLowerCase(), identity()).get(ifcSchemaVersion.toLowerCase()),
                "unsupported ifc header schema version =< %s >", ifcSchemaVersion);
    }

    private final static class MyDynaBean extends LazyDynaMap {

        private final Object inner;

        public MyDynaBean(Supplier<Map<String, Object>> supplier, Object inner) {
            super(lazyMap(supplier));
            this.inner = checkNotNull(inner);
            setRestricted(true);
            setReturnNull(true);
        }

        public Object getInner() {
            return inner;
        }

    }

    private static class IfcFeatureImpl implements IfcFeature {

        private final String name, type;
        private final boolean isArray, isEntry;

        public IfcFeatureImpl(EStructuralFeature inner) {
            this.name = checkNotBlank(inner.getName());
            isArray = inner.getUpperBound() != 1;
            Class genericType = inner.getEType().getInstanceClass();
            if (IdEObject.class.isAssignableFrom(genericType)) {
                isEntry = true;
                type = inner.getEType().getName();
            } else {
                isEntry = false;
                type = genericType.getSimpleName();
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public boolean isArray() {
            return isArray;
        }

        @Override
        public boolean isEntry() {
            return isEntry;
        }

        @Override
        public String toString() {
            return "IfcFeature{" + "name=" + name + ", type=" + type + (isArray ? "[]" : "") + '}';
        }

    }

}
