/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.ifc.utils;

import jakarta.activation.DataSource;
import java.lang.invoke.MethodHandles;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.cmdbuild.utils.ifc.inner.Ifc2XktHelper;
import org.cmdbuild.utils.ifc.inner.Ifc2XktHelperImpl;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.lang.CmConcurrentUtils.lazyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for converting IFC data to XKT format using
 * {@link Ifc2XktHelper}.
 * <p>
 * Provides static methods to perform conversions from different input types.
 */
public class XktUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final LazyInitializer<Ifc2XktHelper> HELPER = lazyInitializer(Ifc2XktHelperImpl::new);

    /**
     * Converts IFC data to XKT format.
     *
     * @param ifc the IFC data as a {@link BigByteArray}
     * @return the converted XKT data as a {@link BigByteArray}
     * @throws IfcException if the conversion fails
     */
    public static BigByteArray ifc2Xkt(BigByteArray ifc) {
        try {
            return HELPER.get().ifc2Xkt(ifc);
        } catch (ConcurrentException ex) {
            LOGGER.error("Error instantiating helper");
            throw new IfcException(ex, "CMO 802: error during conversion");
        }
    }

    /**
     * Converts IFC data to XKT format.
     *
     * @param ifc the IFC data as a byte array
     * @return the converted XKT data as a byte array
     * @throws IfcException if the conversion fails
     */
    public static byte[] ifc2Xkt(byte[] ifc) {
        try {
            return HELPER.get().ifc2Xkt(ifc);
        } catch (ConcurrentException ex) {
            LOGGER.error("Error instantiating helper");
            throw new IfcException(ex, "CMO 802: error during conversion");
        }
    }

    /**
     * Converts IFC data to XKT format.
     *
     * @param ifc the IFC data as a {@link DataSource}
     * @return the converted XKT data as a byte array
     * @throws IfcException if the conversion fails
     */
    public static byte[] ifc2Xkt(DataSource ifc) {
        try {
            return HELPER.get().ifc2Xkt(ifc);
        } catch (ConcurrentException ex) {
            LOGGER.error("Error instantiating helper");
            throw new IfcException(ex, "CMO 802: error during conversion");
        }
    }
}
