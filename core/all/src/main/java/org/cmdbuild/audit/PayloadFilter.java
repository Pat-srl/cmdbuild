package org.cmdbuild.audit;

public interface PayloadFilter {

    RequestData filterPayload(RequestData data);
}
