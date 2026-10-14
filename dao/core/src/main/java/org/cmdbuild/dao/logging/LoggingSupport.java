package org.cmdbuild.dao.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public interface LoggingSupport {

	Logger logger = LoggerFactory.getLogger("persist");

}
