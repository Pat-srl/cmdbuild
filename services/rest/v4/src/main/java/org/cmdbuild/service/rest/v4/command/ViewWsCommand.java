/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import jakarta.activation.DataHandler;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.contextmenu.ContextMenuService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.formstructure.FormStructureService;
import org.cmdbuild.report.SysReportService;
import org.cmdbuild.service.rest.common.serializationhelpers.ContextMenuSerializationHelper;
import org.cmdbuild.service.rest.v4.model.WsViewData;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewImpl;
import org.cmdbuild.view.ViewService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ldare
 */
@Component
public class ViewWsCommand {

    private final OperationUserSupplier operationUserSupplier;
    private final ViewService viewService;
    private final FormStructureService formStructureService;
    private final ContextMenuService contextMenuService;
    private final ContextMenuSerializationHelper contextMenuSerializationHelper;
    private final SysReportService sysReportService;

    public ViewWsCommand(OperationUserSupplier operationUserSupplier, ViewService viewService, FormStructureService formStructureService, ContextMenuService contextMenuService, ContextMenuSerializationHelper contextMenuSerializationHelper, SysReportService sysReportService) {
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.viewService = checkNotNull(viewService);
        this.formStructureService = checkNotNull(formStructureService);
        this.contextMenuService = checkNotNull(contextMenuService);
        this.contextMenuSerializationHelper = checkNotNull(contextMenuSerializationHelper);
        this.sysReportService = checkNotNull(sysReportService);
    }

    public List<View> doGetMany(Supplier<List<View>> function) {
        return function.get();
    }

    public View doGetOne(String viewId, Function<String, View> function) {
        return function.apply(viewId);
    }

    public Collection<Attribute> doGetAttributes(String viewId, Function<String, View> function) {
        View view = function.apply(viewId);
        return viewService.getAttributesForView(view);
    }

    public View doCreate(WsViewData data) {
        View view = viewService.createForCurrentUser(data.toView().accept(setCurrentUser(data)).build());
        formStructureService.setFormForView(view, data.getFormStructure());
        contextMenuService.updateContextMenuItemsForView(view, contextMenuSerializationHelper.toContextMenuItems(data.getContextMenuItems()));
        return view;
    }

    public View doUpdate(String viewId, WsViewData data) {
        View view = viewService.updateForCurrentUser(data.toView().accept(setViewId(viewId)).accept(setCurrentUser(data)).build());//TODO use viewId ???
        formStructureService.setFormForView(view, data.getFormStructure());
        contextMenuService.updateContextMenuItemsForView(view, contextMenuSerializationHelper.toContextMenuItems(data.getContextMenuItems()));
        return view;
    }

    public void doDelete(String viewId, Function<String, View> function) {
        viewService.delete((function.apply(viewId)).getId());
    }

    public DataHandler doPrintView(String viewId, String filterStr, String sort, Long limit, Long offset, String extension, String attributes) {
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder().withFilter(filterStr).withSorter(sort).withPaging(offset, limit).withAttrs(isBlank(attributes) ? null : fromJson(attributes, LIST_OF_STRINGS)).build();
        return sysReportService.executeViewReport(viewService.getForCurrentUserByNameOrId(viewId), reportExtFromString(extension), queryOptions);
    }

    private Consumer<ViewImpl.ViewImplBuilder> setViewId(String viewId) {
        return (b) -> {
            if (NumberUtils.isCreatable(viewId)) {
                b.withId(toLong(viewId));
            } else {
                b.withName(viewId);
            }
        };
    }

    private Consumer<ViewImpl.ViewImplBuilder> setCurrentUser(WsViewData data) {
        return (b) -> {
            if (!data.getShared()) {
                b.withUserId(operationUserSupplier.getUser().getLoginUser().getId());
            }
        };
    }
}
