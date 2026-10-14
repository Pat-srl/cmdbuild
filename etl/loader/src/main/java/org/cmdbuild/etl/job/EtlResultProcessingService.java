package org.cmdbuild.etl.job;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;

public interface EtlResultProcessingService {

    EtlResultProcessor getResultProcessor(EtlResultProcessingConfig config);

    @VisibleForTesting
    void overrideLogger(Logger logger);
}
