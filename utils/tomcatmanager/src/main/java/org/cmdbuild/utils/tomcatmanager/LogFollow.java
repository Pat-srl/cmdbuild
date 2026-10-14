/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.tomcatmanager;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Long.max;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFollow implements LogManager {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(namedThreadFactory(getClass()));
	private final File file;
	private long pos = 0;

	public LogFollow(File file) {
		checkArgument(file != null);
		this.file = file;
	}

	public void startFollowingLog() {
		logger.info("startFollowingLog file = {}", file.getAbsolutePath());
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				if (Thread.currentThread().isInterrupted()) {
					//do nothing, return
				} else {
					followLog();
					executorService.schedule(this, 100, TimeUnit.MILLISECONDS);
				}
			}
		});
	}

	private synchronized void followLog() {
		if (!file.isFile()) {
			//do nothing, wait for file
		} else {
			long size = file.length();
			if (size < pos || pos == 0) {
				//file size decreased, reset pos
				pos = max(0, size - 1000);
			}
			try {
				byte[] data;
				try (FileInputStream in = new FileInputStream(file)) {
					in.skip(pos);
					data = IOUtils.toByteArray(in);
					pos += data.length;
				}
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)))) {
					String line;
					while ((line = reader.readLine()) != null) {
						logger.info("LOGFILE={} {}", file.getName(), line);
					}
				}
			} catch (IOException ex) {
				logger.error("error", ex);
				pos = 0;
			}
		}
	}

	public void stopFollowingLog() {
		logger.info("stopFollowingLog file = {}", file.getAbsolutePath());
		executorService.shutdown();
		try {
			boolean hasTerminated = executorService.awaitTermination(2, TimeUnit.SECONDS);
			checkArgument(hasTerminated);
		} catch (InterruptedException ex) {
		}
	}

	@Override
	public void flushLogs() {
		try {
			Thread.sleep(100); //give time to tomcat to flush logs
		} catch (InterruptedException ex) {
		}
		followLog();
	}
}
