/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sourcecode;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import static java.lang.String.format;
import java.time.LocalDate;
import java.time.Period;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.LoggerFactory;

public class ChangelogHelper {//TODO move this to other project

	public static void main(String[] args) {
		checkArgument(equal(args[0], "build-changelog"));
		try {
			File targetFile = new File(checkNotBlank(args[1]));
			String content = buildChangelogFile();
			writeToFile(targetFile, content);
		} catch (Exception ex) {
			LoggerFactory.getLogger(ChangelogHelper.class).error("error while building changelog", ex);
		}
	}

	private static String buildChangelogFile() {
		LocalDate since = LocalDate.now().minus(Period.ofWeeks(1));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (PrintStream out = new PrintStream(byteArrayOutputStream)) {
			out.printf("\n === CMDBUILD v3.0 CHANGELOG ===\n\nchanges since %s:\n\n", toIsoDate(since));//TODO attach also git info; TODO auto version; TODO ascii art banner
			out.println(executeProcess("/bin/bash", "-c", format("git log --format=' * %%s' --since=%s", toIsoDate(since)))); //TODO filter out empty commit messages
		}
		return byteArrayOutputStream.toString();
	}

}
