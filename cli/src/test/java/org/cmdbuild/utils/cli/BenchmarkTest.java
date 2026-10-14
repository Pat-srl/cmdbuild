package org.cmdbuild.utils.cli;

import org.cmdbuild.utils.cli.commands.BenchmarkCommandRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	@Ignore("Issue #820")
	public void testBenchmark() throws Exception {
		new BenchmarkCommandRunner().exec(new String[]{"-m", "1", "-f"});
	}

}
