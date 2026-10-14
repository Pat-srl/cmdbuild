/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad;

import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.US_ASCII;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.io.FilenameUtils.getExtension;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.cmdbuild.utils.cad.dxfparser.CadException;
import org.cmdbuild.utils.cad.dxfparser.DxfReader;
import org.cmdbuild.utils.cad.dxfparser.model.DxfDocument;
import org.cmdbuild.utils.cad.inner.Dwg2DxfHelper;
import org.cmdbuild.utils.cad.inner.Dwg2DxfHelperImpl;
import org.cmdbuild.utils.http.ExtServiceConfiguration;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.hasContentType;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.lang.CmConcurrentUtils.lazyInitializer;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.safeSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for working with CAD files, providing methods to detect DWG
 * version, and to parse DWG and DXF files into {@link DxfDocument} objects.
 * <p>
 * Supports conversion of DWG files to DXF format using a {@link Dwg2DxfHelper}
 * implementation, and handles various input types including {@link DataSource},
 * {@link InputStream}, and byte arrays.
 * </p>
 *
 * <p>
 * Typical usage:
 *
 * <pre>
 * DxfDocument doc = CadUtils.parseCadFile(dataSource);
 * String version = CadUtils.getDwgVersion(dataSource);
 * </pre>
 * </p>
 */
public class CadUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final LazyInitializer<Dwg2DxfHelper> HELPER = lazyInitializer(Dwg2DxfHelperImpl::new);

    /**
     * Attempts to detect the DWG version from the given {@link DataSource}.
     *
     * @param dwg the DWG file as a {@link DataSource}
     * @return the DWG version string, or {@code null} if not detected
     */
    @Nullable
    public static String getDwgVersion(DataSource dwg) {
        return getDwgVersion(safeSupplier(dwg::getInputStream));
    }

    /**
     * Attempts to detect the DWG version from the given {@link Supplier} of
     * {@link InputStream}.
     *
     * @param dwg a supplier of the DWG file input stream
     * @return the DWG version string, or {@code null} if not detected
     */
    @Nullable
    public static String getDwgVersion(Supplier<InputStream> dwg) {
        try {
            try (InputStream in = dwg.get()) {
                Matcher matcher = Pattern.compile("^AC[0-9]+").matcher(new String(in.readNBytes(1024), US_ASCII));
                if (matcher.find()) {
                    return matcher.group();
                } else {
                    return null;
                }
            }
        } catch (Exception ex) {
            LOGGER.debug("unable to read dwg version", ex);
            return null;
        }
    }

    /**
     * Parses a CAD file (DWG or DXF) from the given {@link DataSource},
     * automatically detecting the format.
     *
     * @param data the CAD file as a {@link DataSource}
     * @return the parsed {@link DxfDocument}
     */
    public static DxfDocument parseCadFile(DataSource data) {
        Dwg2DxfHelper dwg2DxfHelper;
        try {
            dwg2DxfHelper = HELPER.get();
            return parseCadFile(dwg2DxfHelper, data);
        } catch (ConcurrentException ex) {
            throw new CadException(ex, "CMO 801: error calling LazyInitializer of Dwg2DxfHelper");
        }
    }

    /**
     * Parses a CAD file (DWG or DXF) from the given {@link DataSource} using a
     * custom DWG-to-DXF configuration.
     *
     * @param dwg2DxfConfiguration the configuration for DWG-to-DXF conversion
     * @param data the CAD file as a {@link DataSource}
     * @return the parsed {@link DxfDocument}
     */
    public static DxfDocument parseCadFile(ExtServiceConfiguration dwg2DxfConfiguration, DataSource data) {
        Dwg2DxfHelper dwg2DxfHelper = new Dwg2DxfHelperImpl(dwg2DxfConfiguration);
        return parseCadFile(dwg2DxfHelper, data);
    }

    /**
     * Converts and parses a DWG file from the given {@link DataSource} using
     * the provided helper.
     *
     * @param dwg2DxfHelper the DWG-to-DXF conversion helper
     * @param dwg the DWG file as a {@link DataSource}
     * @return the parsed {@link DxfDocument}
     */
    public static DxfDocument parseDwgFile(Dwg2DxfHelper dwg2DxfHelper, DataSource dwg) {
        return parseDxfFile(dwg2DxfHelper.dwg2Dxf(dwg));
    }

    /**
     * Converts and parses a DWG file from the given {@link InputStream} using
     * the provided helper.
     *
     * @param dwg2DxfHelper the DWG-to-DXF conversion helper
     * @param dwg the DWG file as an {@link InputStream}
     * @return the parsed {@link DxfDocument}
     */
    public static DxfDocument parseDwgFile(Dwg2DxfHelper dwg2DxfHelper, InputStream dwg) {
        return parseDxfFile(dwg2DxfHelper.dwg2Dxf(toBigByteArray(dwg)));
    }

    /**
     * Parses a DXF file from the given byte array.
     *
     * @param dxf the DXF file as a byte array
     * @return the parsed {@link DxfDocument}
     */
    public static DxfDocument parseDxfFile(byte[] dxf) {
        return parseDxfFile(new BigByteArray(dxf));
    }

    /**
     * Parses a DXF file from the given {@link BigByteArray}.
     *
     * @param dxf the DXF file as a {@link BigByteArray}
     * @return the parsed {@link DxfDocument}
     */
    public static DxfDocument parseDxfFile(BigByteArray dxf) {
        return parseDxfFile(dxf.toInputStream());
    }

    /**
     * Parses a DXF file from the given {@link DataSource}.
     *
     * @param file the DXF file as a {@link DataSource}
     * @return the parsed {@link DxfDocument}
     * @throws CadException if an I/O error occurs
     */
    public static DxfDocument parseDxfFile(DataSource file) {
        try (InputStream in = file.getInputStream()) {
            return parseDxfFile(in);
        } catch (IOException ex) {
            throw new CadException(ex);
        }
    }

    /**
     * Parses a DXF file from the given {@link InputStream}.
     *
     * @param stream the DXF file as an {@link InputStream}
     * @return the parsed {@link DxfDocument}
     */
    public static DxfDocument parseDxfFile(InputStream stream) {
        return new DxfReader().readStream(new InputStreamReader(stream, StandardCharsets.UTF_8));// TODO charset
    }

    /**
     * Internal method to parse a CAD file using the provided helper and data
     * source, automatically detecting the file type by content type or
     * extension.
     *
     * @param dwg2DxfHelper the DWG-to-DXF conversion helper
     * @param data the CAD file as a {@link DataSource}
     * @return the parsed {@link DxfDocument}
     * @throws CadException if the file type cannot be detected
     */
    private static DxfDocument parseCadFile(Dwg2DxfHelper dwg2DxfHelper, DataSource data) {
        if (hasContentType(data, "image/vnd.dwg", "application/dwg")) {
            return parseDwgFile(dwg2DxfHelper, data);
        } else if (hasContentType(data, "image/vnd.dxf")) {
            return parseDxfFile(data);
        }
        return switch (getExtension(data.getName()).toLowerCase()) {
            case "dwg" ->
                parseDwgFile(dwg2DxfHelper, data);
            case "dxf" ->
                parseDxfFile(data);
            default ->
                throw new CadException("unable to detect type of cad file = %s", data);
        };
    }
}
