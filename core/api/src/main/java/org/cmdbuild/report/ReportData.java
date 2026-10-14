package org.cmdbuild.report;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import jakarta.annotation.Nullable;

public interface ReportData extends ReportInfo {

    @Nullable
    @Override
    Long getId();

    @Override
    String getCode();

    @Override
    String getDescription();

    String getQuery();

    List<String> getSourceReports();

    List<byte[]> getCompiledReports();

    List<byte[]> getImages();

    List<String> getImageNames();

    default boolean hasCompiledReports() {
        return !getCompiledReports().isEmpty();
    }

    default boolean hasSourceReports() {
        return !getSourceReports().isEmpty();
    }

    default byte[] getCompiledMasterReport() {
        checkArgument(hasCompiledReports());
        return getCompiledReports().get(0);
    }

    default List<byte[]> getCompiledSubReports() {
        checkArgument(hasCompiledReports());
        return getCompiledReports().subList(1, getCompiledReports().size());
    }

    default String getSourceMasterReport() {
        checkArgument(hasSourceReports());
        return getSourceReports().get(0);
    }

    default List<String> getSourceSubReports() {
        checkArgument(hasSourceReports());
        return getSourceReports().subList(1, getSourceReports().size());
    }

}
