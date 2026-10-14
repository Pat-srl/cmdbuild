/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.cad.inner;

import jakarta.activation.DataSource;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;

/**
 * Helper interface for converting DWG files to DXF format.
 * <p>
 * Provides methods to convert DWG data from various input types (byte array,
 * {@link jakarta.activation.DataSource}, or {@link BigByteArray}) to DXF
 * format, returning the result as a byte array or {@link BigByteArray}.
 * Implementations may use local or remote conversion services.
 * </p>
 *
 * <p>
 * This interface extends {@link AutoCloseable} to allow resource cleanup if
 * needed.
 * </p>
 */
public interface Dwg2DxfHelper {

    /**
     * Converts the given DWG data to DXF format.
     *
     * @param dwg the DWG file as a {@link BigByteArray}
     * @return the converted DXF file as a {@link BigByteArray}
     */
    BigByteArray dwg2Dxf(BigByteArray dwg);

    /**
     * Converts the given DWG data (as a {@link jakarta.activation.DataSource})
     * to DXF format.
     *
     * @param dwg the DWG file as a {@link jakarta.activation.DataSource}
     * @return the converted DXF file as a byte array
     */
    default byte[] dwg2Dxf(DataSource dwg) {
        return Dwg2DxfHelper.this.dwg2Dxf(toBigByteArray(dwg)).toByteArray();
    }

    /**
     * Converts the given DWG data (as a byte array) to DXF format.
     *
     * @param dwg the DWG file as a byte array
     * @return the converted DXF file as a byte array
     */
    default byte[] dwg2Dxf(byte[] dwg) {
        return Dwg2DxfHelper.this.dwg2Dxf(new BigByteArray(dwg)).toByteArray();
    }
}
