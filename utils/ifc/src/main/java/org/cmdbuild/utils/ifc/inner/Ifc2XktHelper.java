/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.ifc.inner;

import jakarta.activation.DataSource;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;

/**
 * Helper interface for converting IFC files to XKT format.
 * <p>
 * Provides methods to convert IFC data from various input types to XKT format.
 */
public interface Ifc2XktHelper {

    /**
     * Converts IFC data to XKT format.
     *
     * @param ifc the IFC data as a {@link BigByteArray}
     * @return the converted XKT data as a {@link BigByteArray}
     */
    BigByteArray ifc2Xkt(BigByteArray ifc);

    /**
     * Converts IFC data to XKT format.
     *
     * @param ifc the IFC data as a byte array
     * @return the converted XKT data as a byte array
     */
    default byte[] ifc2Xkt(byte[] ifc) {
        return Ifc2XktHelper.this.ifc2Xkt(new BigByteArray(ifc)).toByteArray();
    }

    /**
     * Converts IFC data to XKT format.
     *
     * @param ifc the IFC data as a {@link DataSource}
     * @return the converted XKT data as a byte array
     */
    default byte[] ifc2Xkt(DataSource ifc) {
        return Ifc2XktHelper.this.ifc2Xkt(toBigByteArray(ifc)).toByteArray();
    }

}
