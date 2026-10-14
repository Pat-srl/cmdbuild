/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.homepage;

import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.ui.TargetDevice;

import javax.annotation.Nullable;
import java.util.List;

import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

public interface HomepageRepository {

    List<HomepageData> getAll();

    HomepageData getHomepageById(long id);

    HomepageData getHomepageByCode(String code);

    HomepageData createHomepage(HomepageData data);

    HomepageData updateHomepage(HomepageData data);

    @Nullable
    List<HomepageData> getHomepageDataForGroupOrNull(String groupName);

    void deleteHomepage(long id);

    default HomepageData getHomepageByIdOrCode(String idOrCode) {
        if (NumberUtils.isCreatable(idOrCode)) {
            return getHomepageById(toLong(idOrCode));
        } else {
            return getHomepageByCode(idOrCode);
        }
    }
}
