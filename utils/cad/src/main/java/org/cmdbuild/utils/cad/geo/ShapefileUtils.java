/*{
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.geo;

import static java.lang.String.format;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmZipUtils.buildZipFile;
import static org.cmdbuild.utils.io.CmZipUtils.unzipDataAsMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ShapefileUtils {

    public static BigByteArray renameShapefileInners(BigByteArray data, String name) {
        checkNotBlank(name);
        Map<String, BigByteArray> map = unzipDataAsMap(data);
        map = map(map).mapKeys(k -> format("%s/%s.%s", FilenameUtils.getPath(k), name, FilenameUtils.getExtension(k)));
        return buildZipFile(map);
    }

}
