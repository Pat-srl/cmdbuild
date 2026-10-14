package org.cmdbuild.email.template;

import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.email.EmailCommonData;
import org.cmdbuild.report.ReportConfig;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;

public interface EmailTemplate extends EmailCommonData {

    final String EMAIL_TEMPLATE_CONFIG_SHOW_ON_CLASSES = "showOnClasses", LANG_EXPR_ATTR = "cm_lang_expr";
    final static String UPLOAD_ATTACHMENTS_FILTERS = "uploadAttachments", UPLOAD_ATTACHMENTS_ALL = "{}";

    @Nullable
    Long getId();

    String getCode();

    @Nullable
    String getDescription();

    List<String> getReportCodes();

    List<ReportConfig> getReports();

    /**
     *
     * @return
     * <dl><dt><code>filter.isFalse()</code> <dd>if no attachments to upload;
     * <dt><code>filter.isNoop()</code> <dd>if all attachments (no filter to
     * apply) to upload;
     * <dt>a initialized {@link CmFilter} <dd>otherwise;
     * </dl>
     */
    CmdbFilter getUploadAttachmentsFilter();

    default Map<String, String> getBindingParams() {
        return mapOf(String.class, String.class).accept(
                m -> {
                    list(getReports()).forEach(r -> {
                        r.getParams().entrySet().forEach(
                                p -> m.put(
                                        p.getKey(),
                                        p.getValue().toString()) // convert each param value to a String
                        );
                    });
                });
    }

    boolean isActive();

    default Set<String> getShowOnClasses() {
        return set(toListOfStrings(getMeta().get(EMAIL_TEMPLATE_CONFIG_SHOW_ON_CLASSES)));
    }

    default boolean hasReports() {
        return !getReports().isEmpty();
    }

    default boolean hasUploadAttachments() {
        return !getUploadAttachmentsFilter().isFalse(); // Has nothing to filter (Noop) or something to filter
    }

    default boolean hasTemplateAttachments() {
        return hasReports() || hasUploadAttachments(); // Has nothing to filter (Noop) or something to filter
    }

    @Nullable
    default String getLangExpr() {
        return getMeta(LANG_EXPR_ATTR);
    }

    default boolean hasLangExpr() {
        return isNotBlank(getLangExpr());
    }

}
