package org.cmdbuild.api.fluent;

import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

abstract class AbstractActiveCard extends CardImpl {

    private final FluentApiExecutor executor;
    protected final Map<String, Attachment> attachments = map();

    AbstractActiveCard(FluentApiExecutor executor, String className, Long id) {
        super(className, id);
        this.executor = executor;
    }

    protected FluentApiExecutor executor() {
        return executor;
    }

}
