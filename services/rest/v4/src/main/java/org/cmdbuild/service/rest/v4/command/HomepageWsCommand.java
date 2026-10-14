/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.cmdbuild.homepage.HomepageData;
import org.cmdbuild.homepage.HomepageService;
import org.cmdbuild.service.rest.v4.model.WsHomepageData;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author mbuzzulini
 */
@Component
public class HomepageWsCommand {

    private final HomepageService homepageService;

    public HomepageWsCommand(HomepageService homepageService) {
        this.homepageService = checkNotNull(homepageService);
    }

    public HomepageData doRead(Supplier<HomepageData> function) {
        return function.get();
    }

    public HomepageData doReadOne(String idOrCode, Function<String, HomepageData> function) {
        return function.apply(idOrCode);
    }

    public List<HomepageData> doGetAll(Supplier<List<HomepageData>> function) {
        return function.get();
    }

    public HomepageData doCreate(WsHomepageData data) throws  IllegalArgumentException {
        HomepageData homepage = data.toHomepage().build();
        return homepageService.create(homepage);
    }

    public HomepageData doUpdate(Long id, WsHomepageData data) throws IllegalArgumentException {
        HomepageData homepage = data.toHomepage().withId(id).build();
        return homepageService.update(homepage);
    }
    public void doDelete(Long id) throws IllegalArgumentException {
        homepageService.delete(id);
    }
}
